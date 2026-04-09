package srparasites_traps.capability;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class BiomassTank extends FluidTank {
    public BiomassTank(int capacity) {
        super(capacity);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid == null) return false;
        if (fluid.getFluid() == null) return false;
        return fluid.getFluid().equals(FluidRegistry.WATER);
    }
}
