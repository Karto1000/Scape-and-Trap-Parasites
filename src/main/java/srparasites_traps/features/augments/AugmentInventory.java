package srparasites_traps.features.augments;

import cofh.api.core.IAugmentable;
import cofh.core.block.TileCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class AugmentInventory<T extends TileCore & IAugmentable> implements IInventory {
    private final T tileEntity;

    public AugmentInventory(T tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Override
    public int getSizeInventory() {
        return tileEntity.getAugmentSlots().length;
    }

    @Override
    public boolean isEmpty() {
        return Arrays.stream(tileEntity.getAugmentSlots()).allMatch(ItemStack::isEmpty);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return tileEntity.getAugmentSlots()[index];
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(
            int index,
            int count
    ) {
        ItemStack stack = getStackInSlot(index);
        setInventorySlotContents(index, ItemStack.EMPTY);
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getStackInSlot(index);
        setInventorySlotContents(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(
            int index,
            ItemStack stack
    ) {
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }

        tileEntity.getAugmentSlots()[index] = stack;
        tileEntity.updateAugmentStatus();
        tileEntity.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {
        this.tileEntity.markDirty();
        this.tileEntity.updateAugmentStatus();
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(
            int index,
            @Nonnull ItemStack stack
    ) {
        return this.tileEntity.isValidAugment(stack);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(
            int id,
            int value
    ) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Nonnull
    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
