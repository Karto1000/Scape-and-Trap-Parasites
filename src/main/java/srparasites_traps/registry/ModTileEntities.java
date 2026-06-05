package srparasites_traps.registry;

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
import srparasites_traps.features.sentry_turret.base.SentryTurretBlock;
import srparasites_traps.features.sentry_turret.base.SentryTurretTileEntity;
import srparasites_traps.features.static_electricity_generator.StaticElectricityGeneratorBlock;
import srparasites_traps.features.static_electricity_generator.StaticElectricityGeneratorTileEntity;
import srparasites_traps.features.tesla_coil.TeslaCoilBlock;
import srparasites_traps.features.tesla_coil.TeslaCoilTileEntity;

public class ModTileEntities {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(SentryTurretTileEntity.class, SRParasitesTraps.MOD_ID + ":" + SentryTurretBlock.REGISTRY_NAME + "_tile_entity");
        GameRegistry.registerTileEntity(RelocatorTileEntity.class, SRParasitesTraps.MOD_ID + ":" + RelocatorBlock.REGISTRY_NAME + "_tile_entity");
        GameRegistry.registerTileEntity(BiomassFactoryTileEntity.class, SRParasitesTraps.MOD_ID + ":" + BiomassFactoryBlock.REGISTRY_NAME + "_tile_entity");
        GameRegistry.registerTileEntity(DecontaminatorTileEntity.class, SRParasitesTraps.MOD_ID + ":" + DecontaminatorBlock.REGISTRY_NAME + "_tile_entity");
        GameRegistry.registerTileEntity(TeslaCoilTileEntity.class, SRParasitesTraps.MOD_ID + ":" + TeslaCoilBlock.REGISTRY_NAME + "_tile_entity");
        GameRegistry.registerTileEntity(ProximitySensorTileEntity.class, SRParasitesTraps.MOD_ID + ":" + ProximitySensorBlock.REGISTRY_NAME + "_tile_entity");
        GameRegistry.registerTileEntity(StaticElectricityGeneratorTileEntity.class, SRParasitesTraps.MOD_ID + ":" + StaticElectricityGeneratorBlock.REGISTRY_NAME + "_tile_entity");
    }
}
