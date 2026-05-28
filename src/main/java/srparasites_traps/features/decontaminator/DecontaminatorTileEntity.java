package srparasites_traps.features.decontaminator;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModSounds;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.StateManager;

import javax.annotation.Nullable;
import java.util.List;

// We can't inherit from TileCore since that doesn't allow the update method to be called on the client
public class DecontaminatorTileEntity extends TileEntity implements ITickable {
    public final static int DEFAULT_DECONTAMINATOR_CAPACITY = ForgeConfigHandler.decontaminator.DEFAULT_DECONTAMINATOR_SPRAY_CAPACITY;
    public final static int DEFAULT_DECONTAMINATOR_SPRAY_COOLDOWN_TICKS = ForgeConfigHandler.decontaminator.DEFAULT_DECONTAMINATOR_SPRAY_COOLDOWN_TICKS;
    public final static int DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS = ForgeConfigHandler.decontaminator.DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS;
    public final static int DEFAULT_DECONTAMINATOR_CLOSE_DURATION_TICKS = ForgeConfigHandler.decontaminator.DEFAULT_DECONTAMINATOR_CLOSE_DURATION_TICKS;
    public final static int DEFAULT_DECONTAMINATOR_CAPACITY_REGEN_TIME_TICKS = ForgeConfigHandler.decontaminator.DEFAULT_DECONTAMINATOR_CAPACITY_REGEN_TIME_TICKS;
    public final static int ANIMATION_FRAMES = 6;

    private final static int DEFAULT_DECONTAMINATOR_CHECK_TICKS_COOLDOWN = 10;
    private int currentCheckCooldown = 0;
    private int currentSprayCooldown = 0;
    private int currentOpenCooldown = DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS;
    private int currentCloseCooldown = DEFAULT_DECONTAMINATOR_CLOSE_DURATION_TICKS;
    private int currentCapacity = DEFAULT_DECONTAMINATOR_CAPACITY;
    private final static Potion[] removableEffects = new Potion[]{
            SRPPotions.VIRA_E,
            SRPPotions.COTH_E
    };
    private void onSwitchState(DecontaminatorState lastState, DecontaminatorState newState) {
        switch (lastState) {
            case IDLE:
                this.world.playSound(
                        null,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.5,
                        this.pos.getZ() + 0.5,
                        ModSounds.DECONTAMINATOR_OPEN,
                        SoundCategory.BLOCKS,
                        0.4f,
                        1.0f
                );
                this.callBlockUpdate();
                break;
            case DISPENSING:
                this.world.playSound(
                        null,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.5,
                        this.pos.getZ() + 0.5,
                        ModSounds.DECONTAMINATOR_CLOSE,
                        SoundCategory.BLOCKS,
                        0.4f,
                        1.0f
                );
                this.callBlockUpdate();
                break;
            case OPENING:
            case CLOSING:
                this.callBlockUpdate();
                break;
        }
    }

    private final StateManager<DecontaminatorState> state = new StateManager<>(DecontaminatorState.IDLE, this::onSwitchState);

    public DecontaminatorTileEntity() {
        super();
    }

    public DecontaminatorState getState() {
        return state.getState();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("State", this.state.getState().ordinal());
        compound.setInteger("CurrentCapacity", this.currentCapacity);
        compound.setInteger("CurrentCheckCooldown", this.currentCheckCooldown);
        compound.setInteger("CurrentSprayCooldown", this.currentSprayCooldown);
        compound.setInteger("CurrentOpenCooldown", this.currentOpenCooldown);
        compound.setInteger("CurrentCloseCooldown", this.currentCloseCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.currentCheckCooldown = NBTHelper.getIntegerOrElse(compound, "CurrentCheckCooldown", () -> DEFAULT_DECONTAMINATOR_CHECK_TICKS_COOLDOWN);
        this.currentCapacity = NBTHelper.getIntegerOrElse(compound, "CurrentCapacity", () -> DEFAULT_DECONTAMINATOR_CAPACITY);
        this.state.setState(DecontaminatorState.values()[NBTHelper.getIntegerOrElse(compound, "State", DecontaminatorState.IDLE::ordinal)]);
        this.currentSprayCooldown = NBTHelper.getIntegerOrElse(compound, "CurrentSprayCooldown", () -> DEFAULT_DECONTAMINATOR_SPRAY_COOLDOWN_TICKS);
        this.currentOpenCooldown = NBTHelper.getIntegerOrElse(compound, "CurrentOpenCooldown", () -> DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS);
        this.currentCloseCooldown = NBTHelper.getIntegerOrElse(compound, "CurrentCloseCooldown", () -> DEFAULT_DECONTAMINATOR_CLOSE_DURATION_TICKS);
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

    public int getAnimationFrame() {
        if (this.state.getState() == DecontaminatorState.OPENING) {
            int elapsedTicks = DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS - this.currentOpenCooldown;
            int frame = elapsedTicks / (DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS / ANIMATION_FRAMES);
            return Math.max(0, Math.min(ANIMATION_FRAMES - 1, frame));
        }

        if (this.state.getState() == DecontaminatorState.CLOSING) {
            int frame = this.currentCloseCooldown / (DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS / ANIMATION_FRAMES);
            return Math.max(0, Math.min(ANIMATION_FRAMES - 1, frame));
        }

        if (this.state.getState() == DecontaminatorState.DISPENSING) {
            return ANIMATION_FRAMES - 1;
        }

        return -1;
    }

    public EnumFacing getGrateFacingDirection() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof DecontaminatorBlock) return state.getValue(DecontaminatorBlock.grateDirection);
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
                    dirVec.getX() + (dirVec.getX() == 0 ? (Math.random() - 0.5) : 0),
                    dirVec.getY() + (dirVec.getY() == 0 ? (Math.random() - 0.5) : 0),
                    dirVec.getZ() + (dirVec.getZ() == 0 ? (Math.random() - 0.5) : 0),
                    Math.random()
            );
        }
    }

    public void callBlockUpdate() {
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
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
        if (this.world.isRemote) {
            if (state.getState() == DecontaminatorState.OPENING && this.currentOpenCooldown > 0) {
                this.currentOpenCooldown--;
            } else if (state.getState() == DecontaminatorState.CLOSING && this.currentCloseCooldown > 0) {
                this.currentCloseCooldown--;
            }
            return;
        }

        boolean canRegainCapacity = this.world.getTotalWorldTime() % DEFAULT_DECONTAMINATOR_CAPACITY_REGEN_TIME_TICKS == 0
                && this.currentCapacity < DEFAULT_DECONTAMINATOR_CAPACITY;
        if (canRegainCapacity) this.currentCapacity++;

        switch (state.getState()) {
            case IDLE:
                if (this.currentCheckCooldown > 0) {
                    this.currentCheckCooldown--;
                    return;
                }

                if (this.currentCapacity <= 0) return;

                List<EntityPlayer> players = this.getPlayersInRange();
                this.currentCheckCooldown = DEFAULT_DECONTAMINATOR_CHECK_TICKS_COOLDOWN;
                if (players.isEmpty()) return;
                if (!anyPlayerHasBadEffect(players)) return;

                this.state.switchState(this.world.getTotalWorldTime());
                break;
            case DISPENSING:
                if (this.currentSprayCooldown > 0) {
                    this.currentSprayCooldown--;
                    return;
                }

                List<EntityPlayer> playersInRange = this.getPlayersInRange();

                if (currentCapacity <= 0 || playersInRange.isEmpty()) {
                    this.state.switchState(this.world.getTotalWorldTime());
                    return;
                }

                if (!anyPlayerHasBadEffect(playersInRange)) {
                    this.state.switchState(this.world.getTotalWorldTime());
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
                        ModSounds.DECONTAMINATOR_SPRAY,
                        SoundCategory.BLOCKS,
                        0.4f,
                        1.0f
                );

                currentCapacity--;
                currentSprayCooldown = DEFAULT_DECONTAMINATOR_SPRAY_COOLDOWN_TICKS;
                break;
            case OPENING:
                if (this.currentOpenCooldown > 0) {
                    this.currentOpenCooldown--;
                    return;
                }

                this.currentOpenCooldown = DEFAULT_DECONTAMINATOR_OPEN_DURATION_TICKS;
                this.state.switchState(this.world.getTotalWorldTime());
                break;
            case CLOSING:
                if (this.currentCloseCooldown > 0) {
                    this.currentCloseCooldown--;
                    return;
                }

                this.currentCloseCooldown = DEFAULT_DECONTAMINATOR_CLOSE_DURATION_TICKS;
                this.state.switchState(this.world.getTotalWorldTime());
                break;
            default:
        }
    }

}
