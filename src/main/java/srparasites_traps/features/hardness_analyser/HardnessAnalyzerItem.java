package srparasites_traps.features.hardness_analyser;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.EntityHelper;
import srparasites_traps.util.Pair;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class HardnessAnalyzerItem extends Item {
    public static final String REGISTRY_NAME = "hardness_analyzer";
    private static final SortedMap<String, List<GriefingParasite>> griefingParasites = new TreeMap<>();

    public static class GriefingParasite {
        ResourceLocation id;
        float hardness;

        public GriefingParasite(
                ResourceLocation id,
                float blockH
        ) {
            this.id = id;
            this.hardness = blockH;
        }

        @Override
        public String toString() {
            return String.format("%s: %f", id, hardness);
        }
    }

    public static SortedMap<String, List<GriefingParasite>> getGriefingParasites() {
        if (!griefingParasites.isEmpty()) return griefingParasites;

        for (String grief : SRPConfig.parasiteGriefing) {
            String[] split = grief.split(";");
            if (split.length < 2) continue;

            String entityRegistryName = split[0];
            if (entityRegistryName.isEmpty()) continue;

            String entityHardness = split[1];
            if (entityHardness.isEmpty()) continue;

            Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(entityRegistryName));
            if (entityClass == null) continue;

            Optional<String> parasiteType = EntityHelper.getEntityParasiteType(entityClass);
            if (!parasiteType.isPresent()) continue;

            if (!griefingParasites.containsKey(parasiteType.get()))
                griefingParasites.put(parasiteType.get(), new ArrayList<>());
            List<GriefingParasite> entityList = griefingParasites.get(parasiteType.get());

            entityList.add(new GriefingParasite(new ResourceLocation(entityRegistryName), (float) Double.parseDouble(entityHardness)));
        }

        return griefingParasites;
    }

    public HardnessAnalyzerItem() {
        super();

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setMaxStackSize(1);

        if (ForgeConfigHandler.hardnessAnalyzer.ENABLE)
            this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    public static Optional<Pair<Float, String>> getHardnessAndName(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return Optional.empty();

        float hardness = tag.getFloat("hardness");
        String name = tag.getString("name");
        if (name.isEmpty()) return Optional.empty();

        return Optional.of(Pair.of(hardness, name));
    }

    public static class GroupGriefSummary {
        public final int vulnerableCount;
        public final int parasiteCount;
        public final float hardnessSum;

        public GroupGriefSummary(
                int vulnerableCount,
                int parasiteCount,
                float hardnessSum
        ) {
            this.vulnerableCount = vulnerableCount;
            this.parasiteCount = parasiteCount;
            this.hardnessSum = hardnessSum;
        }

        public float averageHardness() {
            return hardnessSum / parasiteCount;
        }
    }

    @SideOnly(Side.CLIENT)
    public static Map<String, GroupGriefSummary> getVulnerableCount(ItemStack stack) {
        Optional<Pair<Float, String>> hardnessAndName = getHardnessAndName(stack);
        if (!hardnessAndName.isPresent()) return Collections.emptyMap();
        float blockHardness = hardnessAndName.get().first();

        SortedMap<String, GroupGriefSummary> vulnerableCount = new TreeMap<>();
        getGriefingParasites().forEach((key, value) -> {
            vulnerableCount.put(key, new GroupGriefSummary(0, value.size(), 0));

            for (GriefingParasite parasite : value) {
                GroupGriefSummary summary = vulnerableCount.get(key);
                int newVulnerableCount = summary.vulnerableCount;
                if (blockHardness <= parasite.hardness && blockHardness != -1.) newVulnerableCount++;
                vulnerableCount.put(key, new GroupGriefSummary(newVulnerableCount, value.size(), summary.hardnessSum + parasite.hardness));
            }
        });

        return vulnerableCount;
    }

    private void resetHardnessAndName(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;
        tag.removeTag("hardness");
        tag.removeTag("name");
    }

    @Override
    public void onUpdate(
            @Nonnull ItemStack stack,
            @Nonnull World worldIn,
            @Nonnull Entity entityIn,
            int itemSlot,
            boolean isSelected
    ) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        if (!isSelected) return;
        if (!worldIn.isRemote) return;
        if (!(entityIn instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entityIn;

        RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
        if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
            resetHardnessAndName(stack);
            return;
        }

        BlockPos pos = result.getBlockPos();
        IBlockState state = worldIn.getBlockState(pos);

        float hardness = state.getBlockHardness(worldIn, pos);

        Block block = state.getBlock();
        ItemStack pickedState = block.getPickBlock(state, result, worldIn, pos, player);
        String name = pickedState.isEmpty() ? block.getLocalizedName() : pickedState.getDisplayName();

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setFloat("hardness", hardness);
        tag.setString("name", name);
        stack.setTagCompound(tag);
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
}
