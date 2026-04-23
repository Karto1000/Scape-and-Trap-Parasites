package srparasites_traps.capability;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import srparasites_traps.util.Constants;

public class BiomassTank extends FluidTank {
    public BiomassTank(int capacity) {
        super(capacity);
    }

    public Fluid getAcceptedFluid() {
        return Constants.BIOMASS_FLUID;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid == null) return false;
        if (fluid.getFluid() == null) return false;
        return fluid.getFluid().equals(Constants.BIOMASS_FLUID);
    }

    public void fillBiomass(int amount) {
        this.fill(new FluidStack(this.getAcceptedFluid(), amount), true);
    }

    public void setBiomass(int amount) {
        FluidStack currentFluid = this.getFluid();

        if (currentFluid != null) {
            currentFluid.amount = amount;
            this.setFluid(currentFluid);
            return;
        }

        this.setFluid(new FluidStack(this.getAcceptedFluid(), amount));
    }
}
