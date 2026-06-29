package srparasites_traps.features.barbed_wire;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.handlers.SlowdownHandler;
import srparasites_traps.registry.ModSounds;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public abstract class BarbedWireBlock extends Block {
    public static PropertyBool NORTH = PropertyBool.create("north");
    public static PropertyBool EAST = PropertyBool.create("east");
    public static PropertyBool SOUTH = PropertyBool.create("south");
    public static PropertyBool WEST = PropertyBool.create("west");
    public final String registryName;

    public BarbedWireBlock(String registryName) {
        super(Material.IRON, MapColor.GRAY);
        this.registryName = registryName;

        this.setRegistryName(SRParasitesTraps.MOD_ID, registryName);
        this.setTranslationKey(getTranslationKeyFor(registryName));
        this.setHardness(ForgeConfigHandler.barbedWire.HARDNESS);
        this.setResistance(ForgeConfigHandler.barbedWire.RESISTANCE);
        this.setHarvestLevel("pickaxe", 1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, true).withProperty(SOUTH, false).withProperty(WEST, true));

        if (ForgeConfigHandler.barbedWire.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int num = 0b0000;
        if (state.getValue(NORTH)) num |= 0b0001;
        if (state.getValue(EAST)) num |= 0b0010;
        if (state.getValue(SOUTH)) num |= 0b0100;
        if (state.getValue(WEST)) num |= 0b1000;
        return num;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean isNorth = (meta & 0b0001) == 0b0001;
        boolean isEast = (meta & 0b0010) == 0b0010;
        boolean isSouth = (meta & 0b0100) == 0b0100;
        boolean isWest = (meta & 0b1000) == 0b1000;
        return this.getDefaultState().withProperty(NORTH, isNorth).withProperty(EAST, isEast).withProperty(SOUTH, isSouth).withProperty(WEST, isWest);
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(@Nonnull IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return false;
    }

    @Override
    public SoundType getSoundType(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity entity) {
        return ModSounds.BARBED_WIRE_ST;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
        boolean isNorthBarbedWire = BarbedWireBlock.class.isAssignableFrom(world.getBlockState(pos.north()).getBlock().getClass());
        boolean isEastBarbedWire = BarbedWireBlock.class.isAssignableFrom(world.getBlockState(pos.east()).getBlock().getClass());
        boolean isSouthBarbedWire = BarbedWireBlock.class.isAssignableFrom(world.getBlockState(pos.south()).getBlock().getClass());
        boolean isWestBarbedWire = BarbedWireBlock.class.isAssignableFrom(world.getBlockState(pos.west()).getBlock().getClass());

        return this.getDefaultState()
                .withProperty(NORTH, isNorthBarbedWire)
                .withProperty(EAST, isEastBarbedWire)
                .withProperty(SOUTH, isSouthBarbedWire)
                .withProperty(WEST, isWestBarbedWire);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
        boolean isNorthBarbedWire = BarbedWireBlock.class.isAssignableFrom(worldIn.getBlockState(pos.north()).getBlock().getClass());
        boolean isEastBarbedWire = BarbedWireBlock.class.isAssignableFrom(worldIn.getBlockState(pos.east()).getBlock().getClass());
        boolean isSouthBarbedWire = BarbedWireBlock.class.isAssignableFrom(worldIn.getBlockState(pos.south()).getBlock().getClass());
        boolean isWestBarbedWire = BarbedWireBlock.class.isAssignableFrom(worldIn.getBlockState(pos.west()).getBlock().getClass());

        IBlockState newState = state.
                withProperty(NORTH, isNorthBarbedWire)
                .withProperty(EAST, isEastBarbedWire)
                .withProperty(SOUTH, isSouthBarbedWire)
                .withProperty(WEST, isWestBarbedWire);

        worldIn.setBlockState(pos, newState, 2);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        return new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.9, 0.9D);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return NULL_AABB;
    }

    @Nullable
    @Override
    public net.minecraft.pathfinding.PathNodeType getAiPathNodeType(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EntityLiving entity) {
        return PathNodeType.WALKABLE;
    }


    @Override
    public void onEntityCollision(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
        if (worldIn.isRemote) return;
        if (!(entityIn instanceof EntityLivingBase)) return;

        EntityLivingBase entityInLiving = (EntityLivingBase) entityIn;
        SlowdownHandler.applySlowdown(entityInLiving, ForgeConfigHandler.barbedWire.MOVEMENT_SPEED_REDUCTION);

        DamageSource damageSource = new BarbedWireDamageSource();
        entityIn.attackEntityFrom(damageSource, ForgeConfigHandler.barbedWire.DAMAGE);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        Translation.addMultilineTooltip(tooltip, "item." + this.registryName);
    }
}
