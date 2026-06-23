package srparasites_traps.features.serrated_spikes;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class SerratedSpikesBlock extends Block {
    public float baseDamage = ForgeConfigHandler.serratedSpikes.DEFAULT_DAMAGE;
    public double damageThreshold = ForgeConfigHandler.serratedSpikes.DEFAULT_SERRATED_SPIKES_DAMAGE_MOVE_THRESHOLD;
    public double slowDownAmount = ForgeConfigHandler.serratedSpikes.DEFAULT_SLOW_DOWN_AMOUNT;
    public int minHurtResistanceTime = ForgeConfigHandler.serratedSpikes.DEFAULT_MIN_HURT_RESISTANT_TIME;
    protected static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0D, 1, 0.1D, 1);
    protected static final PropertyDirection direction = PropertyDirection.create("direction", EnumFacing.Plane.HORIZONTAL);
    protected static final DamageSource serratedSpikesDamage = new SerratedSpikesDamageSource();

    public SerratedSpikesBlock(String registryName) {
        super(Material.IRON, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, registryName);
        this.setTranslationKey(getTranslationKeyFor(registryName));
        this.setDefaultState(this.blockState.getBaseState().withProperty(direction, EnumFacing.NORTH));
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setHarvestLevel("pickaxe", 1);
        if (ForgeConfigHandler.serratedSpikes.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Nonnull
    @Override
    public EnumPushReaction getPushReaction(@Nonnull IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public boolean isSideSolid(@Nonnull IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return false;
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
        EnumFacing playerFacing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        return this.getDefaultState()
                .withProperty(direction, EnumFacing.fromAngle(playerFacing.getHorizontalAngle()));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState blockBelow = worldIn.getBlockState(pos.down());
        return blockBelow.isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        if (!fromPos.equals(pos.down(1))) return;
        IBlockState blockBelow = worldIn.getBlockState(fromPos);

        if (worldIn.isAirBlock(fromPos) || !blockBelow.isSideSolid(worldIn, fromPos, EnumFacing.UP)) {
            worldIn.setBlockToAir(pos);
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this)));
        }
    }

    @Nullable
    @Override
    public net.minecraft.pathfinding.PathNodeType getAiPathNodeType(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EntityLiving entity) {
        return PathNodeType.WALKABLE;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(direction);
        return facing.getHorizontalIndex();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing;
        switch (meta) {
            case 1:
                facing = EnumFacing.EAST;
                break;
            case 2:
                facing = EnumFacing.SOUTH;
                break;
            case 3:
                facing = EnumFacing.WEST;
                break;
            default:
                facing = EnumFacing.NORTH;
                break;
        }

        return this.getDefaultState().withProperty(direction, facing);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, direction);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return NULL_AABB;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    protected void damageEntity(Entity entity, float damage) {
        entity.hurtResistantTime = 0;
        entity.attackEntityFrom(serratedSpikesDamage, damage);

        if (ForgeConfigHandler.serratedSpikes.BLEEDING_DAMAGE) {
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).addPotionEffect(new PotionEffect(SRPPotions.BLEED_E, ForgeConfigHandler.serratedSpikes.DEFAULT_BLEEDING_DURATION));
            } else if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).addPotionEffect(new PotionEffect(SRPPotions.BLEED_E, ForgeConfigHandler.serratedSpikes.DEFAULT_BLEEDING_DURATION));
            }
        }
    }

    @Override
    public void onEntityCollision(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
        if (worldIn.isRemote) return;
        if (entityIn instanceof EntityPlayer && entityIn.isSneaking()) return;

        double dX = entityIn.posX - entityIn.prevPosX;
        double dY = entityIn.posY - entityIn.prevPosY;
        double dZ = entityIn.posZ - entityIn.prevPosZ;
        float motionSum = (float) (Math.abs(dX) + Math.abs(dY) + Math.abs(dZ));

        if (motionSum <= 0) return;
        if (entityIn.hurtResistantTime > minHurtResistanceTime + motionSum * ForgeConfigHandler.serratedSpikes.DEFAULT_INVULNERABILITY_REDUCTION_MULTIPLIER) return;
        if (motionSum < damageThreshold) return;

        EnumFacing facing = state.getValue(direction);
        int horizontalIndex = facing.getHorizontalIndex();

        Vec3d facingVec;
        if (horizontalIndex == 0) facingVec = new Vec3d(0, 0, -1);
        else if (horizontalIndex == 1) facingVec = new Vec3d(1, 0, 0);
        else if (horizontalIndex == 2) facingVec = new Vec3d(0, 0, 1);
        else facingVec = new Vec3d(-1, 0, 0);

        double dp = facingVec.dotProduct(new Vec3d(dX, dY, dZ).normalize());
        // We only want the spikes to damage the entity if the entity is moving towards the pointy ends of the spikes
        if (dp < 0.2) return;

        damageEntity(entityIn, baseDamage);
        entityIn.motionX *= slowDownAmount;
        entityIn.motionY *= slowDownAmount;
        entityIn.motionZ *= slowDownAmount;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.serrated_spikes"));
    }
}
