package srparasites_traps.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class DualEnergyStorage extends EnergyStorage {
    private final cofh.redstoneflux.impl.EnergyStorage rfEnergyStorage;

    public DualEnergyStorage(int capacity) {
        super(capacity);
        this.maxExtract = capacity;
        this.rfEnergyStorage = new cofh.redstoneflux.impl.EnergyStorage(capacity);
    }

    public cofh.redstoneflux.impl.EnergyStorage getRfEnergyStorage() {
        return rfEnergyStorage;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        this.rfEnergyStorage.setEnergyStored(this.energy);
        return extracted;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        this.rfEnergyStorage.setEnergyStored(this.energy);
        return received;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        this.rfEnergyStorage.setEnergyStored(energy);
    }

    public void setCapacity(int maxCapacity) {
        this.capacity = maxCapacity;
        this.rfEnergyStorage.setCapacity(maxCapacity);
    }

    public EnergyStorage readFromNBT(NBTTagCompound nbt) {
        this.energy = nbt.getInteger("Energy");
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }

        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (this.energy < 0) {
            this.energy = 0;
        }

        nbt.setInteger("Energy", this.energy);
        return nbt;
    }
}
