package srparasites_traps.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class DualEnergyStorage extends EnergyStorage {
    private final cofh.redstoneflux.impl.EnergyStorage rfEnergyStorage = new cofh.redstoneflux.impl.EnergyStorage(0);

    public DualEnergyStorage(int capacity) {
        super(capacity);
        this.rfEnergyStorage.setCapacity(capacity);
        this.rfEnergyStorage.setMaxReceive(this.maxReceive);
        this.rfEnergyStorage.setMaxExtract(this.maxExtract);
    }

    public cofh.redstoneflux.impl.EnergyStorage getRfEnergyStorage() {
        return rfEnergyStorage;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        this.rfEnergyStorage.extractEnergy(maxExtract, simulate);
        return super.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        this.rfEnergyStorage.receiveEnergy(maxReceive, simulate);
        return super.receiveEnergy(maxReceive, simulate);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        this.rfEnergyStorage.setEnergyStored(energy);
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
