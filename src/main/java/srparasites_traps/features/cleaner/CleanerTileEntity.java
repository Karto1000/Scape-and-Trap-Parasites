package srparasites_traps.features.cleaner;

import cofh.core.block.TileCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.StateManager;

import javax.annotation.Nullable;
import java.util.List;

public class CleanerTileEntity extends TileCore implements ITickable {
    private final static int DEFAULT_CLEANER_CHECK_TICKS_COOLDOWN = 10;
    private final static int DEFAULT_CLEANER_CAPACITY = 10;
    private int currentCheckCooldown = 0;
    private int currentCapacity = DEFAULT_CLEANER_CAPACITY;

    private final StateManager<CleanerState> state = new StateManager<>(CleanerState.IDLE);
    private EntityPlayer assignedPlayer;

    public CleanerTileEntity() {
        super();
    }

    public CleanerState getState() {
        return state.getState();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("State", this.state.getState().ordinal());
        compound.setInteger("CurrentCapacity", this.currentCapacity);
        compound.setInteger("CurrentCheckCooldown", this.currentCheckCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.currentCheckCooldown = NBTHelper.getIntegerOrElse(compound, "CurrentCheckCooldown", () -> DEFAULT_CLEANER_CHECK_TICKS_COOLDOWN);
        this.currentCapacity = NBTHelper.getIntegerOrElse(compound, "CurrentCapacity", () -> DEFAULT_CLEANER_CAPACITY);
        this.state.setState(CleanerState.values()[NBTHelper.getIntegerOrElse(compound, "State", CleanerState.IDLE::ordinal)]);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    private boolean isAssignedPlayerValid() {
        if (this.assignedPlayer == null) return false;
        if (!this.assignedPlayer.isEntityAlive()) return false;
        double distance = Math.sqrt(this.getPos().distanceSq(assignedPlayer.posX, assignedPlayer.posY, assignedPlayer.posZ));
        return distance <= 2;
    }

    @Override
    public void update() {
        if (this.world.isRemote) return;

        boolean canRegainCapacity = this.world.getTotalWorldTime() % 20 == 0 && this.currentCapacity < DEFAULT_CLEANER_CAPACITY;
        if (canRegainCapacity) this.currentCapacity++;

        switch (state.getState()) {
            case IDLE:
                if (this.currentCheckCooldown > 0) {
                    this.currentCheckCooldown--;
                    return;
                }

                if (this.currentCapacity <= 0) return;

                AxisAlignedBB aabb = new AxisAlignedBB(this.getPos()).grow(1, 1, 1);
                List<EntityPlayer> entities = this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
                this.currentCheckCooldown = DEFAULT_CLEANER_CHECK_TICKS_COOLDOWN;
                if (entities.isEmpty()) return;
                this.state.setState(CleanerState.DISPENSING, this.world.getTotalWorldTime());
                this.assignedPlayer = entities.get(this.world.rand.nextInt(entities.size()));
                this.callBlockUpdate();
                break;
            case DISPENSING:
                if (currentCapacity <= 0 || !isAssignedPlayerValid()) {
                    this.state.setState(CleanerState.IDLE, this.world.getTotalWorldTime());
                    this.callBlockUpdate();
                    return;
                }

                currentCapacity--;
                break;
            default:
        }
    }
}
