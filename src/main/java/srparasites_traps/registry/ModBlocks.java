package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import srparasites_traps.features.barbed_wire.BasicBarbedWire;
import srparasites_traps.features.barbed_wire.BurningBarbedWire;
import srparasites_traps.features.barbed_wire.ViralBarbedWire;
import srparasites_traps.features.biomass_factory.BeckonNidusBlock;
import srparasites_traps.features.biomass_factory.BiomassFactoryBlock;
import srparasites_traps.features.decontaminator.DecontaminatorBlock;
import srparasites_traps.features.infested_beacon.InfestedBeaconBlock;
import srparasites_traps.features.obsidian_blocks.*;
import srparasites_traps.features.proximity_sensor.ProximitySensorBlock;
import srparasites_traps.features.relocator.RelocatorBlock;
import srparasites_traps.features.sentry_turret.SentryTurretBlock;
import srparasites_traps.features.serrated_spikes.BurningSerratedSpikes;
import srparasites_traps.features.serrated_spikes.SerratedSpikesBlock;
import srparasites_traps.features.serrated_spikes.ViralSerratedSpikes;
import srparasites_traps.features.tesla_coil.StaticElectricityGeneratorBlock;
import srparasites_traps.features.tesla_coil.TeslaCoilBlock;

import java.util.List;

public class ModBlocks {
    public static final Block SENTRY_TURRET = new SentryTurretBlock();
    public static final Block RELOCATOR = new RelocatorBlock();
    public static final Block SERRATED_SPIKES = new SerratedSpikesBlock("serrated_spikes");
    public static final Block BURNING_SERRATED_SPIKES = new BurningSerratedSpikes();
    public static final Block VIRAL_SERRATED_SPIKES = new ViralSerratedSpikes();
    public static final Block BIOMASS_FACTORY = new BiomassFactoryBlock();
    public static final Block DECONTAMINATOR = new DecontaminatorBlock();
    public static final Block OBSIDIAN_LADDER = new ObsidianLadderBlock(ObsidianLadderBlock.REGISTRY_NAME);
    public static final Block TESLA_COIL = new TeslaCoilBlock();
    public static final Block PROXIMITY_SENSOR = new ProximitySensorBlock();
    public static final Block STATIC_ELECTRICITY_GENERATOR = new StaticElectricityGeneratorBlock();
    public static final Block BECKON_NIDUS = new BeckonNidusBlock();
    public static final Block OBSIDIAN_GLASS = new ObsidianGlassBlock();
    public static final Block BARBED_WIRE = new BasicBarbedWire();
    public static final Block BURNING_BARBED_WIRE = new BurningBarbedWire();
    public static final Block VIRAL_BARBED_WIRE = new ViralBarbedWire();
    public static final Block INFESTED_BEACON = new InfestedBeaconBlock();
    public static final BlockSlab OBSIDIAN_SLAB = new ObsidianSlabBlock.Half();
    public static final BlockSlab OBSIDIAN_SLAB_DOUBLE = new ObsidianSlabBlock.Double();
    public static final Block OBSIDIAN_STAIRS = new ObsidianStairsBlock();
    public static final Block BLEEDING_OBSIDIAN_GLASS = new BleedingObsidianGlassBlock();
    public static final Block BLEEDING_OBSIDIAN_LADDER = new BleedingObsidianLadderBlock();

    public static List<Block> getBlockList() {
        return RegistryHelper.getStaticFieldsOfType(ModBlocks.class, Block.class);
    }
}
