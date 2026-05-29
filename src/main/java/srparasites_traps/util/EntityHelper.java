package srparasites_traps.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.*;
import net.minecraft.entity.Entity;

import java.util.Optional;

public class EntityHelper {
    public static Optional<String> getEntityParasiteType(Class<? extends Entity> entityClass) {
        if (EntityPCrude.class.isAssignableFrom(entityClass)) {
            return Optional.of("crude");
        } else if (EntityPPrimitive.class.isAssignableFrom(entityClass)) {
            return Optional.of("primitive");
        } else if (EntityPInfected.class.isAssignableFrom(entityClass)) {
            return Optional.of("infected");
        } else if (EntityPDerived.class.isAssignableFrom(entityClass)) {
            return Optional.of("derived");
        } else if (EntityPFocused.class.isAssignableFrom(entityClass)) {
            return Optional.of("focused");
        } else if (EntityPFeral.class.isAssignableFrom(entityClass)) {
            return Optional.of("feral");
        } else if (EntityPAdapted.class.isAssignableFrom(entityClass)) {
            return Optional.of("adapted");
        } else if (EntityPPure.class.isAssignableFrom(entityClass)) {
            return Optional.of("pure");
        } else if (EntityPHijacked.class.isAssignableFrom(entityClass)) {
            return Optional.of("hijacked");
        } else if (EntityPAncient.class.isAssignableFrom(entityClass)) {
            return Optional.of("ancient");
        }

        return Optional.empty();
    }
}
