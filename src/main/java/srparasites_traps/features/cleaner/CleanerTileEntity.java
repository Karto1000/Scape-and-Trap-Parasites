package srparasites_traps.features.cleaner;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
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
    private final static int DEFAULT_CLEANER_OPEN_DURATION_TICKS = 40;
    private final static int DEFAULT_CLEANER_CLOSE_DURATION_TICKS = 40;
    private final static int DEFAULT_CLEANER_CAPACITY_REGEN_TIME_TICKS = 100;
    private int currentCheckCooldown = 0;
    private int currentSprayCooldown = 0;
    private int currentOpenCooldown = DEFAULT_CLEANER_OPEN_DURATION_TICKS;
    private int currentCloseCooldown = DEFAULT_CLEANER_CLOSE_DURATION_TICKS;
    private int currentCapacity = DEFAULT_CLEANER_CAPACITY;
    private final static Potion[] removableEffects = new Potion[]{
            SRPPotions.VIRA_E,
            SRPPotions.COTH_E
    };

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

    private EnumFacing getGrateFacingDirection() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof CleanerBlock) return state.getValue(CleanerBlock.grateDirection);
        return EnumFacing.NORTH;
    }

    private List<EntityPlayer> getPlayersInRange() {
        EnumFacing facing = this.getGrateFacingDirection();
        Vec3i dirVec = facing.getDirectionVec();
        AxisAlignedBB aabb = new AxisAlignedBB(this.getPos())
                .offset(dirVec.getX(), dirVec.getY(), dirVec.getZ())
                .expand(dirVec.getX(), dirVec.getY(), dirVec.getZ());
        return this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
    }

    private void shootParticles() {
        EnumFacing facing = this.getGrateFacingDirection();
        Vec3i dirVec = facing.getDirectionVec();

        double originX = pos.getX() + 0.5 + (double) dirVec.getX() / 2;
        double originY = pos.getY() + 0.5 + (double) dirVec.getY() / 2;
        double originZ = pos.getZ() + 0.5 + (double) dirVec.getZ() / 2;

        for (int i = 0; i < 10; i++) {
            ((WorldServer) this.world).spawnParticle(
                    EnumParticleTypes.CLOUD,
                    originX,
                    originY,
                    originZ,
                    0,
                    dirVec.getX(),
                    dirVec.getY(),
                    dirVec.getZ(),
                   Math.random()
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
                        0.4f,
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
                        0.4f,
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

    private boolean anyPlayerHasBadEffect(List<EntityPlayer> players) {
        return players.stream().anyMatch(player -> {
            for (Potion effect : removableEffects) {
                if (player.isPotionActive(effect)) return true;
            }

            return false;
        });
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

                if (!anyPlayerHasBadEffect(players)) return;

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

                if (!anyPlayerHasBadEffect(playersInRange)) {
                    this.switchState();
                    this.callBlockUpdate();
                    return;
                }

                for (EntityPlayer player : playersInRange) {
                    for (Potion effect : removableEffects) player.removePotionEffect(effect);
                }

                shootParticles();
                this.world.playSound(
                        null,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.5,
                        this.pos.getZ() + 0.5,
                        ModSounds.CLEANER_SPRAY,
                        SoundCategory.BLOCKS,
                        0.4f,
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
                this.currentOpenCooldown = DEFAULT_CLEANER_OPEN_DURATION_TICKS;
                break;
            case CLOSING:
                if (this.currentCloseCooldown > 0) {
                    this.currentCloseCooldown--;
                    return;
                }

                this.switchState();
                this.callBlockUpdate();
                this.currentCloseCooldown = DEFAULT_CLEANER_CLOSE_DURATION_TICKS;
                break;
            default:
        }
    }
}
