package srparasites_traps.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import srparasites_traps.SRParasitesTraps;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class ModItems {
    public static Item SENTRY_TURRET_BASE_ITEM = new ItemBlock(ModBlocks.SENTRY_TURRET_BASE)
            .setRegistryName(Objects.requireNonNull(ModBlocks.SENTRY_TURRET_BASE.getRegistryName()));

    public static ArrayList<Item> getItemList() {
        ArrayList<Item> items = new ArrayList<>();

        for (Field field : ModItems.class.getDeclaredFields()) {
            if (field.getType() != Item.class) continue;

            try {
                Item item = (Item) field.get(null);
                items.add(item);
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error("Failed to get item from field {}", field.getName());
            }
        }

        return items;
    }
}
