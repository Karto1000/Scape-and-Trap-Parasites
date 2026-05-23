package srparasites_traps.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.registry.ModItems;

import java.util.Optional;

public class RenderHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        ItemStack stack = player.getHeldItemMainhand();
        Item item = stack.getItem();

        if (stack.isEmpty()) return;
        if (item != ModItems.AREA_MARKER_ITEM) return;

        double renderX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) return;

        Optional<AxisAlignedBB> boundArea = AreaMarkerItem.getBoundAreaAsAABB(tagCompound);
        boundArea.ifPresent(box -> drawSelectionBox(box.minX - renderX, box.minY - renderY, box.minZ - renderZ, box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ, 0.5F, 0.0F, 0.0F));
    }

    private void drawSelectionBox(double x, double y, double z, double w, double h, double d, float r, float g, float b) {
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

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
    }
}
