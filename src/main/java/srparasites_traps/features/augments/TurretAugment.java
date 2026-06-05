package srparasites_traps.features.augments;

import cofh.api.item.IAugmentItem;
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

public class TurretAugment extends Item implements IAugmentItem {
    public final String registryName;

    public TurretAugment(String registryName) {
        super();

        this.registryName = registryName;

        this.setRegistryName(SRParasitesTraps.MOD_ID, registryName);
        this.setTranslationKey(getTranslationKeyFor(registryName));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public AugmentType getAugmentType(ItemStack itemStack) {
        return AugmentType.BASIC;
    }

    @Override
    public String getAugmentIdentifier(ItemStack itemStack) {
        return this.registryName;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + this.registryName));
    }
}
