package srparasites_traps.features.obsidian_blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nullable;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BleedingObsidianGlassBlock extends Block {
    public static final String REGISTRY_NAME = "bleeding_obsidian_glass";

    public BleedingObsidianGlassBlock() {
        super(Material.GLASS);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(2.3F);

        if (ForgeConfigHandler.obsidianBlocks.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public SoundType getSoundType(
            IBlockState state,
            World world,
            BlockPos pos,
            @Nullable Entity entity
    ) {
        return SoundType.GLASS;
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