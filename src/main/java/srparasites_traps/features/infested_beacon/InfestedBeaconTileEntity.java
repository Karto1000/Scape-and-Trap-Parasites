package srparasites_traps.features.infested_beacon;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModPotions;
import srparasites_traps.registry.ModSounds;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.UpdateLimiter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class InfestedBeaconTileEntity extends TileCore implements ITickable {
    public static final int range = ForgeConfigHandler.infestedBeacon.RANGE;
    public static final int effectDuration = ForgeConfigHandler.infestedBeacon.EFFECTS_DURATION;
    public static final int maxDamageReduction = ForgeConfigHandler.infestedBeacon.MAX_DAMAGE_REDUCTION_PERCENT;
    public static final int maxLevel = ForgeConfigHandler.infestedBeacon.MAX_LEVEL;
    public static final int cureAbilityCooldown = ForgeConfigHandler.infestedBeacon.CURE_ABILITY_COOLDOWN;
    public static final int cureLingeringDuration = ForgeConfigHandler.infestedBeacon.CURE_LINGERING_DURATION;
    public static final int rangeIncreasePerLevel = ForgeConfigHandler.infestedBeacon.RANGE_INCREASE_PER_LEVEL;
    public static final int maxStrengthEffectAplifier = ForgeConfigHandler.infestedBeacon.MAX_STRENGTH_EFFECT_AMPLIFIER;
    public static final int maxPivotEffectAmplifier = ForgeConfigHandler.infestedBeacon.MAX_PIVOT_EFFECT_AMPLIFIER;

    // Same as the vanilla beacon
    private final UpdateLimiter updateLimiter = new UpdateLimiter(80);
    private byte[] powerLevels = new byte[4];
    private int currentCureAbilityCooldown = 0;


    private byte getPowerLevelOfBlock(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != SRPBlocks.evolutionLure) return 0;

        BlockEvolutionLure.EnumType variant = state.getValue(BlockEvolutionLure.VARIANT);
        return (byte) (variant.ordinal() + 1);
    }

    private byte[] calculatePowerLevels() {
        byte[] newPowerLevels = new byte[4];
        newPowerLevels[0] = getPowerLevelOfBlock(pos.north().west());
        newPowerLevels[1] = getPowerLevelOfBlock(pos.north().east());
        newPowerLevels[2] = getPowerLevelOfBlock(pos.south().east());
        newPowerLevels[3] = getPowerLevelOfBlock(pos.south().west());
        return newPowerLevels;
    }

    public int getMaxPower() {
        // Right now this is the last obtainable one
        return (BlockEvolutionLure.EnumType.SIX.ordinal() + 1) * powerLevels.length;
    }

    public byte[] getPowerLevels() {
        return powerLevels;
    }

    public int getTotalPower() {
        int sum = 0;
        for (byte powerLevel : powerLevels) sum += powerLevel;
        return sum;
    }

    public void callBlockUpdate() {
        if (this.world == null) return;

        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 3);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(
            @Nonnull NetworkManager net,
            SPacketUpdateTileEntity pkt
    ) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        int powerLevelInt = this.powerLevels[0] << 24 | this.powerLevels[1] << 16 | this.powerLevels[2] << 8 | this.powerLevels[3];
        player.sendWindowProperty(container, 0, powerLevelInt);
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        switch (id) {
            case 0:
                this.powerLevels[0] = (byte) (data >> 24);
                this.powerLevels[1] = (byte) (data >> 16);
                this.powerLevels[2] = (byte) (data >> 8);
                this.powerLevels[3] = (byte) data;
                break;
        }
    }

    private void performSpecialAbility(EntityPlayer entity) {
        PotionEffect effect = new PotionEffect(ModPotions.CURE_POTION, 20, 0);
        EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, entity.posX, entity.posY, entity.posZ);
        cloud.addEffect(effect);

        cloud.setRadius(3.F);
        cloud.setDuration(cureLingeringDuration);
        cloud.setColor(ModPotions.CURE_POTION.getLiquidColor());
        cloud.setWaitTime(0);

        world.spawnEntity(cloud);
    }

    private boolean canPerformSpecialAbility() {
        return currentCureAbilityCooldown <= 0;
    }

    public int getBeaconLevel() {
        return (int) Math.floor((double) getTotalPower() / getMaxPower() * maxLevel);
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        // Override the 1x1x1 default to span from the block up to the 1.12.2 sky limit
        return new AxisAlignedBB(
                this.pos.getX(),
                this.pos.getY(),
                this.pos.getZ(),
                this.pos.getX() + 1,
                world.getHeight(),
                this.pos.getZ() + 1
        );
    }

    @Override
    public void update() {
        if (this.currentCureAbilityCooldown > 0) this.currentCureAbilityCooldown -= 1;

        if (updateLimiter.every(5)) {
            byte[] newPowerLevels = calculatePowerLevels();

            if (!Arrays.equals(newPowerLevels, powerLevels)) {
                this.callBlockUpdate();

                // The beacon just got powered up, play a sound
                if (this.getTotalPower() == 0) {
                    world.playSound(null, pos, ModSounds.INFESTED_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.5F, 1.0F);
                }
            }

            powerLevels = calculatePowerLevels();
        }
        if (updateLimiter.tickDown()) return;
        if (this.getTotalPower() == 0) return;

        int level = this.getBeaconLevel();

        AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(range + this.getBeaconLevel() * rangeIncreasePerLevel);
        List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        for (EntityPlayer entity : entities) {
            entity.addPotionEffect(new PotionEffect(
                    SRPPotions.PIVOT_E,
                    effectDuration,
                    (int) ((float) maxPivotEffectAmplifier / maxLevel * level)
            ));
            entity.addPotionEffect(new PotionEffect(
                    MobEffects.STRENGTH,
                    effectDuration,
                    (int) ((float) maxStrengthEffectAplifier / maxLevel * level)
            ));

            if (this.canPerformSpecialAbility() && CurePotion.hasCurableEffect(entity)) {
                this.performSpecialAbility(entity);
                currentCureAbilityCooldown = cureAbilityCooldown;
            }
        }

        updateLimiter.reset();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setByteArray("PowerLevels", powerLevels);
        compound.setInteger("CurrentCureAbilityCooldown", currentCureAbilityCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.powerLevels = NBTHelper.getByteArrayOrElse(compound, "PowerLevels", () -> new byte[4]);
        this.currentCureAbilityCooldown = NBTHelper.getIntegerOrElse(compound, "CurrentCureAbilityCooldown", () -> 0);
    }
}
