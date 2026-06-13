package srparasites_traps.features.serrated_spikes;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;

public class ViralSerratedSpikes extends SerratedSpikesBlock {
    public static final String REGISTRY_NAME = "viral_serrated_spikes";

    public ViralSerratedSpikes() {
        super(REGISTRY_NAME);
    }

    @Override
    protected void damageEntity(Entity entity, float damage) {
        super.damageEntity(entity, damage);

        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).addPotionEffect(
                    new PotionEffect(
                            SRPPotions.VIRA_E,
                            ForgeConfigHandler.serratedSpikes.DEFAULT_VIRAL_EFFECT_DURATION,
                            ForgeConfigHandler.serratedSpikes.DEFAULT_VIRAL_AMPLIFIER
                    )
            );
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME));
    }
}
