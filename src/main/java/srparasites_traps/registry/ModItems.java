package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.dispatcher_brainstem.DispatcherBrainstem;
import srparasites_traps.features.disptacher_cerebrum.DispatcherCerebrum;
import srparasites_traps.features.relocation_marker.RelocationMarkerItem;
import srparasites_traps.features.sentry_skin.SentrySkinItem;
import srparasites_traps.features.two_way_communication_unit.TwoWayCommunicationUnitItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class ModItems {
    public static Item SENTRY_TURRET_BASE_ITEM = getItemBlock(ModBlocks.SENTRY_TURRET_BASE);
    public static Item RELOCATOR_ITEM = getItemBlock(ModBlocks.RELOCATOR);
    public static Item SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.SERRATED_SPIKES);
    public static Item FLAME_COATED_SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.FLAME_COATED_SERRATED_SPIKES);
    public static Item RELOCATION_MARKER_ITEM = new RelocationMarkerItem();
    public static Item TWO_WAY_COMMUNICATION_UNIT = new TwoWayCommunicationUnitItem();
    public static Item SENTRY_FLESH = new SentrySkinItem();
    public static Item DISPATCHER_BRAINSTEM = new DispatcherBrainstem();
    public static Item DISPATCHER_CEREBRUM = new DispatcherCerebrum();

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
