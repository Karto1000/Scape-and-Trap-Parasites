package srparasites_traps.features.biomass_pit;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import srparasites_traps.SRParasitesTraps;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BiomassPit extends Block {
    public BiomassPit() {
        super(Material.CLAY, MapColor.CLAY);

        this.setRegistryName("biomass_pit");
        this.setTranslationKey(getTranslationKeyFor("biomass_pit"));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }
}
