package srparasites_traps.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;
import java.util.function.Supplier;

public class NBTHelper {
    public static <T> T getOrElse(NBTTagCompound nbt, String key, Supplier<T> exists, Supplier<T> orElse) {
        if (nbt.hasKey(key)) return exists.get();
        return orElse.get();
    }

    public static Double getDoubleOrElse(NBTTagCompound nbt, String key, Supplier<Double> orElse) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getDouble(key),
                orElse
        );
    }

    public static Integer getIntegerOrElse(NBTTagCompound nbt, String key, Supplier<Integer> orElse) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getInteger(key),
                orElse
        );
    }

    public static Long getLongOrElse(NBTTagCompound nbt, String key, Supplier<Long> orElse) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getLong(key),
                orElse
        );
    }

    public static Boolean getBooleanOrElse(NBTTagCompound nbt, String key, Supplier<Boolean> orElse) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getBoolean(key),
                orElse
        );
    }

    public static UUID getUniqueIdOrElse(NBTTagCompound nbt, String key, Supplier<UUID> orElse) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getUniqueId(key),
                orElse
        );
    }

    public static BlockPos getBlockPosOrElse(NBTTagCompound nbt, String key, Supplier<BlockPos> orElse) {
        return getOrElse(
                nbt,
                key,
                () -> BlockPos.fromLong(nbt.getLong(key)),
                orElse
        );
    }

    public static <T> T getOrDie(EntityLiving entityLiving, NBTTagCompound nbt, String key, Supplier<T> exists) {
        if (nbt.hasKey(key)) return exists.get();
        else {
            entityLiving.setDead();
            return null;
        }
    }

    public static Double getDoubleOrDie(EntityLiving entityLiving, NBTTagCompound nbt, String key) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getDouble(key));
    }

    public static Integer getIntegerOrDie(EntityLiving entityLiving, NBTTagCompound nbt, String key) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getInteger(key));
    }

    public static UUID getUniqueIdOrDie(EntityLiving entityLiving, NBTTagCompound nbt, String key) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getUniqueId(key));
    }

    public static Boolean getBooleanOrDie(EntityLiving entityLiving, NBTTagCompound nbt, String key) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getBoolean(key));
    }

    public static BlockPos getBlockPosOrDie(EntityLiving entityLiving, NBTTagCompound nbt, String key) {
        return getOrDie(entityLiving, nbt, key, () -> BlockPos.fromLong(nbt.getLong(key)));
    }
}
