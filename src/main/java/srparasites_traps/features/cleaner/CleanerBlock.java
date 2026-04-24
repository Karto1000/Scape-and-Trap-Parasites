package srparasites_traps.features.cleaner;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class CleanerBlock extends Block {
    public CleanerBlock() {
        super(Material.IRON, Material.IRON.getMaterialMapColor());

        this.setRegistryName("cleaner");
        this.setTranslationKey(getTranslationKeyFor("cleaner"));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new CleanerTileEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof CleanerTileEntity)) return;

        CleanerTileEntity cleaner = (CleanerTileEntity) tile;
        if (cleaner.getState() == CleanerState.DISPENSING) {
            // Shoot particles in a hexagonal pattern outwards
            double originX = pos.getX() + 0.5;
            double originY = pos.getY() + 0.5;
            double originZ = pos.getZ() + 0.5;

            for (int i = 0; i < 6; i++) {
                double spinAngle = (i * 2 * Math.PI) / 6 * Math.random();
                double x = Math.cos(spinAngle) * 0.2;
                double z = Math.sin(spinAngle) * 0.2;


                worldIn.spawnParticle(
                        EnumParticleTypes.CLOUD,
                        originX,
                        originY,
                        originZ,
                        x,
                        -0.05,
                        z
                );
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.cleaner"));
    }
}
