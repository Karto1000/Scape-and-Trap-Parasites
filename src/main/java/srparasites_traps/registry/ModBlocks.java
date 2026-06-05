package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.biomass_factory.BiomassFactoryBlock;
import srparasites_traps.features.decontaminator.DecontaminatorBlock;
import srparasites_traps.features.obsidian_blocks.ObsidianLadderBlock;
import srparasites_traps.features.obsidian_blocks.ObsidianSlabBlock;
import srparasites_traps.features.proximity_sensor.ProximitySensorBlock;
import srparasites_traps.features.relocator.RelocatorBlock;
import srparasites_traps.features.sentry_turret.SentryTurretBlock;
import srparasites_traps.features.serrated_spikes.BurningSerratedSpikes;
import srparasites_traps.features.serrated_spikes.SerratedSpikesBlock;
import srparasites_traps.features.serrated_spikes.ViralSerratedSpikes;
import srparasites_traps.features.static_electricity_generator.StaticElectricityGeneratorBlock;
import srparasites_traps.features.tesla_coil.TeslaCoilBlock;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModBlocks {
    public static Block SENTRY_TURRET = new SentryTurretBlock();
    public static Block RELOCATOR = new RelocatorBlock();
    public static Block SERRATED_SPIKES = new SerratedSpikesBlock("serrated_spikes");
    public static Block BURNING_SERRATED_SPIKES = new BurningSerratedSpikes();
    public static Block VIRAL_SERRATED_SPIKES = new ViralSerratedSpikes();
    public static Block BIOMASS_FACTORY = new BiomassFactoryBlock();
    public static Block DECONTAMINATOR = new DecontaminatorBlock();
    public static Block OBSIDIAN_LADDER = new ObsidianLadderBlock();
    public static Block TESLA_COIL = new TeslaCoilBlock();
    public static Block PROXIMITY_SENSOR = new ProximitySensorBlock();
    public static Block STATIC_ELECTRICITY_GENERATOR = new StaticElectricityGeneratorBlock();
    public static BlockSlab OBSIDIAN_SLAB = new ObsidianSlabBlock.Half();
    public static BlockSlab OBSIDIAN_SLAB_DOUBLE = new ObsidianSlabBlock.Double();

    public static ArrayList<Block> getBlockList() {
        ArrayList<Block> blocks = new ArrayList<>();

        for (Field field : ModBlocks.class.getDeclaredFields()) {
            if (field.getType() != Block.class && field.getType() != BlockSlab.class) continue;

            try {
                Block block = (Block) field.get(null);
                blocks.add(block);
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error("Failed to get block from field {}", field.getName());
            }
        }

        return blocks;
    }
}
