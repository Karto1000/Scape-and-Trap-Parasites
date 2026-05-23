package srparasites_traps.features.area_marker;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.relocator.RelocatorBlock;
import srparasites_traps.features.relocator.RelocatorTileEntity;
import srparasites_traps.util.Pair;
import srparasites_traps.util.VecHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static srparasites_traps.util.Translation.*;

public class AreaMarkerItem extends Item {
    public final static String TAG_AREA_START = "areaPosition1";
    public final static String TAG_AREA_END = "areaPosition2";
    public static final String REGISTRY_NAME = "area_marker";

    public AreaMarkerItem() {
        super();

        setMaxStackSize(1);
        setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    public static Pair<Double, Double> getDistancesOfAreaTo(AxisAlignedBB aabb, BlockPos pos1) {
        return Pair.of(
                Math.sqrt(pos1.distanceSq(aabb.minX, aabb.minY, aabb.minZ)),
                Math.sqrt(pos1.distanceSq(aabb.maxX, aabb.maxY, aabb.maxZ))
        );
    }

    private boolean isBlockAtPositionRelocator(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof RelocatorBlock)) return false;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof RelocatorTileEntity)) return false;

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME + ".base", ForgeConfigHandler.relocator.DEFAULT_AREA_MARKER_MAX_VOLUME));

        if (worldIn == null) return;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) return;

        if (nbt.hasKey(TAG_AREA_START) && nbt.hasKey(TAG_AREA_END)) {
            BlockPos pos1 = BlockPos.fromLong(nbt.getLong(TAG_AREA_START));
            BlockPos pos2 = BlockPos.fromLong(nbt.getLong(TAG_AREA_END));
            tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME + ".area_defined", pos1.getX(), pos2.getX(), pos1.getY(), pos2.getY(), pos1.getZ(), pos2.getZ()));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.isEmpty()) return EnumActionResult.FAIL;
        if (!heldItem.hasTagCompound()) heldItem.setTagCompound(new NBTTagCompound());
        if (isBlockAtPositionRelocator(worldIn, pos)) return EnumActionResult.FAIL;

        NBTTagCompound nbt = heldItem.getTagCompound();
        if (nbt == null) return EnumActionResult.FAIL;

        if (nbt.hasKey(TAG_AREA_START) && nbt.hasKey(TAG_AREA_END)) {
            nbt.removeTag(TAG_AREA_START);
            nbt.removeTag(TAG_AREA_END);
            player.sendStatusMessage(new TextComponentTranslation(getServerStatusFor(REGISTRY_NAME + ".remove_positions")), true);
            return EnumActionResult.SUCCESS;
        }

        if (!nbt.hasKey(TAG_AREA_START)) {
            nbt.setLong(TAG_AREA_START, pos.toLong());
            player.sendStatusMessage(new TextComponentTranslation(getServerStatusFor(REGISTRY_NAME + ".set_position"), pos.getX(), pos.getY(), pos.getZ()), true);
            return EnumActionResult.SUCCESS;
        }

        BlockPos pos1 = BlockPos.fromLong(nbt.getLong(TAG_AREA_START));
        long volume = VecHelper.area(pos1, pos);

        if (volume > ForgeConfigHandler.relocator.DEFAULT_AREA_MARKER_MAX_VOLUME) {
            player.sendStatusMessage(new TextComponentTranslation(getServerStatusFor(REGISTRY_NAME + ".area_too_big"), ForgeConfigHandler.relocator.DEFAULT_AREA_MARKER_MAX_VOLUME), true);
            return EnumActionResult.FAIL;
        }

        player.sendStatusMessage(new TextComponentTranslation(getServerStatusFor(REGISTRY_NAME + ".set_position"), pos.getX(), pos.getY(), pos.getZ()), true);
        nbt.setLong(TAG_AREA_END, pos.toLong());
        return EnumActionResult.SUCCESS;
    }

    public static Optional<AxisAlignedBB> getBoundAreaAsAABB(NBTTagCompound nbt) {
        if (nbt == null) return Optional.empty();

        if (!nbt.hasKey(TAG_AREA_START)) return Optional.empty();
        if (!nbt.hasKey(TAG_AREA_END)) return Optional.empty();

        long pos1 = nbt.getLong(TAG_AREA_START);
        long pos2 = nbt.getLong(TAG_AREA_END);
        BlockPos pos1BlockPos = BlockPos.fromLong(pos1);
        BlockPos pos2BlockPos = BlockPos.fromLong(pos2);

        if (pos1BlockPos.getX() > pos2BlockPos.getX()) pos1BlockPos = pos1BlockPos.add(new Vec3i(1, 0, 0));
        else pos2BlockPos = pos2BlockPos.add(new Vec3i(1, 0, 0));

        if (pos1BlockPos.getY() > pos2BlockPos.getY()) pos1BlockPos = pos1BlockPos.add(new Vec3i(0, 1, 0));
        else pos2BlockPos = pos2BlockPos.add(new Vec3i(0, 1, 0));

        if (pos1BlockPos.getZ() > pos2BlockPos.getZ()) pos1BlockPos = pos1BlockPos.add(new Vec3i(0, 0, 1));
        else pos2BlockPos = pos2BlockPos.add(new Vec3i(0, 0, 1));

        AxisAlignedBB searchArea = new AxisAlignedBB(pos1BlockPos, pos2BlockPos);
        return Optional.of(searchArea);
    }

    public static boolean hasBoundPosition(ItemStack item) {
        if (item.getTagCompound() == null) return false;
        return getBoundAreaAsAABB(item.getTagCompound()).isPresent();
    }
}
