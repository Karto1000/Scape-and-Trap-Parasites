package srparasites_traps.registry;

import net.minecraft.potion.Potion;
import srparasites_traps.features.tesla_coil.ShockedPotion;

import java.util.List;

public class ModPotions {
    public static final Potion SHOCKED_POTION = new ShockedPotion();
    
    public static List<Potion> getPotionList() {
        return RegistryHelper.getStaticFieldsOfType(ModPotions.class, Potion.class);
    }
}
