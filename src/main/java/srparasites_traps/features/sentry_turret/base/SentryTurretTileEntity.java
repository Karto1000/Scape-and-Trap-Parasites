package srparasites_traps.features.sentry_turret.base;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import srparasites_traps.capability.BiomassTank;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class SentryTurretTileEntity extends TileEntity implements ITickable, ICapabilityProvider {
    private SentryTurretEntity assignedSentryTurret;
    private UUID assignedSentryTurretUUID;
    private final FluidTank biomassStorage = new BiomassTank(2000);

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
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.biomassStorage);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {

    }

    public int getBiomassAmount() {
        return biomassStorage.getFluidAmount();
    }

    public int getBiomassCapacity() {
        return biomassStorage.getCapacity();
    }

    public boolean consumeBiomass(int amount) {
        if (this.biomassStorage.getFluidAmount() < amount) return false;
        return this.biomassStorage.drain(amount, true) != null;
    }

    public boolean hasMoreBiomassThan(int amount) {
        return this.biomassStorage.getFluidAmount() > amount;
    }
}
