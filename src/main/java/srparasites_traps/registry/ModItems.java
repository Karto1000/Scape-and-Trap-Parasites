package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.BasicItem;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.features.hardness_analyser.HardnessAnalyzerItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class ModItems {
    public static Item SENTRY_TURRET_ITEM = getItemBlock(ModBlocks.SENTRY_TURRET);
    public static Item RELOCATOR_ITEM = getItemBlock(ModBlocks.RELOCATOR);
    public static Item SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.SERRATED_SPIKES);
    public static Item BURNING_SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.BURNING_SERRATED_SPIKES);
    public static Item VIRAL_SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.VIRAL_SERRATED_SPIKES);
    public static Item BIOMASS_FACTORY_ITEM = getItemBlock(ModBlocks.BIOMASS_FACTORY);
    public static Item DECONTAMINATOR_ITEM = getItemBlock(ModBlocks.DECONTAMINATOR);
    public static Item OBSIDIAN_LADDER_ITEM = getItemBlock(ModBlocks.OBSIDIAN_LADDER);
    public static Item PROXIMITY_SENSOR_ITEM = getItemBlock(ModBlocks.PROXIMITY_SENSOR);
    public static Item OBSIDIAN_SLAB_ITEM = getItemSlab(ModBlocks.OBSIDIAN_SLAB, ModBlocks.OBSIDIAN_SLAB, ModBlocks.OBSIDIAN_SLAB_DOUBLE);
    public static Item TESLA_COIL_ITEM = getItemBlock(ModBlocks.TESLA_COIL);
    public static Item STATIC_ELECTRICITY_GENERATOR_ITEM = getItemBlock(ModBlocks.STATIC_ELECTRICITY_GENERATOR);
    public static Item AREA_MARKER_ITEM = new AreaMarkerItem();
    public static HardnessAnalyzerItem HARDNESS_ANALYZER_ITEM = new HardnessAnalyzerItem();
    public static Item PARASITIC_MATTER = new BasicItem("parasitic_matter").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item TWO_WAY_COMMUNICATION_UNIT = new BasicItem("two_way_communication_unit").maxStackSize(1);
    public static Item DISPATCHER_BRAINSTEM = new BasicItem("dispatcher_brainstem").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item DISPATCHER_CEREBRUM = new BasicItem("dispatcher_cerebrum").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item RECONSTRUCTED_SENTRY = new BasicItem("reconstructed_sentry").maxStackSize(1);
    public static Item RECONSTRUCTED_SEIZER = new BasicItem("reconstructed_seizer").maxStackSize(1);
    public static Item BECKON_HEART = new BasicItem("beckon_heart").maxStackSize(16).rarity(EnumRarity.RARE);
    public static Item ANTENNA = new BasicItem("antenna");
    public static Item COIL = new BasicItem("coil");

    private static Item getItemSlab(Block block, BlockSlab single, BlockSlab doubleSlab) {
        return new ItemSlab(block, single, doubleSlab).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    private static Item getItemBlock(Block block) {
        return new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    public static ArrayList<Item> getItemList() {
        ArrayList<Item> items = new ArrayList<>();

        for (Field field : ModItems.class.getDeclaredFields()) {
            if (!Item.class.isAssignableFrom(field.getType())) continue;

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
