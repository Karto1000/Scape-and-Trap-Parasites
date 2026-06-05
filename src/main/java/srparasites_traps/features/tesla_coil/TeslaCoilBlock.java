package srparasites_traps.features.tesla_coil;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.RedstoneControlHelper;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class TeslaCoilBlock extends Block {
    public static final String REGISTRY_NAME = "tesla_coil";

    public TeslaCoilBlock() {
        super(Material.IRON, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);

        if (ForgeConfigHandler.teslaCoil.ENABLE_TESLA_COIL) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TeslaCoilTileEntity();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TeslaCoilTileEntity) {
            RedstoneControlHelper.updatePower((TeslaCoilTileEntity) tileEntity, worldIn, pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TeslaCoilTileEntity) {
            RedstoneControlHelper.updatePower((TeslaCoilTileEntity) tileEntity, worldIn, pos);
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        playerIn.openGui(SRParasitesTraps.instance, Constants.TESLA_COIL_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TeslaCoilTileEntity) {
            TeslaCoilTileEntity teslaCoilTileEntity = (TeslaCoilTileEntity) tileEntity;
            teslaCoilTileEntity.dropAugments(worldIn, pos);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME));
    }
}
