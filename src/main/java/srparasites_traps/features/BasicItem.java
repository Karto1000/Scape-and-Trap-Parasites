package srparasites_traps.features;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!I18n.hasKey("tooltip." + SRParasitesTraps.MOD_ID + ".item." + this.registryName)) return;
        Translation.addMultilineTooltip(tooltip, "item." + this.registryName);
    }
}
