package srparasites_traps.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.features.hardness_analyser.HardnessAnalyzerItem;
import srparasites_traps.registry.ModItems;
import srparasites_traps.util.Pair;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RenderHandler {
    private String cachedStatus = null;
    private Float cachedHardness = null;
    private String cachedName = null;

    private void renderAreaMarker() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack stack = player.getHeldItemMainhand();
        Item item = stack.getItem();

        if (stack.isEmpty()) return;
        if (item != ModItems.AREA_MARKER_ITEM) return;

        RenderManager renderManager = mc.getRenderManager();
        double renderX = renderManager.viewerPosX;
        double renderY = renderManager.viewerPosY;
        double renderZ = renderManager.viewerPosZ;

        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) return;

        Optional<AxisAlignedBB> boundArea = AreaMarkerItem.getBoundAreaAsAABB(tagCompound);
        boundArea.ifPresent(box -> drawSelectionBox(box.minX - renderX, box.minY - renderY, box.minZ - renderZ, box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ, 0.5F, 0.0F, 0.0F));
    }

    private void drawOverlayTextBox(
            String text,
            int x,
            int y,
            int maxWidth,
            int textColor
    ) {
        Minecraft mc = Minecraft.getMinecraft();

        int padding = 4;
        int lineHeight = mc.fontRenderer.FONT_HEIGHT;
        int textHeight = 0;
        int textWidth = 0;

        String[] lines = text.split("\n");
        for (String line : lines) {
            List<String> wrappedLines = mc.fontRenderer.listFormattedStringToWidth(line, maxWidth);

            if (wrappedLines.isEmpty()) {
                textHeight += lineHeight;
                continue;
            }

            textHeight += wrappedLines.size() * lineHeight;

            for (String wrappedLine : wrappedLines) {
                textWidth = Math.max(textWidth, mc.fontRenderer.getStringWidth(wrappedLine));
            }
        }

        int left = x - padding;
        int top = y - padding;
        int right = x + textWidth + padding;
        int bottom = y + textHeight + padding;

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        Gui.drawRect(left, top, right, bottom, 0xAA000000);

        Gui.drawRect(left + 1, top + 1, right - 1, top + 2, 0xFF36049a);
        Gui.drawRect(left + 1, bottom - 2, right - 1, bottom - 1, 0xFF36049a);
        Gui.drawRect(left + 1, top + 1, left + 2, bottom - 1, 0xFF36049a);
        Gui.drawRect(right - 2, top + 1, right - 1, bottom - 1, 0xFF36049a);

        mc.fontRenderer.drawSplitString(text, x, y, maxWidth, textColor);

        GlStateManager.resetColor();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderHardnessAnalyserOverlay() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack stack = player.getHeldItemMainhand();
        Item item = stack.getItem();

        if (stack.isEmpty()) return;
        if (item != ModItems.HARDNESS_ANALYZER_ITEM) return;

        Optional<Pair<Float, String>> hardnessAndName = HardnessAnalyzerItem.getHardnessAndName(stack);
        if (!hardnessAndName.isPresent()) return;

        float blockHardness = hardnessAndName.get().first();
        String blockName = hardnessAndName.get().second();

        if ((cachedName == null && cachedHardness == null) || !Objects.equals(cachedHardness, blockHardness) || !Objects.equals(cachedName, blockName)) {
            cachedStatus = HardnessAnalyzerItem.getVulnerableCount(stack)
                    .entrySet()
                    .stream()
                    .sorted((o1, o2) -> Float.compare(o1.getValue().averageHardness(), o2.getValue().averageHardness()))
                    .map((entry) -> {
                        HardnessAnalyzerItem.GroupGriefSummary summary = entry.getValue();
                        if (summary.parasiteCount == 0) return "";

                        if (summary.vulnerableCount == 0)
                            return String.format("§a| %s: No vulnerabilities§f", entry.getKey());

                        return String.format(
                                "%s| %s: breakable by %d of %d parasites§f",
                                ((double) summary.vulnerableCount / summary.parasiteCount) <= 0.5 ? "§e" : "§c",
                                entry.getKey(),
                                summary.vulnerableCount,
                                summary.parasiteCount
                        );
                    })
                    .collect(Collectors.joining("\n"));

            cachedName = blockName;
            cachedHardness = blockHardness;
        }


        String text = blockName + "\n" +
                blockHardness + " Block Hardness \n"
                + cachedStatus;

        drawOverlayTextBox(
                text,
                ForgeConfigHandler.hardnessAnalyzer.GUI_TEXT_OFFSET_X,
                ForgeConfigHandler.hardnessAnalyzer.GUI_TEXT_OFFSET_Y,
                500,
                0xFFFFFF
        );
    }

    private void drawSelectionBox(
            double x,
            double y,
            double z,
            double w,
            double h,
            double d,
            float r,
            float g,
            float b
    ) {
        AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + w, y + h, z + d).grow(0.002);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        RenderGlobal.renderFilledBox(box, r, g, b, 0.4F);
        RenderGlobal.drawSelectionBoundingBox(box, r, g, b, 0.8F);

        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        renderHardnessAnalyserOverlay();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        renderAreaMarker();
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
    }
}
