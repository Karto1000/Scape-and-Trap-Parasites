package srparasites_traps.registry;

import net.minecraftforge.fml.common.registry.GameRegistry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.biomass_factory.BiomassFactoryTileEntity;
import srparasites_traps.features.cleaner.CleanerTileEntity;
import srparasites_traps.features.relocator.RelocatorTileEntity;
import srparasites_traps.features.sentry_turret.base.SentryTurretTileEntity;
import srparasites_traps.features.tesla_coil.TeslaCoilTileEntity;

public class ModTileEntities {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(SentryTurretTileEntity.class, SRParasitesTraps.MOD_ID + ":sentry_turret_tile_entity");
        GameRegistry.registerTileEntity(RelocatorTileEntity.class, SRParasitesTraps.MOD_ID + ":relocator_tile_entity");
        GameRegistry.registerTileEntity(BiomassFactoryTileEntity.class, SRParasitesTraps.MOD_ID + ":biomass_factory_tile_entity");
        GameRegistry.registerTileEntity(CleanerTileEntity.class, SRParasitesTraps.MOD_ID + ":cleaner_tile_entity");
        GameRegistry.registerTileEntity(TeslaCoilTileEntity.class, SRParasitesTraps.MOD_ID + ":tesla_coil_tile_entity");
    }
}
