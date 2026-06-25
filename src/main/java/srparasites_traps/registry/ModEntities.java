package srparasites_traps.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.relocator.RelocatorEntity;
import srparasites_traps.features.sentry_turret.SentryTurretAlafhaBall;
import srparasites_traps.features.sentry_turret.SentryTurretEntity;
import srparasites_traps.features.sentry_turret.SentryTurretSpineball;

import java.util.List;

public class ModEntities {
    public static EntityEntry SENTRY_TURRET_ENTITY = EntityEntryBuilder.create()
            .entity(SentryTurretEntity.class)
            .id(new ResourceLocation(SRParasitesTraps.MOD_ID, "sentry_turret"), 1)
            .name("sentry_turret")
            .tracker(64, 1, true)
            .build();
    public static EntityEntry SENTRY_TURRET_SPINEBALL_ENTITY = EntityEntryBuilder.create()
            .entity(SentryTurretSpineball.class)
            .id(new ResourceLocation(SRParasitesTraps.MOD_ID, "sentry_turret_spineball"), 2)
            .tracker(64, 1, true)
            .name("sentry_turret_spineball")
            .build();
    public static EntityEntry SENTRY_TURRET_ALAPHA_BALL_ENTITY = EntityEntryBuilder.create()
            .entity(SentryTurretAlafhaBall.class)
            .id(new ResourceLocation(SRParasitesTraps.MOD_ID, "sentry_turret_alapha_ball"), 3)
            .tracker(64, 1, true)
            .name("sentry_turret_alapha_ball")
            .build();
    public static EntityEntry RELOCATOR_ENTITY = EntityEntryBuilder.create()
            .entity(RelocatorEntity.class)
            .id(new ResourceLocation(SRParasitesTraps.MOD_ID, "relocator"), 4)
            .tracker(64, 1, true)
            .name("relocator")
            .build();

    public static List<EntityEntry> getEntityList() {
        return RegistryHelper.getStaticFieldsOfType(ModEntities.class, EntityEntry.class);
    }
}
