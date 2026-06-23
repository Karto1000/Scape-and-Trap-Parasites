package srparasites_traps.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import srparasites_traps.SRParasitesTraps;

import java.util.List;

public class ModSounds {
    public static final SoundEvent DECONTAMINATOR_SPRAY = createSoundEvent("decontaminator_spray");
    public static final SoundEvent DECONTAMINATOR_OPEN = createSoundEvent("decontaminator_open");
    public static final SoundEvent DECONTAMINATOR_CLOSE = createSoundEvent("decontaminator_close");
    public static final SoundEvent TESLA_COIL_FIRE = createSoundEvent("tesla_coil_fire");
    public static final SoundEvent ELECTRIC_ARC = createSoundEvent("electric_arc");
    public static final SoundEvent TESLA_COIL_CHARGE = createSoundEvent("tesla_coil_charge");
    public static final SoundEvent BIOMASS_FACTORY_WORK = createSoundEvent("biomass_factory_work");

    private static SoundEvent createSoundEvent(String name) {
        return new SoundEvent(new ResourceLocation(SRParasitesTraps.MOD_ID, name))
                .setRegistryName(new ResourceLocation(SRParasitesTraps.MOD_ID, name));
    }

    public static List<SoundEvent> getSoundList() {
        return RegistryHelper.getStaticFieldsOfType(ModSounds.class, SoundEvent.class);
    }
}
