package srparasites_traps.features.serrated_spikes;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;

public class FlameCoatedSerratedSpikes extends SerratedSpikesBlock {
    public static final String REGISTRY_NAME = "flame_coated_serrated_spikes";

    public FlameCoatedSerratedSpikes() {
        super(REGISTRY_NAME);
    }

    @Override
    protected void damageEntity(Entity entity, float damage) {
        super.damageEntity(entity, damage);
        entity.setFire(ForgeConfigHandler.serratedSpikes.DEFAULT_FLAME_COATED_SERRATED_SPIKES_FLAME_DURATION);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME));
    }
}
