package srparasites_traps.features.decontaminator;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class DecontaminatorBlock extends Block {
    public static final PropertyDirection GRATE_DIRECTION = PropertyDirection.create("grate_direction");
    public static final PropertyEnum<DecontaminatorState> DECONTAMINATOR_STATE = PropertyEnum.create("state", DecontaminatorState.class);
    public static final String REGISTRY_NAME = "decontaminator";

    public DecontaminatorBlock() {
        super(Material.IRON, Material.IRON.getMaterialMapColor());

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(GRATE_DIRECTION, EnumFacing.NORTH)
                .withProperty(DECONTAMINATOR_STATE, DecontaminatorState.IDLE)
        );

        if (ForgeConfigHandler.decontaminator.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }


    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, GRATE_DIRECTION, DECONTAMINATOR_STATE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(DecontaminatorBlock.GRATE_DIRECTION);
        return facing.getIndex();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);
        return this.getDefaultState().withProperty(GRATE_DIRECTION, facing);
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, @Nonnull BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof DecontaminatorTileEntity) {
            DecontaminatorTileEntity cte = (DecontaminatorTileEntity) tileEntity;
            return state.withProperty(DECONTAMINATOR_STATE, cte.getState());
        }
        return state;
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
        EnumFacing playerFacing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        return this.getDefaultState().withProperty(GRATE_DIRECTION, playerFacing);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new DecontaminatorTileEntity();
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME));
    }
}
