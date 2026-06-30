package srparasites_traps.features.biomass_factory;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BiomassFactoryBlock extends Block {
    public static final String REGISTRY_NAME = "biomass_factory";
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BiomassFactoryBlock() {
        super(Material.SPONGE, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));

        if (ForgeConfigHandler.biomassFactory.ENABLE)
            this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = meta == 1;
        return this.getDefaultState().withProperty(ACTIVE, active);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public void addInformation(
            @Nonnull ItemStack stack,
            @Nullable World worldIn,
            List<String> tooltip,
            @Nonnull ITooltipFlag flagIn
    ) {
        Translation.addMultilineTooltip(tooltip, "item." + REGISTRY_NAME);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(
            @Nonnull World world,
            @Nonnull IBlockState state
    ) {
        return new BiomassFactoryTileEntity();
    }

    @Override
    public void breakBlock(
            World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull IBlockState state
    ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof BiomassFactoryTileEntity) {
            ((BiomassFactoryTileEntity) tileEntity).dropInventory();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(
            IBlockState stateIn,
            @Nonnull World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull Random rand
    ) {
        if (!stateIn.getValue(ACTIVE)) return;

        worldIn.spawnParticle(
                EnumParticleTypes.SMOKE_LARGE,
                pos.getX() + 0.8,
                pos.getY() + 1.4,
                pos.getZ() + 0.8,
                (worldIn.rand.nextDouble() - 0.5) * 0.1,
                0.1,
                (worldIn.rand.nextDouble() - 0.5) * 0.1
        );
    }

    @Override
    public boolean onBlockActivated(
            World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull IBlockState state,
            @Nonnull EntityPlayer playerIn,
            @Nonnull EnumHand hand,
            @Nonnull EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ
    ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof BiomassFactoryTileEntity)) return false;
        BiomassFactoryTileEntity bft = (BiomassFactoryTileEntity) tileEntity;

        ItemStack heldItem = playerIn.getHeldItem(hand);
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(heldItem);
        if (fluidHandler != null) {
            FluidUtil.interactWithFluidHandler(playerIn, hand, bft.biomassStorage);
            return true;
        }

        if (worldIn.isRemote) return true;

        playerIn.openGui(SRParasitesTraps.instance, Constants.BIOMASS_FACTORY_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
