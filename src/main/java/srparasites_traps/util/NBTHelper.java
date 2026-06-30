package srparasites_traps.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;
import java.util.function.Supplier;

public class NBTHelper {
    public static void removeStringFromList(
            NBTTagList list,
            String str
    ) {
        for (int i = 0; i < list.tagCount(); i++) if (list.getStringTagAt(i).equals(str)) list.removeTag(i);
    }

    public static boolean listHasString(
            NBTTagList list,
            String str
    ) {
        for (int i = 0; i < list.tagCount(); i++) if (list.getStringTagAt(i).equals(str)) return true;
        return false;
    }

    public static String[] tagListToStringArray(NBTTagList list) {
        String[] array = new String[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++) array[i] = list.getStringTagAt(i);
        return array;
    }

    public static <T> T getOrElse(
            NBTTagCompound nbt,
            String key,
            Supplier<T> exists,
            Supplier<T> orElse
    ) {
        if (nbt.hasKey(key)) return exists.get();
        return orElse.get();
    }

    public static Double getDoubleOrElse(
            NBTTagCompound nbt,
            String key,
            Supplier<Double> orElse
    ) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getDouble(key),
                orElse
        );
    }

    public static Integer getIntegerOrElse(
            NBTTagCompound nbt,
            String key,
            Supplier<Integer> orElse
    ) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getInteger(key),
                orElse
        );
    }

    public static Long getLongOrElse(
            NBTTagCompound nbt,
            String key,
            Supplier<Long> orElse
    ) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getLong(key),
                orElse
        );
    }

    public static Boolean getBooleanOrElse(
            NBTTagCompound nbt,
            String key,
            Supplier<Boolean> orElse
    ) {
        return getOrElse(
                nbt,
                key,
                () -> nbt.getBoolean(key),
                orElse
        );
    }

    public static BlockPos getBlockPosOrElse(
            NBTTagCompound nbt,
            String key,
            Supplier<BlockPos> orElse
    ) {
        return getOrElse(
                nbt,
                key,
                () -> BlockPos.fromLong(nbt.getLong(key)),
                orElse
        );
    }

    public static <T> T getOrDie(
            EntityLiving entityLiving,
            NBTTagCompound nbt,
            String key,
            Supplier<T> exists
    ) {
        if (nbt.hasKey(key)) return exists.get();
        else {
            entityLiving.setDead();
            return null;
        }
    }

    public static Double getDoubleOrDie(
            EntityLiving entityLiving,
            NBTTagCompound nbt,
            String key
    ) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getDouble(key));
    }

    public static Integer getIntegerOrDie(
            EntityLiving entityLiving,
            NBTTagCompound nbt,
            String key
    ) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getInteger(key));
    }

    public static UUID getUniqueIdOrDie(
            EntityLiving entityLiving,
            NBTTagCompound nbt,
            String key
    ) {
        if (nbt.hasUniqueId(key)) return nbt.getUniqueId(key);
        else {
            entityLiving.setDead();
            return null;
        }
    }

    public static Boolean getBooleanOrDie(
            EntityLiving entityLiving,
            NBTTagCompound nbt,
            String key
    ) {
        return getOrDie(entityLiving, nbt, key, () -> nbt.getBoolean(key));
    }

    public static BlockPos getBlockPosOrDie(
            EntityLiving entityLiving,
            NBTTagCompound nbt,
            String key
    ) {
        return getOrDie(entityLiving, nbt, key, () -> BlockPos.fromLong(nbt.getLong(key)));
    }
}
