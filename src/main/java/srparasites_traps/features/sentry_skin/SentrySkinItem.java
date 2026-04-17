package srparasites_traps.features.sentry_skin;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class SentrySkinItem extends Item {
    public SentrySkinItem() {
        super();

        setRegistryName(SRParasitesTraps.MOD_ID, "sentry_skin");
        setMaxStackSize(16);
        setTranslationKey(getTranslationKeyFor("sentry_skin"));
        setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.sentry_skin"));
    }
}
