package srparasites_traps.util;

import net.minecraft.util.math.BlockPos;

public class VecHelper {
    public static long area(BlockPos pos1, BlockPos pos2) {
        int width = Math.abs(pos1.getX() - pos2.getX()) + 1;
        int height = Math.abs(pos1.getY() - pos2.getY()) + 1;
        int depth = Math.abs(pos1.getZ() - pos2.getZ()) + 1;
        return (long) width * height * depth;
    }

    public static String formatAsXYZ(BlockPos pos) {
        return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
    }
}
