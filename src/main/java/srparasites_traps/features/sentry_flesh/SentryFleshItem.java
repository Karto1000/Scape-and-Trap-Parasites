package srparasites_traps.features.sentry_flesh;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class SentryFleshItem extends Item {
    public SentryFleshItem() {
        super();

        setRegistryName(SRParasitesTraps.MOD_ID, "sentry_flesh");
        setMaxStackSize(64);
        setTranslationKey(getTranslationKeyFor("sentry_flesh"));
        setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.sentry_flesh"));
    }
}
