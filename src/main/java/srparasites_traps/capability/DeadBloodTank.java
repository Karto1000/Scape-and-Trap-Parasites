package srparasites_traps.capability;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import srparasites_traps.util.Constants;

public class DeadBloodTank extends FluidTank {
    public DeadBloodTank(int capacity) {
        super(capacity);
    }

    public Fluid getAcceptedFluid() {
        return Constants.TURRET_FUEL;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid == null) return false;
        if (fluid.getFluid() == null) return false;
        return fluid.getFluid().equals(Constants.TURRET_FUEL);
    }

    public void fill(int amount) {
        this.fill(new FluidStack(this.getAcceptedFluid(), amount), true);
    }

    public void set(int amount) {
        FluidStack currentFluid = this.getFluid();

        if (currentFluid != null) {
            currentFluid.amount = amount;
            this.setFluid(currentFluid);
            return;
        }

        this.setFluid(new FluidStack(this.getAcceptedFluid(), amount));
    }

    public boolean isFull() {
        return this.getFluidAmount() >= this.getCapacity();
    }
}
