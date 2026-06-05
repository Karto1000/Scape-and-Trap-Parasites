package srparasites_traps.features.tesla_coil;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.block.TileCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import srparasites_traps.capability.DualEnergyStorage;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.IDefaultValueHolder;
import srparasites_traps.features.IExtendedAugmentable;
import srparasites_traps.features.augments.AttackSpeedAugment;
import srparasites_traps.features.augments.RangeAugment;
import srparasites_traps.network.SRParasitesTrapsNetwork;
import srparasites_traps.network.SpawnElectricityParticlePacket;
import srparasites_traps.network.SpawnLightningParticlePacket;
import srparasites_traps.registry.ModPotions;
import srparasites_traps.registry.ModSounds;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.RedstoneControlHelper;
import srparasites_traps.util.UpdateLimiter;
import srparasites_traps.util.VecHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TeslaCoilTileEntity extends TileCore implements ITickable, IRedstoneControl, IExtendedAugmentable, IDefaultValueHolder {
    public int maxEnergy = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_MAX_ENERGY;
    public int energyPerShot = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_ENERGY_PER_SHOT;
    public int range = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_RANGE;
    public int shockedAmplifier = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_ARC_AMPLIFIER;
    public int chargingDelay = ForgeConfigHandler.teslaCoil.DEFAULT_CHARGING_DELAY;
    public final DualEnergyStorage energyStorage;

    private boolean firstTick = true;
    private EntityLivingBase target;
    private TeslaCoilState state = TeslaCoilState.IDLE;
    private final UpdateLimiter shootLimiter = new UpdateLimiter(ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_FIRE_DELAY);
    private int currentChargingDelay = this.chargingDelay;
    private final static Vec3d fireOffset = new Vec3d(0.5, 1.8, 0.5);
    private ControlMode controlMode = ControlMode.DISABLED;
    private boolean powered = false;
    private final ItemStack[] augments = Stream.generate(() -> ItemStack.EMPTY)
            .limit(ForgeConfigHandler.augments.TESLA_COIL_AUGMENT_SLOTS)
            .toArray(ItemStack[]::new);

    public TeslaCoilTileEntity() {
        this.energyStorage = new DualEnergyStorage(maxEnergy);
    }

    private Vec3d getBlockCenter() {
        return VecHelper.blockPosToVec3d(this.pos).add(fireOffset);
    }

    private Optional<EntityLivingBase> findPossibleTarget() {
        AxisAlignedBB aabb = new AxisAlignedBB(this.pos).grow(range);
        List<EntityLivingBase> entityMobs = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        for (EntityLivingBase entity : entityMobs) {
            if (!(entity instanceof EntityMob)) continue;
            if (!entity.isEntityAlive()) continue;

            Vec3d targetPos = entity.getPositionVector().add(0, entity.height / 2.0, 0);
            Vec3d blockCenter = this.getBlockCenter();

            Vec3d directionToTarget = targetPos.subtract(blockCenter).normalize();
            Vec3d safeStartPos = blockCenter.add(directionToTarget);

            RayTraceResult rayTraceResult = this.world.rayTraceBlocks(safeStartPos, entity.getPositionVector(), false, true, false);

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) continue;

            return Optional.of(entity);
        }

        return Optional.empty();
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == EnumFacing.DOWN) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == EnumFacing.DOWN) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.energyStorage.readFromNBT(compound.getCompoundTag("EnergyStorage"));
        this.currentChargingDelay = NBTHelper.getIntegerOrElse(compound, "CurrentChargingDelay", () -> this.chargingDelay);
        this.powered = NBTHelper.getBooleanOrElse(compound, "Powered", () -> false);
        this.controlMode = ControlMode.values()[NBTHelper.getIntegerOrElse(compound, "ControlMode", ControlMode.DISABLED::ordinal)];
        this.readAugmentsFromNBT(compound);

        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("EnergyStorage", this.energyStorage.writeToNBT(new NBTTagCompound()));
        compound.setInteger("CurrentChargingDelay", this.currentChargingDelay);
        compound.setBoolean("Powered", this.powered);
        compound.setInteger("ControlMode", this.controlMode.ordinal());
        this.writeAugmentsToNBT(compound);

        return super.writeToNBT(compound);
    }

    private void fireAt(EntityLivingBase target) {
        Vec3d firePos = this.getBlockCenter();
        SRParasitesTrapsNetwork.CHANNEL.sendToAllAround(new SpawnLightningParticlePacket(firePos, target.getPositionVector().add(0, target.getEyeHeight(), 0)), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), firePos.x, firePos.y, firePos.z, 32));

        target.addPotionEffect(new PotionEffect(ModPotions.SHOCKED_POTION, 5, shockedAmplifier));
        this.energyStorage.extractEnergy(energyPerShot, false);
        this.markDirty();
    }

    private Vec3d getRaycastStartPos(EntityLivingBase entity) {
        Vec3d targetPos = entity.getPositionVector().add(0, entity.height / 2.0, 0);
        Vec3d blockCenter = this.getBlockCenter();

        Vec3d directionToTarget = targetPos.subtract(blockCenter).normalize();
        return blockCenter.add(directionToTarget);
    }

    private boolean needsNewTarget() {
        if (this.target == null) return true;
        if (!this.target.isEntityAlive()) return true;
        if (this.target.getDistanceSq(this.pos) > (range * range)) return true;

        Vec3d safeStartPos = this.getRaycastStartPos(this.target);
        RayTraceResult rayTraceResult = this.world.rayTraceBlocks(safeStartPos, this.target.getPositionVector(), false, true, false);

        return rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK;
    }

    private void switchState() {
        Vec3d blockCenter = this.getBlockCenter();

        switch (this.state) {
            case IDLE:
                this.world.playSound(null, blockCenter.x, blockCenter.y, blockCenter.z, ModSounds.TESLA_COIL_CHARGE, SoundCategory.BLOCKS, 0.5F, 1F);
                this.state = TeslaCoilState.CHARGING;
                break;
            case CHARGING:
                this.state = TeslaCoilState.FIRING;
                this.world.playSound(null, blockCenter.x, blockCenter.y, blockCenter.z, ModSounds.TESLA_COIL_FIRE, SoundCategory.BLOCKS, 0.25F, 1F);
                break;
            case FIRING:
                this.state = TeslaCoilState.IDLE;
                break;
            default:
        }
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, 0, this.energyStorage.getEnergyStored());
        player.sendWindowProperty(container, 1, this.controlMode.ordinal());
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case 0:
                this.energyStorage.setEnergy(data);
                break;
            case 1:
                this.controlMode = ControlMode.values()[data];
                break;
        }
    }

    @Override
    public void update() {
        if (this.firstTick) {
            this.updateAugmentStatus();
            this.firstTick = false;
        }

        if (this.energyStorage.getEnergyStored() < energyPerShot) return;

        switch (this.state) {
            case IDLE:
                if (this.shootLimiter.tickDown()) return;
                if (this.controlMode != ControlMode.DISABLED && !this.powered) return;

                if (this.needsNewTarget()) {
                    this.target = null;

                    Optional<EntityLivingBase> possibleTarget = findPossibleTarget();
                    if (!possibleTarget.isPresent()) return;
                    this.target = possibleTarget.get();
                }

                this.switchState();
                shootLimiter.reset();
                break;
            case CHARGING:
                if (this.currentChargingDelay <= 0) {
                    this.currentChargingDelay = chargingDelay;
                    this.switchState();
                    return;
                }

                this.currentChargingDelay--;

                Vec3d coilPosition = this.getBlockCenter();
                Vec3d offset = new Vec3d(Math.sin(this.currentChargingDelay * this.world.rand.nextGaussian()) * 0.8, 0, Math.cos(this.currentChargingDelay * this.world.rand.nextGaussian()) * 0.8);
                Vec3d spawnPos = coilPosition.add(offset);

                SRParasitesTrapsNetwork.CHANNEL.sendToAllAround(new SpawnElectricityParticlePacket(spawnPos), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), spawnPos.x, spawnPos.y, spawnPos.z, 32));

                break;
            case FIRING:
                this.fireAt(this.target);
                this.switchState();
                break;
        }
    }

    @Override
    public boolean setControl(ControlMode controlMode) {
        this.controlMode = controlMode;

        if (this.world.isRemote) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player.openContainer == null) return false;
            mc.playerController.sendEnchantPacket(mc.player.openContainer.windowId, controlMode.ordinal());
        } else {
            RedstoneControlHelper.updatePower(this, this.world, this.pos);
            this.markDirty();
        }

        return true;
    }

    @Override
    public ControlMode getControl() {
        return this.controlMode;
    }

    @Override
    public void setPowered(boolean b) {
        this.powered = b;
    }

    @Override
    public boolean isPowered() {
        return this.powered;
    }

    @Override
    public void applyAugment(ItemStack augment) {
        if (augment.isEmpty()) return;

        if (augment.getItem() instanceof AttackSpeedAugment) {
            this.shootLimiter.tickDuration -= ForgeConfigHandler.augments.TESLA_COIL_ATTACK_SPEED_INCREASE;
        } else if (augment.getItem() instanceof RangeAugment) {
            this.range += ForgeConfigHandler.augments.TESLA_COIL_RANGE_INCREASE;
        }
    }

    @Override
    public boolean isValidAugment(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        if (itemStack.getItem() instanceof AttackSpeedAugment) return true;
        return itemStack.getItem() instanceof RangeAugment;
    }

    @Override
    public ItemStack[] getAugmentSlots() {
        return augments;
    }

    @Override
    public void applyDefaults() {
        this.maxEnergy = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_MAX_ENERGY;
        this.energyPerShot = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_ENERGY_PER_SHOT;
        this.range = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_RANGE;
        this.shockedAmplifier = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_ARC_AMPLIFIER;
        this.chargingDelay = ForgeConfigHandler.teslaCoil.DEFAULT_CHARGING_DELAY;
        this.shootLimiter.tickDuration = ForgeConfigHandler.teslaCoil.DEFAULT_TESLA_COIL_FIRE_DELAY;
    }
}
