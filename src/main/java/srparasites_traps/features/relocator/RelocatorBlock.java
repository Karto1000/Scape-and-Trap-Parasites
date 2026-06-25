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
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.RedstoneControlHelper;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
        if (ForgeConfigHandler.relocator.ENABLE) setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new RelocatorTileEntity();
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof RelocatorTileEntity) {
            if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing)) return true;
        }

        playerIn.openGui(SRParasitesTraps.instance, Constants.RELOCATOR_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof RelocatorTileEntity) {
            RedstoneControlHelper.updatePower((RelocatorTileEntity) tileEntity, worldIn, pos);
        }
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
        if (worldIn.isRemote) return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof RelocatorTileEntity) {
            RedstoneControlHelper.updatePower((RelocatorTileEntity) tileEntity, worldIn, pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof RelocatorTileEntity) {
            ((RelocatorTileEntity) tileEntity).dropInventory();
            ((RelocatorTileEntity) tileEntity).dropAugments(worldIn, pos);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        Translation.addMultilineTooltip(tooltip, "item." + REGISTRY_NAME);
    }
}
