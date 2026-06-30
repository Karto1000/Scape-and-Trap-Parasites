package srparasites_traps.util;

import cofh.api.tileentity.IRedstoneControl;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneControlHelper {
    public static <T extends IRedstoneControl> void updatePower(
            T tileEntity,
            World world,
            BlockPos pos
    ) {
        if (tileEntity.getControl() == IRedstoneControl.ControlMode.DISABLED) return;
        int power = world.getRedstonePowerFromNeighbors(pos);

        if (tileEntity.getControl() == IRedstoneControl.ControlMode.LOW)
            tileEntity.setPowered(power <= 15 && power > 0);
        else if (tileEntity.getControl() == IRedstoneControl.ControlMode.HIGH) tileEntity.setPowered(power >= 15);

        IBlockState blockState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, blockState, blockState, 3);
    }
}
