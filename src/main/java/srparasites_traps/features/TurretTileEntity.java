package srparasites_traps.features;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.block.TileCore;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import srparasites_traps.capability.DeadBloodTank;
import srparasites_traps.capability.DualEnergyStorage;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.RedstoneControlHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TurretTileEntity extends TileCore implements ICapabilityProvider, IRedstoneControl {
    public final DeadBloodTank biomassStorage;
    public final DualEnergyStorage energyStorage;
    public ControlMode controlMode = ControlMode.DISABLED;
    public boolean powered = false;
    protected final static int AVAILABLE_WINDOW_VAR = 3;

    public TurretTileEntity(
            int maxBiomass,
            int maxEnergy
    ) {
        this.biomassStorage = new DeadBloodTank(maxBiomass);
        this.biomassStorage.setTileEntity(this);
        this.energyStorage = new DualEnergyStorage(maxEnergy);
    }

    @Override
    public boolean hasCapability(
            @Nonnull Capability<?> capability,
            @Nullable EnumFacing facing
    ) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        } else if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(
            @Nonnull Capability<T> capability,
            @Nullable EnumFacing facing
    ) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.biomassStorage);
        } else if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("BiomassStorage"))
            this.biomassStorage.readFromNBT(compound.getCompoundTag("BiomassStorage"));
        if (compound.hasKey("EnergyStorage")) this.energyStorage.readFromNBT(compound.getCompoundTag("EnergyStorage"));
        this.controlMode = ControlMode.values()[NBTHelper.getIntegerOrElse(compound, "ControlMode", ControlMode.DISABLED::ordinal)];
        this.powered = NBTHelper.getBooleanOrElse(compound, "Powered", () -> false);

        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("BiomassStorage", this.biomassStorage.writeToNBT(new NBTTagCompound()));
        compound.setTag("EnergyStorage", this.energyStorage.writeToNBT(new NBTTagCompound()));
        compound.setInteger("ControlMode", this.controlMode.ordinal());
        compound.setBoolean("Powered", this.powered);

        return super.writeToNBT(compound);
    }

    @Override
    public void sendGuiNetworkData(
            Container container,
            IContainerListener player
    ) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, 0, this.biomassStorage.getFluidAmount());
        player.sendWindowProperty(container, 1, this.energyStorage.getEnergyStored());
        player.sendWindowProperty(container, 2, this.controlMode.ordinal());
    }

    @Override
    public void receiveGuiNetworkData(
            int id,
            int data
    ) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case 0:
                if (data == 0) this.biomassStorage.setFluid(null);
                else this.biomassStorage.set(data);
                break;
            case 1:
                this.energyStorage.setEnergy(data);
                break;
            case 2:
                this.controlMode = ControlMode.values()[data];
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
        return controlMode;
    }

    @Override
    public void setPowered(boolean b) {
        this.powered = b;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }
}
