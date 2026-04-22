package srparasites_traps.features.biomass_factory;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BiomassFactoryBlock extends Block {
    public BiomassFactoryBlock() {
        super(Material.SPONGE, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, "biomass_factory");
        this.setTranslationKey(getTranslationKeyFor("biomass_factory"));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.biomass_factory"));
    }
}
