package srparasites_traps.registry;

import srparasites_traps.SRParasitesTraps;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class RegistryHelper {
    private RegistryHelper() {
    }

    public static <T> List<T> getStaticFieldsOfType(Class<?> registryClass, Class<T> entryType) {
        List<T> entries = new ArrayList<T>();

        for (Field field : registryClass.getDeclaredFields()) {
            if (!entryType.isAssignableFrom(field.getType())) continue;

            try {
                entries.add(entryType.cast(field.get(null)));
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error(
                        "Failed to get {} from field {} in {}",
                        entryType.getSimpleName(),
                        field.getName(),
                        registryClass.getSimpleName()
                );
            }
        }

        return entries;
    }
}
