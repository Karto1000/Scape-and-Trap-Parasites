package srparasites_traps.util;

import cofh.api.tileentity.IRedstoneControl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneControlHelper {
    public static <T extends IRedstoneControl> void setPowered(T tileEntity, World world, BlockPos pos) {
        if (tileEntity.getControl() == IRedstoneControl.ControlMode.DISABLED) return;
        int power = world.getRedstonePowerFromNeighbors(pos);

        if (tileEntity.getControl() == IRedstoneControl.ControlMode.LOW) {
            tileEntity.setPowered(power <= 15 && power > 0);
        } else if (tileEntity.getControl() == IRedstoneControl.ControlMode.HIGH) {
            tileEntity.setPowered(power >= 15);
        }
    }
}
