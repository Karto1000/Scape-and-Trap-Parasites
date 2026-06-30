package srparasites_traps.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class VecHelper {
    public static long area(
            BlockPos pos1,
            BlockPos pos2
    ) {
        int width = Math.abs(pos1.getX() - pos2.getX()) + 1;
        int height = Math.abs(pos1.getY() - pos2.getY()) + 1;
        int depth = Math.abs(pos1.getZ() - pos2.getZ()) + 1;
        return (long) width * height * depth;
    }

    public static Vec3d blockPosToVec3d(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos getRandomPosition(
            Random random,
            AxisAlignedBB bb
    ) {
        return new BlockPos(
                bb.minX + random.nextDouble() * (bb.maxX - bb.minX),
                bb.minY + random.nextDouble() * (bb.maxY - bb.minY),
                bb.minZ + random.nextDouble() * (bb.maxZ - bb.minZ)
        );
    }

    public static String formatAsXYZ(BlockPos pos) {
        return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3d random(Random random) {
        return new Vec3d(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }
}
