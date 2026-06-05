package srparasites_traps.features;

import cofh.api.core.IAugmentable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public interface IExtendedAugmentable extends IAugmentable, IDefaultValueHolder {
    default void writeAugmentsToNBT(NBTTagCompound compound) {
        NBTTagList augmentList = new NBTTagList();
        ItemStack[] augments = this.getAugmentSlots();

        for (int i = 0; i < augments.length; i++) {
            ItemStack augment = augments[i];
            if (augment.isEmpty()) continue;

            NBTTagCompound augmentTag = new NBTTagCompound();
            augmentTag.setInteger("Slot", i);
            augment.writeToNBT(augmentTag);
            augmentList.appendTag(augmentTag);
        }

        compound.setTag("Augments", augmentList);
    }

    default void readAugmentsFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Augments")) {
            NBTTagList augmentList = compound.getTagList("Augments", 10);
            ItemStack[] augments = this.getAugmentSlots();

            for (int i = 0; i < augmentList.tagCount(); i++) {
                NBTTagCompound augmentTag = augmentList.getCompoundTagAt(i);
                int slot = augmentTag.getInteger("Slot");
                if (slot < 0 || slot >= augments.length) continue;

                augments[slot] = new ItemStack(augmentTag);
            }
        }
    }

    default Optional<Integer> getFreeSlot() {
        ItemStack[] augments = this.getAugmentSlots();

        for (int i = 0; i < augments.length; i++) {
            if (augments[i].isEmpty()) return Optional.of(i);
        }

        return Optional.empty();
    }

    @Override
    default void updateAugmentStatus() {
        this.applyDefaults();
        ItemStack[] augments = this.getAugmentSlots();
        for (ItemStack augment : augments) applyAugment(augment);
    }

    @Override
    default boolean installAugment(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        if (!isValidAugment(itemStack)) return false;

        Optional<Integer> freeSlot = getFreeSlot();
        if (!freeSlot.isPresent()) return false;

        Integer slot = freeSlot.get();
        this.getAugmentSlots()[slot] = itemStack;

        return true;
    }

    default void dropAugments(World world, BlockPos pos) {
        ItemStack[] augments = this.getAugmentSlots();
        for (ItemStack augment : augments) {
            if (!augment.isEmpty()) {
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), augment));
            }
        }
    }

    void applyAugment(ItemStack augment);
}
