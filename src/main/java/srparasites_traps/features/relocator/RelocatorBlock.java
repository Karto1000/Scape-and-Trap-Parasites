package srparasites_traps.features.relocator;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Constants;

import javax.annotation.Nullable;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class RelocatorBlock extends Block {
    public RelocatorBlock() {
        super(Material.IRON, MapColor.IRON);
        setRegistryName("relocator");
        setTranslationKey(getTranslationKeyFor("relocator"));
        setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new RelocatorTileEntity();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof RelocatorTileEntity) {
            if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing)) return true;
        }

        playerIn.openGui(SRParasitesTraps.instance, Constants.RELOCATOR_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
