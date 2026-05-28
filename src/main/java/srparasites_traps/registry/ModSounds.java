package srparasites_traps.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import srparasites_traps.SRParasitesTraps;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModSounds {
    public static final SoundEvent DECONTAMINATOR_SPRAY = createSoundEvent("decontaminator_spray");
    public static final SoundEvent DECONTAMINATOR_OPEN = createSoundEvent("decontaminator_open");
    public static final SoundEvent DECONTAMINATOR_CLOSE = createSoundEvent("decontaminator_close");
    public static final SoundEvent TESLA_COIL_FIRE = createSoundEvent("tesla_coil_fire");
    public static final SoundEvent ELECTRIC_ARC = createSoundEvent("electric_arc");
    public static final SoundEvent TESLA_COIL_CHARGE = createSoundEvent("tesla_coil_charge");

    private static SoundEvent createSoundEvent(String name) {
        return new SoundEvent(new ResourceLocation(SRParasitesTraps.MOD_ID, name))
                .setRegistryName(new ResourceLocation(SRParasitesTraps.MOD_ID, name));
    };

    public static ArrayList<SoundEvent> getSoundList() {
        ArrayList<SoundEvent> sounds = new ArrayList<>();

        for (Field field : ModSounds.class.getDeclaredFields()) {
            if (field.getType() != SoundEvent.class) continue;

            try {
                SoundEvent sound = (SoundEvent) field.get(null);
                sounds.add(sound);
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error("Failed to get sound from field {}", field.getName());
            }
        }

        return sounds;
    }
}
