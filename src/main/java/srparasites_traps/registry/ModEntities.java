package srparasites_traps.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.sentry_turret.SentryTurretEntity;
import srparasites_traps.features.sentry_turret.SentryTurretSpineball;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModEntities {
    public static EntityEntry SENTRY_TURRET_ENTITY = EntityEntryBuilder.create()
            .entity(SentryTurretEntity.class)
            .id(new ResourceLocation(SRParasitesTraps.MOD_ID, "sentry_turret"), 1)
            .name("sentry_turret")
            .tracker(64, 1, true)
            .egg(0, 0xFFFFFF)
            .build();
    public static EntityEntry SENTRY_TURRET_SPINEBALL_ENTITY = EntityEntryBuilder.create()
            .entity(SentryTurretSpineball.class)
            .id(new ResourceLocation(SRParasitesTraps.MOD_ID, "sentry_turret_spineball"), 2)
            .tracker(64, 1, true)
            .name("sentry_turret_spineball")
            .build();

    public static ArrayList<EntityEntry> getEntityList() {
        ArrayList<EntityEntry> entities = new ArrayList<>();

        for (Field field : ModEntities.class.getDeclaredFields()) {
            if (field.getType() != EntityEntry.class) continue;

            try {
                EntityEntry entity = (EntityEntry) field.get(null);
                entities.add(entity);
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error("Failed to get entity entry from field {}", field.getName());
            }
        }

        return entities;
    }
}
