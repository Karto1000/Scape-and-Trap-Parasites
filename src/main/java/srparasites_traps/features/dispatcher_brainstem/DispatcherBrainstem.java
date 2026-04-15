package srparasites_traps.features.dispatcher_brainstem;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class DispatcherBrainstem extends Item {
    public DispatcherBrainstem() {
        super();

        setMaxStackSize(16);
        setRegistryName("dispatcher_brainstem");
        setTranslationKey(getTranslationKeyFor("dispatcher_brainstem"));
        setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.dispatcher_brainstem"));
    }
}
