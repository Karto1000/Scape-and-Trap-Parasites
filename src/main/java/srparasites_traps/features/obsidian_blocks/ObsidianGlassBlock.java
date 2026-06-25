package srparasites_traps.features.obsidian_blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ObsidianGlassBlock extends Block {
    public static final String REGISTRY_NAME = "obsidian_glass";

    public ObsidianGlassBlock() {
        super(Material.GLASS, MapColor.BLACK);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(2000);
        this.setHarvestLevel("pickaxe", 3);

        if (ForgeConfigHandler.obsidianBlocks.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
