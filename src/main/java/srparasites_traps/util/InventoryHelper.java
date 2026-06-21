package srparasites_traps.util;

import net.minecraftforge.items.ItemStackHandler;

public class InventoryHelper {
    public static boolean isInventoryEmpty(ItemStackHandler inventory) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            if (!inventory.getStackInSlot(slot).isEmpty()) return false;
        }

        return true;
    }
}
