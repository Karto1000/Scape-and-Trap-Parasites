package srparasites_traps.features.sentry_turret.base;

import cofh.core.block.TileCore;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import srparasites_traps.capability.BiomassTank;
import srparasites_traps.capability.DualEnergyStorage;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class SentryTurretBaseTileEntity extends TileCore implements ITickable, ICapabilityProvider {
    private SentryTurretEntity assignedSentryTurret;
    private UUID assignedSentryTurretUUID;
    public int biomassPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_BIOMASS_PER_SHOT;
    public int energyPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENERGY_PER_SHOT;
    public final BiomassTank biomassStorage = new BiomassTank(ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_MAX_BIOMASS);
    public final DualEnergyStorage energyStorage = new DualEnergyStorage(ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_MAX_ENERGY);

    public SentryTurretBaseTileEntity() {
        this.biomassStorage.setTileEntity(this);
    }

    public void setAssignedSentryTurret(SentryTurretEntity sentryTurret) {
        this.assignedSentryTurret = sentryTurret;
        this.assignedSentryTurretUUID = sentryTurret.getUniqueID();
        this.markDirty();
    }

    public Optional<SentryTurretEntity> getAssignedSentryTurret() {
        if (this.world.isRemote) return Optional.empty();

        if (this.assignedSentryTurret != null) {
            if (this.assignedSentryTurret.isDead) {
                this.assignedSentryTurret = null;
                this.assignedSentryTurretUUID = null;
                return Optional.empty();
            }
            return Optional.of(assignedSentryTurret);
        }

        Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.assignedSentryTurretUUID);
        if (entity == null) return Optional.empty();
        if (!(entity instanceof SentryTurretEntity)) return Optional.empty();

        if (entity.isDead) {
            this.assignedSentryTurret = null;
            this.assignedSentryTurretUUID = null;
            return Optional.empty();
        }

        this.assignedSentryTurret = (SentryTurretEntity) entity;
        return Optional.of(this.assignedSentryTurret);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.assignedSentryTurretUUID != null) {
            compound.setUniqueId("TurretUUID", this.assignedSentryTurretUUID);
        }
        compound.setTag("BiomassStorage", this.biomassStorage.writeToNBT(new NBTTagCompound()));
        compound.setTag("EnergyStorage", this.energyStorage.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasUniqueId("TurretUUID")) {
            this.assignedSentryTurretUUID = compound.getUniqueId("TurretUUID");
        }
        if (compound.hasKey("BiomassStorage")) {
            this.biomassStorage.readFromNBT(compound.getCompoundTag("BiomassStorage"));
        }
        if (compound.hasKey("EnergyStorage")) {
            this.energyStorage.readFromNBT(compound.getCompoundTag("EnergyStorage"));
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        } else if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.biomassStorage);
        } else if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, 0, this.biomassStorage.getFluidAmount());
        player.sendWindowProperty(container, 1, this.energyStorage.getEnergyStored());
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case 0:
                if (data == 0) this.biomassStorage.setFluid(null);
                else {
                    FluidStack currentFluid = this.biomassStorage.getFluid();
                    if (currentFluid != null) {
                        currentFluid.amount = data;
                        this.biomassStorage.setFluid(currentFluid);
                    } else {
                        this.biomassStorage.setFluid(new FluidStack(srparasites_traps.util.Constants.SENTRY_TURRET_ACCEPTED_FLUID, data));
                    }
                }
                break;
            case 1:
                this.energyStorage.setEnergy(data);
                break;
        }
    }

    public boolean consumeBiomass(int amount) {
        if (this.biomassStorage.getFluidAmount() < amount) return false;
        this.markDirty();
        return this.biomassStorage.drain(amount, true) != null;
    }

    public boolean consumeEnergy(int amount) {
        if (this.energyStorage.getEnergyStored() < amount) return false;
        this.markDirty();
        return this.energyStorage.extractEnergy(amount, false) == amount;
    }

    public boolean hasEnoughBiomassToShoot() {
        return this.biomassStorage.getFluidAmount() >= this.biomassPerShot;
    }

    public boolean hasEnoughEnergyToShoot() {
        return this.energyStorage.getEnergyStored() >= this.energyPerShot;
    }

}
