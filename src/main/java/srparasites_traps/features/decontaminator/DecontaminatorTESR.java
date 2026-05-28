package srparasites_traps.features.decontaminator;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import srparasites_traps.SRParasitesTraps;

public class DecontaminatorTESR extends TileEntitySpecialRenderer<DecontaminatorTileEntity> {
    private static final ResourceLocation ANIMATION_TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/block/decontaminator_opening.png");

    @Override
    public void render(DecontaminatorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int frame = te.getAnimationFrame();
        if (frame < 0) return;

        EnumFacing facing = te.getGrateFacingDirection();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        // Rotate the matrix so we are always drawing on the "UP" face of the model
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(180, 1, 0, 0);
                break;
            case NORTH:
                GlStateManager.rotate(-90, 1, 0, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(90, 1, 0, 0);
                break;
            case WEST:
                GlStateManager.rotate(90, 0, 0, 1);
                GlStateManager.rotate(90, 0, 1, 0);
                break;
            case EAST:
                GlStateManager.rotate(-90, 0, 0, 1);
                GlStateManager.rotate(90, 0, 1, 0);
                break;
            case UP:
            default:
                break;
        }

        // Translate slightly up from the center to sit right on top of the block surface
        // Offset by 0.501 to prevent Z-fighting with the physical block
        GlStateManager.translate(-0.5, 0.501, -0.5);

        this.bindTexture(ANIMATION_TEXTURE);

        int light = te.getWorld().getCombinedLight(te.getPos().offset(facing), 0);
        int lightX = light % 65536;
        int lightY = light / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // UV Math
        double frameHeight = 1.0 / DecontaminatorTileEntity.ANIMATION_FRAMES;
        double minV = frame * frameHeight;
        double maxV = minV + frameHeight;
        double minU = 0.0;
        double maxU = 1.0;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(0, 0, 1).tex(minU, maxV).endVertex();
        buffer.pos(1, 0, 1).tex(maxU, maxV).endVertex();
        buffer.pos(1, 0, 0).tex(maxU, minV).endVertex();
        buffer.pos(0, 0, 0).tex(minU, minV).endVertex();

        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
