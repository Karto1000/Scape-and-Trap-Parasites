package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.BasicItem;
import srparasites_traps.features.relocation_marker.RelocationMarkerItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class ModItems {
    public static Item SENTRY_TURRET_BASE_ITEM = getItemBlock(ModBlocks.SENTRY_TURRET_BASE);
    public static Item RELOCATOR_ITEM = getItemBlock(ModBlocks.RELOCATOR);
    public static Item SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.SERRATED_SPIKES);
    public static Item FLAME_COATED_SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.FLAME_COATED_SERRATED_SPIKES);
    public static Item BIOMASS_FACTORY_ITEM = getItemBlock(ModBlocks.BIOMASS_FACTORY);
    public static Item RELOCATION_MARKER_ITEM = new RelocationMarkerItem();
    public static Item SENTRY_SKIN = new BasicItem("sentry_skin").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item TWO_WAY_COMMUNICATION_UNIT = new BasicItem("two_way_communication_unit").maxStackSize(1);
    public static Item DISPATCHER_BRAINSTEM = new BasicItem("dispatcher_brainstem").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item DISPATCHER_CEREBRUM = new BasicItem("dispatcher_cerebrum").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item RECONSTRUCTED_SENTRY = new BasicItem("reconstructed_sentry").maxStackSize(1);

    private static Item getItemBlock(Block block) {
        return new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

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
