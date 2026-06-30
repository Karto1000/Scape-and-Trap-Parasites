package srparasites_traps.features.infested_beacon;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class CurePotion extends Potion {
    public static final String REGISTRY_NAME = "cure";

    public CurePotion() {
        super(false, 0x1100FBFF);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setPotionName("potion." + getTranslationKeyFor(REGISTRY_NAME));
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void affectEntity(
            @Nullable Entity source,
            @Nullable Entity indirectSource,
            @Nonnull EntityLivingBase entityLivingBaseIn,
            int amplifier,
            double health
    ) {
        if (entityLivingBaseIn.world.isRemote) return;
        if (!(entityLivingBaseIn instanceof EntityPlayer)) return;

        entityLivingBaseIn.removePotionEffect(SRPPotions.VIRA_E);
        entityLivingBaseIn.removePotionEffect(SRPPotions.BLEED_E);
    }

    public static boolean hasCurableEffect(EntityLivingBase entity) {
        return entity.getActivePotionEffect(SRPPotions.VIRA_E) != null ||
                entity.getActivePotionEffect(SRPPotions.BLEED_E) != null;
    }
}
