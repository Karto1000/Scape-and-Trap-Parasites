package srparasites_traps.features;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BasicItem extends Item {
    private final String registryName;
    private EnumRarity rarity = EnumRarity.COMMON;

    public BasicItem(String registryName) {
        this.setRegistryName(SRParasitesTraps.MOD_ID, registryName);
        this.setTranslationKey(getTranslationKeyFor(registryName));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
        this.registryName = registryName;
    }

    public BasicItem maxStackSize(int maxStackSize) {
        this.setMaxStackSize(maxStackSize);
        return this;
    }

    public BasicItem rarity(EnumRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public EnumRarity getRarity() {
        return rarity;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + this.registryName));
    }
}
