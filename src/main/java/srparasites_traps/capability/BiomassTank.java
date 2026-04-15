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
}
