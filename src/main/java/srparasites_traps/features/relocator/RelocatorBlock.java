package srparasites_traps.features.relocator;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
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
import net.minecraftforge.fluids.FluidUtil;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.RedstoneControlHelper;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class RelocatorBlock extends Block {
    public static final String REGISTRY_NAME = "relocator";

    public RelocatorBlock() {
        super(Material.IRON, MapColor.IRON);

        setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        setHardness(50);
        setResistance(1200);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.METAL);
        if (ForgeConfigHandler.relocator.ENABLE_RELOCATOR) setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
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


    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof RelocatorTileEntity) {
            RedstoneControlHelper.setPowered((RelocatorTileEntity) tileEntity, worldIn, pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof RelocatorTileEntity) {
            ((RelocatorTileEntity) tileEntity).dropInventory();
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME));
    }
}
