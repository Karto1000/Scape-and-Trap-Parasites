package srparasites_traps.features.proximity_sensor;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.registry.ModItems;
import srparasites_traps.util.Pair;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static srparasites_traps.util.Translation.getServerStatusFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ProximitySensorBlock extends Block {
    public static final String REGISTRY_NAME = "proximity_sensor";

    public ProximitySensorBlock() {
        super(Material.IRON, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);

        if (ForgeConfigHandler.proximitySensor.ENABLE)
            this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public boolean canProvidePower(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(@Nonnull IBlockState blockState, IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        TileEntity tileEntity = blockAccess.getTileEntity(pos);

        if (tileEntity == null) return 0;
        if (!(tileEntity instanceof ProximitySensorTileEntity)) return 0;

        ProximitySensorState state = ((ProximitySensorTileEntity) tileEntity).getState();
        return state == ProximitySensorState.ACTIVE ? 15 : 0;
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof ProximitySensorTileEntity) {
            ProximitySensorTileEntity pste = (ProximitySensorTileEntity) tileEntity;
            Optional<ItemStack> areaMarker = pste.getAreaMarker();
            areaMarker.ifPresent(itemStack -> worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStack)));
            pste.setAreaMarker(null);
        }

        super.breakBlock(worldIn, pos, state);
    }

    private boolean isDistanceAllowed(ProximitySensorTileEntity pste, ItemStack areaMarker) {
        NBTTagCompound tagCompound = areaMarker.getTagCompound();
        if (tagCompound == null) return false;

        Optional<AxisAlignedBB> aabb = AreaMarkerItem.getBoundAreaAsAABB(tagCompound);
        if (!aabb.isPresent()) return false;

        AxisAlignedBB searchArea = aabb.get();
        Pair<Double, Double> distances = AreaMarkerItem.getDistancesOfAreaTo(searchArea, pste.getPos());
        return distances.first() <= ForgeConfigHandler.proximitySensor.DEFAULT_MAX_AREA_DISTANCE &&
                distances.second() <= ForgeConfigHandler.proximitySensor.DEFAULT_MAX_AREA_DISTANCE;
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (!(tileEntity instanceof ProximitySensorTileEntity)) return false;
        ProximitySensorTileEntity pste = (ProximitySensorTileEntity) tileEntity;

        if (heldItem.isEmpty()) {
            Optional<ItemStack> areaMarker = pste.getAreaMarker();

            if (!areaMarker.isPresent()) return false;
            pste.setAreaMarker(null);
            playerIn.setHeldItem(hand, areaMarker.get());

            return true;
        }

        if (heldItem.getItem() != ModItems.AREA_MARKER_ITEM) return false;
        if (!AreaMarkerItem.hasBoundPosition(heldItem)) {
            playerIn.sendStatusMessage(
                    new TextComponentTranslation(getServerStatusFor("proximity_sensor.area_not_bound")),
                    true
            );
            return true;
        }
        if (pste.getAreaMarker().isPresent()) return false;

        if (!isDistanceAllowed(pste, heldItem)) {
            playerIn.sendStatusMessage(
                    new TextComponentTranslation(
                            getServerStatusFor("proximity_sensor.area_too_far_away"),
                            ForgeConfigHandler.proximitySensor.DEFAULT_MAX_AREA_DISTANCE
                    ),
                    true
            );
            return true;
        }

        pste.setAreaMarker(heldItem);
        playerIn.setHeldItem(hand, ItemStack.EMPTY);

        return true;
    }

    @Override
    public int getStrongPower(@Nonnull IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return getWeakPower(blockState, blockAccess, pos, side);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new ProximitySensorTileEntity();
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + Translation.getTooltipFor("item." + REGISTRY_NAME));
    }
}
