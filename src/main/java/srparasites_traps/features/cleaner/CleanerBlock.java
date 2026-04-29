package srparasites_traps.features.cleaner;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class CleanerBlock extends Block {
    public static final PropertyDirection grateDirection = PropertyDirection.create("grate_direction");
    public static final PropertyEnum<CleanerState> cleanerState = PropertyEnum.create("state", CleanerState.class);

    public CleanerBlock() {
        super(Material.IRON, Material.IRON.getMaterialMapColor());

        this.setRegistryName(SRParasitesTraps.MOD_ID, "cleaner");
        this.setTranslationKey(getTranslationKeyFor("cleaner"));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(grateDirection, EnumFacing.NORTH)
                .withProperty(cleanerState, CleanerState.IDLE)
        );

        if (ForgeConfigHandler.cleaner.ENABLE_CLEANER) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, grateDirection, cleanerState);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(CleanerBlock.grateDirection);
        return facing.getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);
        return this.getDefaultState().withProperty(grateDirection, facing);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof CleanerTileEntity) {
            CleanerTileEntity cte = (CleanerTileEntity) tileEntity;
            return state.withProperty(cleanerState, cte.getState());
        }
        return state;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing playerFacing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        return this.getDefaultState().withProperty(grateDirection, playerFacing);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.cleaner"));
    }
}
