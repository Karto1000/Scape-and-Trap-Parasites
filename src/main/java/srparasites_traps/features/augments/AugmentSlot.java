package srparasites_traps.features.augments;

import cofh.api.core.IAugmentable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class AugmentSlot<T extends IAugmentable> extends Slot {
    private final T tileEntity;

    public AugmentSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, T tileEntity) {
        super(inventoryIn, index, xPosition, yPosition);

        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return this.tileEntity.isValidAugment(stack);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
