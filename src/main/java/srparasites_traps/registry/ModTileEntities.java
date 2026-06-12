package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.biomass_factory.BiomassFactoryBlock;
import srparasites_traps.features.biomass_factory.BiomassFactoryTileEntity;
import srparasites_traps.features.decontaminator.DecontaminatorBlock;
import srparasites_traps.features.decontaminator.DecontaminatorTileEntity;
import srparasites_traps.features.proximity_sensor.ProximitySensorBlock;
import srparasites_traps.features.proximity_sensor.ProximitySensorTileEntity;
import srparasites_traps.features.relocator.RelocatorBlock;
import srparasites_traps.features.relocator.RelocatorTileEntity;
import srparasites_traps.features.sentry_turret.SentryTurretBlock;
import srparasites_traps.features.sentry_turret.SentryTurretTileEntity;
import srparasites_traps.features.tesla_coil.TeslaCoilBlock;
import srparasites_traps.features.tesla_coil.TeslaCoilTileEntity;

import java.util.Arrays;
import java.util.List;

public class ModTileEntities {
    public static final List<TileEntityRegistration> TILE_ENTITIES = Arrays.asList(
            new TileEntityRegistration(SentryTurretTileEntity.class, ModBlocks.SENTRY_TURRET, SentryTurretBlock.REGISTRY_NAME),
            new TileEntityRegistration(RelocatorTileEntity.class, ModBlocks.RELOCATOR, RelocatorBlock.REGISTRY_NAME),
            new TileEntityRegistration(BiomassFactoryTileEntity.class, ModBlocks.BIOMASS_FACTORY, BiomassFactoryBlock.REGISTRY_NAME),
            new TileEntityRegistration(DecontaminatorTileEntity.class, ModBlocks.DECONTAMINATOR, DecontaminatorBlock.REGISTRY_NAME),
            new TileEntityRegistration(TeslaCoilTileEntity.class, ModBlocks.TESLA_COIL, TeslaCoilBlock.REGISTRY_NAME),
            new TileEntityRegistration(ProximitySensorTileEntity.class, ModBlocks.PROXIMITY_SENSOR, ProximitySensorBlock.REGISTRY_NAME)
    );

    public static void registerTileEntities() {
        for (TileEntityRegistration tileEntity : TILE_ENTITIES) {
            GameRegistry.registerTileEntity(tileEntity.tileEntityClass, SRParasitesTraps.MOD_ID + ":" + tileEntity.registryName + "_tile_entity");
        }
    }

    public static class TileEntityRegistration {
        public final Class<? extends TileEntity> tileEntityClass;
        public final Block block;
        public final String registryName;

        public TileEntityRegistration(Class<? extends TileEntity> tileEntityClass, Block block, String registryName) {
            this.tileEntityClass = tileEntityClass;
            this.block = block;
            this.registryName = registryName;
        }
    }
}
