package srparasites_traps.features.tesla_coil;

import cofh.core.block.TileCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import srparasites_traps.capability.DualEnergyStorage;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModPotions;
import srparasites_traps.registry.ModSounds;
import srparasites_traps.util.VecHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TeslaCoilTileEntity extends TileCore implements ITickable {
    private static final int CHECK_DELAY_TICKS = 10;
    private int currentCheckDelayTicks = CHECK_DELAY_TICKS;

    public int maxEnergy = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_MAX_ENERGY;
    public int energyPerShot = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_ENERGY_PER_SHOT;
    public int range = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_RANGE;
    public int shockedAmplifier = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_ARC_AMPLIFIER;

    public final DualEnergyStorage energyStorage;

    public TeslaCoilTileEntity() {
        this.energyStorage = new DualEnergyStorage(maxEnergy);
    }

    private Optional<EntityLivingBase> getPossibleTarget() {
        AxisAlignedBB aabb = new AxisAlignedBB(
                this.pos.getX(),
                this.pos.getY(),
                this.pos.getZ(),
                1,
                1,
                1
        ).grow(range);
        List<EntityLivingBase> entityMobs = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        for (EntityLivingBase entity : entityMobs) {
            if (!(entity instanceof EntityMob)) continue;
            if (!entity.isEntityAlive()) continue;

            Vec3d targetPos = entity.getPositionVector().add(0, entity.height / 2.0, 0);
            Vec3d blockCenter = VecHelper.blockPosToVec3d(this.pos).add(0.5, 0.5, 0.5);

            Vec3d directionToTarget = targetPos.subtract(blockCenter).normalize();
            Vec3d safeStartPos = blockCenter.add(directionToTarget);

            RayTraceResult rayTraceResult = this.world.rayTraceBlocks(
                    safeStartPos,
                    entity.getPositionVector(),
                    false,
                    true,
                    false
            );

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) continue;

            return Optional.of(entity);
        }

        return Optional.empty();
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    private void fireAt(EntityLivingBase target) {
        Vec3d blockCenter = VecHelper.blockPosToVec3d(this.pos).add(0.5, 0.5, 0.5);

        LightningParticle lightningParticle = new LightningParticle(
                this.world,
                blockCenter,
                target.getPositionVector().add(0, target.getEyeHeight(), 0),
                20
        );
        Minecraft.getMinecraft().effectRenderer.addEffect(lightningParticle);
        target.addPotionEffect(new PotionEffect(ModPotions.SHOCKED_POTION, 5, shockedAmplifier));
        this.energyStorage.extractEnergy(energyPerShot, false);
        this.world.playSound(
                null,
                blockCenter.x,
                blockCenter.y,
                blockCenter.z,
                ModSounds.TESLA_COIL_FIRE,
                SoundCategory.BLOCKS,
                0.25F,
                1F
        );
    }

    @Override
    public void update() {
        if (currentCheckDelayTicks > 0) {
            currentCheckDelayTicks--;
            return;
        }

        if (this.energyStorage.getEnergyStored() < energyPerShot) return;

        Optional<EntityLivingBase> possibleTarget = getPossibleTarget();
        if (!possibleTarget.isPresent()) return;
        EntityLivingBase entity = possibleTarget.get();

        this.fireAt(entity);

        currentCheckDelayTicks = CHECK_DELAY_TICKS;
    }
}
