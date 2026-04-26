package srparasites_traps.features.cleaner;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import srparasites_traps.registry.ModSounds;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.StateManager;

import javax.annotation.Nullable;
import java.util.List;

public class CleanerTileEntity extends TileCore implements ITickable {
    private final static int DEFAULT_CLEANER_CHECK_TICKS_COOLDOWN = 10;
    private final static int DEFAULT_CLEANER_CAPACITY = 3;
    private final static int DEFAULT_CLEANER_SPRAY_COOLDOWN_TICKS = 20;
    private final static int DEFAULT_CLEANER_OPEN_COOLDOWN_TICKS = 10;
    private final static int DEFAULT_CLEANER_CLOSE_COOLDOWN_TICKS = 10;
    private final static int DEFAULT_CLEANER_CAPACITY_REGEN_TIME_TICKS = 100;
    private int currentCheckCooldown = 0;
    private int currentSprayCooldown = 0;
    private int currentOpenCooldown = 0;
    private int currentCloseCooldown = 0;
    private int currentCapacity = DEFAULT_CLEANER_CAPACITY;

    private final StateManager<CleanerState> state = new StateManager<>(CleanerState.IDLE);

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

    private List<EntityPlayer> getPlayersInRange() {
        AxisAlignedBB aabb = new AxisAlignedBB(this.getPos()).grow(1, 1, 1);
        return this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
    }

    private void shootParticles() {
        // Shoot particles in a hexagonal pattern outwards
        double originX = pos.getX() + 0.5;
        double originY = pos.getY() + 0.5;
        double originZ = pos.getZ() + 0.5;

        for (int i = 0; i < 6; i++) {
            double spinAngle = (i * 2 * Math.PI) / 6 * Math.random();
            double x = Math.cos(spinAngle) * 0.2;
            double z = Math.sin(spinAngle) * 0.2;

            ((WorldServer) this.world).spawnParticle(
                    EnumParticleTypes.CLOUD,
                    originX,
                    originY,
                    originZ,
                    0,
                    x,
                    0.5,
                    z,
                    0.5
            );
        }
    }

    private void switchState() {
        switch (this.state.getState()) {
            case IDLE:
                this.world.playSound(
                        null,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.5,
                        this.pos.getZ() + 0.5,
                        ModSounds.CLEANER_OPEN,
                        SoundCategory.BLOCKS,
                        0.2f,
                        1.0f
                );
                this.state.setState(CleanerState.OPENING, this.world.getTotalWorldTime());
                break;
            case DISPENSING:
                this.world.playSound(
                        null,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.5,
                        this.pos.getZ() + 0.5,
                        ModSounds.CLEANER_CLOSE,
                        SoundCategory.BLOCKS,
                        0.2f,
                        1.0f
                );
                this.state.setState(CleanerState.CLOSING, this.world.getTotalWorldTime());
                break;
            case OPENING:
                this.state.setState(CleanerState.DISPENSING, this.world.getTotalWorldTime());
                break;
            case CLOSING:
                this.state.setState(CleanerState.IDLE, this.world.getTotalWorldTime());
                break;
        }
    }

    @Override
    public void update() {
        if (this.world.isRemote) return;

        boolean canRegainCapacity = this.world.getTotalWorldTime() % DEFAULT_CLEANER_CAPACITY_REGEN_TIME_TICKS == 0
                && this.currentCapacity < DEFAULT_CLEANER_CAPACITY;
        if (canRegainCapacity) this.currentCapacity++;

        switch (state.getState()) {
            case IDLE:
                if (this.currentCheckCooldown > 0) {
                    this.currentCheckCooldown--;
                    return;
                }

                if (this.currentCapacity <= 0) return;

                List<EntityPlayer> players = this.getPlayersInRange();
                this.currentCheckCooldown = DEFAULT_CLEANER_CHECK_TICKS_COOLDOWN;
                if (players.isEmpty()) return;
                this.switchState();
                this.callBlockUpdate();
                break;
            case DISPENSING:
                if (this.currentSprayCooldown > 0) {
                    this.currentSprayCooldown--;
                    return;
                }

                List<EntityPlayer> playersInRange = this.getPlayersInRange();

                if (currentCapacity <= 0 || playersInRange.isEmpty()) {
                    this.switchState();
                    this.callBlockUpdate();
                    return;
                }

                for (EntityPlayer player : playersInRange) {
                    player.removePotionEffect(SRPPotions.VIRA_E);
                }

                shootParticles();
                this.world.playSound(
                        null,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.5,
                        this.pos.getZ() + 0.5,
                        ModSounds.CLEANER_SPRAY,
                        SoundCategory.BLOCKS,
                        0.2f,
                        1.0f
                );

                currentCapacity--;
                currentSprayCooldown = DEFAULT_CLEANER_SPRAY_COOLDOWN_TICKS;
                break;
            case OPENING:
                if (this.currentOpenCooldown > 0) {
                    this.currentOpenCooldown--;
                    return;
                }

                this.switchState();
                this.callBlockUpdate();
                this.currentOpenCooldown = DEFAULT_CLEANER_OPEN_COOLDOWN_TICKS;
                break;
            case CLOSING:
                if (this.currentCloseCooldown > 0) {
                    this.currentCloseCooldown--;
                    return;
                }

                this.switchState();
                this.callBlockUpdate();
                this.currentCloseCooldown = DEFAULT_CLEANER_CLOSE_COOLDOWN_TICKS;
                break;
            default:
        }
    }
}
