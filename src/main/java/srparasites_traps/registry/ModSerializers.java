package srparasites_traps.registry;

import net.minecraft.network.datasync.DataSerializers;

import static srparasites_traps.util.Serializers.LONG;

public class ModSerializers {
    public static void registerSerializers() {
        DataSerializers.registerSerializer(LONG);
    }
}
