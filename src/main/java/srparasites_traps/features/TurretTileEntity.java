package srparasites_traps.features;

import cofh.core.block.TileCore;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import srparasites_traps.capability.BiomassTank;
import srparasites_traps.capability.DualEnergyStorage;

import javax.annotation.Nullable;

public abstract class TurretTileEntity extends TileCore implements ICapabilityProvider {
    public final BiomassTank biomassStorage;
    public final DualEnergyStorage energyStorage;
    protected final static int AVAILABLE_WINDOW_VAR = 2;

    public TurretTileEntity(int maxBiomass, int maxEnergy) {
        this.biomassStorage = new BiomassTank(maxBiomass);
        this.biomassStorage.setTileEntity(this);
        this.energyStorage = new DualEnergyStorage(maxEnergy);
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
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("BiomassStorage")) {
            this.biomassStorage.readFromNBT(compound.getCompoundTag("BiomassStorage"));
        }
        if (compound.hasKey("EnergyStorage")) {
            this.energyStorage.readFromNBT(compound.getCompoundTag("EnergyStorage"));
        }

        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("BiomassStorage", this.biomassStorage.writeToNBT(new NBTTagCompound()));
        compound.setTag("EnergyStorage", this.energyStorage.writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(compound);
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
                else this.biomassStorage.setBiomass(data);
                break;
            case 1:
                this.energyStorage.setEnergy(data);
                break;
        }
    }
}
