package srparasites_traps.mixin.srpmixin;

import com.dhanantry.scapeandrunparasites.block.slabs.BlockSlabRubble;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import srparasites_traps.registry.ModBlocks;

import java.util.Random;

import static com.dhanantry.scapeandrunparasites.init.SRPBlocks.ParasiteRubbleObsidianStair;

@Mixin(BiomeParasiteBase.class)
public class BiomeObsidianSpread {
    @Inject(
            method = "convertBlock",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    public void srparasites_traps_convert_block(
            BlockPos helper,
            World worldIn,
            Random rand,
            CallbackInfoReturnable<Integer> cir
    ) {
        IBlockState lookingState = worldIn.getBlockState(helper);
        Block lookingBlock = lookingState.getBlock();

        if (lookingBlock == ModBlocks.OBSIDIAN_SLAB) {
            if (lookingState.getPropertyKeys().contains(BlockSlabRubble.HALF)) {
                BlockSlab.EnumBlockHalf half = lookingState.getValue(BlockSlabRubble.HALF);

                worldIn.setBlockState(
                        helper,
                        SRPBlocks.ParasiteRubbleSlabHalf
                                .getDefaultState()
                                .withProperty(BlockSlabRubble.VARIANT, BlockSlabRubble.EnumType.OBSIDIAN)
                                .withProperty(BlockSlabRubble.HALF, half)
                );

                cir.setReturnValue(1);
                return;
            }
        }

        if (lookingBlock == ModBlocks.OBSIDIAN_SLAB_DOUBLE) {
            IBlockState parasiteState = SRPBlocks.ParasiteRubbleSlabDouble
                    .getDefaultState()
                    .withProperty(BlockSlabRubble.VARIANT, BlockSlabRubble.EnumType.OBSIDIAN);

            worldIn.setBlockState(helper, parasiteState, 2);
            cir.setReturnValue(1);
            return;
        }

        if (lookingBlock == ModBlocks.OBSIDIAN_STAIRS) {
            IBlockState parasiteState = ParasiteRubbleObsidianStair
                    .getDefaultState()
                    .withProperty(BlockStairs.FACING, lookingState.getValue(BlockStairs.FACING))
                    .withProperty(BlockStairs.HALF, lookingState.getValue(BlockStairs.HALF))
                    .withProperty(BlockStairs.SHAPE, lookingState.getValue(BlockStairs.SHAPE));

            worldIn.setBlockState(helper, parasiteState, 2);
            cir.setReturnValue(1);
            return;
        }

        if (lookingBlock == ModBlocks.OBSIDIAN_GLASS) {
            IBlockState parasiteState = ModBlocks.BLEEDING_OBSIDIAN_GLASS.getDefaultState();
            worldIn.setBlockState(helper, parasiteState, 2);
            cir.setReturnValue(1);
            return;
        }

        if (lookingBlock == ModBlocks.OBSIDIAN_LADDER) {
            IBlockState parasiteState = ModBlocks.BLEEDING_OBSIDIAN_LADDER.getDefaultState()
                    .withProperty(BlockLadder.FACING, lookingState.getValue(BlockLadder.FACING));
            worldIn.setBlockState(helper, parasiteState, 2);
            cir.setReturnValue(1);
        }
    }
}
