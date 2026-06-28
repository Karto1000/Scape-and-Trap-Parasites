package srparasites_traps.features.augments;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.network.SRParasitesTrapsNetwork;
import srparasites_traps.network.SetEntityForTargetingAugment;
import srparasites_traps.registry.ModItems;
import srparasites_traps.util.NBTHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class TargetingAugment extends TurretAugment {
    public static final String REGISTRY_NAME = "targeting_augment";

    public TargetingAugment() {
        super(REGISTRY_NAME);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {

    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        if (!worldIn.isRemote) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));

        Entity pointedEntity = Minecraft.getMinecraft().pointedEntity;
        if (pointedEntity == null) {
            if (!playerIn.isSneaking())
                return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));

            BlockPos pos = playerIn.getPosition();
            playerIn.openGui(SRParasitesTraps.instance, srparasites_traps.util.Constants.TARGETING_AUGMENT_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }

        ResourceLocation entityId = EntityList.getKey(pointedEntity);
        if (entityId == null) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        SRParasitesTrapsNetwork.CHANNEL.sendToServer(new SetEntityForTargetingAugment(entityId.toString()));

        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    public static void addEntityToNBT(ItemStack stack, String entityId) {
        if (stack.getItem() != ModItems.TARGETING_AUGMENT) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) compound = new NBTTagCompound();

        NBTTagList list = compound.getTagList("entities", Constants.NBT.TAG_STRING);
        if (NBTHelper.listHasString(list, entityId)) return;

        NBTTagString entityTag = new NBTTagString(entityId);
        list.appendTag(entityTag);
        compound.setTag("entities", list);

        stack.setTagCompound(compound);
    }

    public static void removeEntityFromNBT(ItemStack stack, String entityId) {
        if (stack.getItem() != ModItems.TARGETING_AUGMENT) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return;

        NBTTagList list = compound.getTagList("entities", Constants.NBT.TAG_STRING);
        NBTHelper.removeStringFromList(list, entityId);
        compound.setTag("entities", list);

        stack.setTagCompound(compound);
    }

    public static String[] getEntityList(ItemStack stack) {
        if (stack.getItem() != ModItems.TARGETING_AUGMENT) return new String[0];
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return new String[0];
        return NBTHelper.tagListToStringArray(compound.getTagList("entities", Constants.NBT.TAG_STRING));
    }

    public static TargetingAugmentGui.TargetingMode getTargetingMode(ItemStack stack) {
        if (stack.getItem() != ModItems.TARGETING_AUGMENT) return TargetingAugmentGui.TargetingMode.WHITELIST;
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return TargetingAugmentGui.TargetingMode.WHITELIST;
        return TargetingAugmentGui.TargetingMode.values()[compound.getInteger("mode")];
    }

    public static void setTargetingMode(ItemStack stack, TargetingAugmentGui.TargetingMode mode) {
        if (stack.getItem() != ModItems.TARGETING_AUGMENT) return;
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) compound = new NBTTagCompound();
        compound.setInteger("mode", mode.ordinal());
        stack.setTagCompound(compound);
    }

    public static boolean isEntityValidForAugment(ItemStack stack, Entity entity) {
        ResourceLocation entityKey = EntityList.getKey(entity);
        if (entityKey == null) return false;

        TargetingAugmentGui.TargetingMode mode = getTargetingMode(stack);
        List<String> entities = Arrays.asList(getEntityList(stack));

        if (mode == TargetingAugmentGui.TargetingMode.WHITELIST) return entities.contains(entityKey.toString());
        else if (mode == TargetingAugmentGui.TargetingMode.BLACKLIST) return !entities.contains(entityKey.toString());
        return false;
    }
}
