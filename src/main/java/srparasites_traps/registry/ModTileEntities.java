package srparasites_traps.registry;

import net.minecraftforge.fml.common.registry.GameRegistry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.sentry_turret.base.SentryTurretBaseTileEntity;

public class ModTileEntities {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(SentryTurretBaseTileEntity.class, SRParasitesTraps.MOD_ID + ":sentry_turret_tile_entity");
    }
}
