package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import srparasites_traps.features.BasicItem;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.features.augments.*;
import srparasites_traps.features.hardness_analyser.HardnessAnalyzerItem;

import java.util.List;
import java.util.Objects;

public class ModItems {
    public static final Item SENTRY_TURRET_ITEM = getItemBlock(ModBlocks.SENTRY_TURRET);
    public static final Item RELOCATOR_ITEM = getItemBlock(ModBlocks.RELOCATOR);
    public static final Item SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.SERRATED_SPIKES);
    public static final Item BURNING_SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.BURNING_SERRATED_SPIKES);
    public static final Item VIRAL_SERRATED_SPIKES_ITEM = getItemBlock(ModBlocks.VIRAL_SERRATED_SPIKES);
    public static final Item BIOMASS_FACTORY_ITEM = getItemBlock(ModBlocks.BIOMASS_FACTORY);
    public static final Item DECONTAMINATOR_ITEM = getItemBlock(ModBlocks.DECONTAMINATOR);
    public static final Item OBSIDIAN_LADDER_ITEM = getItemBlock(ModBlocks.OBSIDIAN_LADDER);
    public static final Item PROXIMITY_SENSOR_ITEM = getItemBlock(ModBlocks.PROXIMITY_SENSOR);
    public static final Item OBSIDIAN_SLAB_ITEM = getItemSlab(ModBlocks.OBSIDIAN_SLAB, ModBlocks.OBSIDIAN_SLAB, ModBlocks.OBSIDIAN_SLAB_DOUBLE);
    public static final Item TESLA_COIL_ITEM = getItemBlock(ModBlocks.TESLA_COIL);
    public static final Item STATIC_ELECTRICITY_GENERATOR_ITEM = getItemBlock(ModBlocks.STATIC_ELECTRICITY_GENERATOR);
    public static final Item BECKON_NIDUS_ITEM = getItemBlock(ModBlocks.BECKON_NIDUS);
    public static final Item OBSIDIAN_GLASS_ITEM = getItemBlock(ModBlocks.OBSIDIAN_GLASS);
    public static final Item OBSIDIAN_STAIRS_ITEM = getItemBlock(ModBlocks.OBSIDIAN_STAIRS);
    public static final Item BLEEDING_OBSIDIAN_GLASS_ITEM = getItemBlock(ModBlocks.BLEEDING_OBSIDIAN_GLASS);
    public static final Item BLEEDING_OBSIDIAN_LADDER_ITEM = getItemBlock(ModBlocks.BLEEDING_OBSIDIAN_LADDER);
    public static final Item BARBED_WIRE_ITEM = getItemBlock(ModBlocks.BARBED_WIRE);
    public static final Item BURNING_BARBED_WIRE_ITEM = getItemBlock(ModBlocks.BURNING_BARBED_WIRE);
    public static final Item VIRAL_BARBED_WIRE_ITEM = getItemBlock(ModBlocks.VIRAL_BARBED_WIRE);
    public static final Item INFESTED_BEACON_ITEM = getItemBlock(ModBlocks.INFESTED_BEACON);
    public static final Item AREA_MARKER_ITEM = new AreaMarkerItem();
    public static final HardnessAnalyzerItem HARDNESS_ANALYZER_ITEM = new HardnessAnalyzerItem();
    public static final Item PARASITIC_MATTER = new BasicItem("parasitic_matter").maxStackSize(16).rarity(EnumRarity.RARE);
    public static final Item TWO_WAY_COMMUNICATION_UNIT = new BasicItem("two_way_communication_unit").maxStackSize(1);
    public static final Item DISPATCHER_BRAINSTEM = new BasicItem("dispatcher_brainstem").maxStackSize(16).rarity(EnumRarity.RARE);
    public static final Item DISPATCHER_CEREBRUM = new BasicItem("dispatcher_cerebrum").maxStackSize(16).rarity(EnumRarity.RARE);
    public static final Item ARMORED_SEMI_ORGANIC_PLATING = new BasicItem("armored_semi_organic_plating");
    public static final Item BIOMASS_STORAGE_UNIT = new BasicItem("biomass_storage_unit");
    public static final Item BECKON_HEART = new BasicItem("beckon_heart").maxStackSize(16).rarity(EnumRarity.RARE);
    public static final Item ANTENNA = new BasicItem("antenna");
    public static final Item COIL = new BasicItem("coil");
    public static final Item BLANK_AUGMENT = new BasicItem("blank_augment");
    public static final TurretAugment ATTACK_SPEED_AUGMENT = new AttackSpeedAugment();
    public static final TurretAugment RANGE_AUGMENT = new RangeAugment();
    public static final TurretAugment DAMAGE_AUGMENT = new DamageAugment();
    public static final TurretAugment TARGETING_AUGMENT = new TargetingAugment();

    private static Item getItemSlab(
            Block block,
            BlockSlab single,
            BlockSlab doubleSlab
    ) {
        return new ItemSlab(block, single, doubleSlab).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    private static Item getItemBlock(Block block) {
        return new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    public static List<Item> getItemList() {
        return RegistryHelper.getStaticFieldsOfType(ModItems.class, Item.class);
    }
}
