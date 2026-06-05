package srparasites_traps.features.static_electricity_generator;

import cofh.core.block.TileCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import srparasites_traps.features.tesla_coil.TeslaCoilTileEntity;
import srparasites_traps.registry.ModBlocks;
import srparasites_traps.util.UpdateLimiter;

public class StaticElectricityGeneratorTileEntity extends TileCore implements ITickable {
    private final UpdateLimiter updateLimiter = new UpdateLimiter(20);

    @Override
    public void update() {
        if (updateLimiter.tickDown()) return;

        BlockPos blockAbove = this.pos.up();
        IBlockState blockStateAbove = world.getBlockState(blockAbove);
        if (blockStateAbove.getBlock() != ModBlocks.TESLA_COIL) return;

        TileEntity tileEntity = this.world.getTileEntity(blockAbove);
        if (tileEntity == null) return;
        if (!(tileEntity instanceof TeslaCoilTileEntity)) return;

        TeslaCoilTileEntity teslaCoilTileEntity = (TeslaCoilTileEntity) tileEntity;
        teslaCoilTileEntity.energyStorage.receiveEnergy(teslaCoilTileEntity.maxEnergy, false);
    }
}
