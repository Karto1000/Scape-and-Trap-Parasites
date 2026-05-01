package srparasites_traps.registry;

import net.minecraft.potion.Potion;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.tesla_coil.ShockedPotion;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModPotions {
    public static final Potion SHOCKED_POTION = new ShockedPotion();
    
    public static ArrayList<Potion> getPotionList() {
        ArrayList<Potion> potions = new ArrayList<>();

        for (Field field : ModPotions.class.getDeclaredFields()) {
            if (field.getType() != Potion.class) continue;

            try {
                Potion potion = (Potion) field.get(null);
                potions.add(potion);
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error("Failed to get potion from field {}", field.getName());
            }
        }

        return potions;
    }
}
