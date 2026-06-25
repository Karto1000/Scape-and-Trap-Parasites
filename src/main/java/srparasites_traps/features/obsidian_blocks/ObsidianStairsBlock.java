package srparasites_traps.features.obsidian_blocks;

import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ObsidianStairsBlock extends BlockStairs {
    public static final String REGISTRY_NAME = "obsidian_stairs";

    public ObsidianStairsBlock() {
        super(Blocks.OBSIDIAN.getDefaultState());

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50.0F);
        this.setResistance(2000.0F);
        this.setHarvestLevel("pickaxe", 3);
        this.setSoundType(Blocks.OBSIDIAN.getSoundType());
        this.useNeighborBrightness = true;

        if (ForgeConfigHandler.obsidianBlocks.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
        else this.setCreativeTab(null);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }
}
