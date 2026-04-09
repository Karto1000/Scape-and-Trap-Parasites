package srparasites_traps.registry;

import net.minecraftforge.fml.common.registry.GameRegistry;
import srparasites_traps.features.sentry_turret.base.SentryTurretTileEntity;

public class ModTileEntities {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(SentryTurretTileEntity.class, "sentry_turret_tile_entity");
    }
}
