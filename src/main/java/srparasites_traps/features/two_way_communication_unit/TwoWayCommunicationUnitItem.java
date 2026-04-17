package srparasites_traps.features.two_way_communication_unit;


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

public class TwoWayCommunicationUnitItem extends Item {
    public TwoWayCommunicationUnitItem() {
        super();

        setRegistryName(SRParasitesTraps.MOD_ID, "two_way_communication_unit");
        setMaxStackSize(1);
        setTranslationKey(getTranslationKeyFor("two_way_communication_unit"));
        setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.two_way_communication_unit"));
    }
}
