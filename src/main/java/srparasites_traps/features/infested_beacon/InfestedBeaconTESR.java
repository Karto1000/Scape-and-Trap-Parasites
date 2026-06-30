package srparasites_traps.features.infested_beacon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class InfestedBeaconTESR extends TileEntitySpecialRenderer<InfestedBeaconTileEntity> {
    private final static float BEAM_THICKNESS = 0.35F;
    private final static float GLOW_THICKNESS = 0.1F;
    private final static ResourceLocation TEXTURE = new ResourceLocation(
            SRParasitesTraps.MOD_ID,
            "textures/particle/infested_beacon_beam.png"
    );

    @Override
    public boolean isGlobalRenderer(@Nonnull InfestedBeaconTileEntity te) {
        return true;
    }

    @Override
    public void render(
            @Nonnull InfestedBeaconTileEntity te,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha
    ) {
        System.out.println(te.getTotalPower());
        if (te.getTotalPower() == 0) return;

        World world = this.getWorld();
        int heightRemaining = world.getHeight() - te.getPos().getY();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        this.bindTexture(TEXTURE);

        float continuousTime = (float) world.getTotalWorldTime() + partialTicks;
        float rotationAngle = continuousTime * 2.0F;
        double scrollOffset = continuousTime * 0.1;
        double uMin = 0.0D;
        double uMax = 1.0D;
        double vMin = 0.0D - scrollOffset;
        double vMax = heightRemaining - scrollOffset;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Draw the glow
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Vec3d dirToCam = new Vec3d(
                player.posX - (te.getPos().getX() + 0.5D),
                0,
                player.posZ - (te.getPos().getZ() + 0.5D)
        ).normalize();
        Vec3d perpendicularToCam = dirToCam.crossProduct(new Vec3d(0, 1, 0)).normalize();

        buffer.pos(
                        perpendicularToCam.x * (BEAM_THICKNESS + GLOW_THICKNESS),
                        0,
                        perpendicularToCam.z * (BEAM_THICKNESS + GLOW_THICKNESS)
                )
                .color(1.0F, 1.0F, 1.0F, 0.5F)
                .endVertex();
        buffer.pos(
                        perpendicularToCam.x * (BEAM_THICKNESS + GLOW_THICKNESS),
                        heightRemaining,
                        perpendicularToCam.z * (BEAM_THICKNESS + GLOW_THICKNESS)
                )
                .color(1.0F, 1.0F, 1.0F, 0.5F)
                .endVertex();
        buffer.pos(
                        -perpendicularToCam.x * (BEAM_THICKNESS + GLOW_THICKNESS),
                        heightRemaining,
                        -perpendicularToCam.z * (BEAM_THICKNESS + GLOW_THICKNESS)
                )
                .color(1.0F, 1.0F, 1.0F, 0.5F)
                .endVertex();
        buffer.pos(
                        -perpendicularToCam.x * (BEAM_THICKNESS + GLOW_THICKNESS),
                        0,
                        -perpendicularToCam.z * (BEAM_THICKNESS + GLOW_THICKNESS)
                )
                .color(1.0F, 1.0F, 1.0F, 0.5F)
                .endVertex();

        tessellator.draw();

        float radius = BEAM_THICKNESS / 2.0F;

        double minX = -radius;
        double maxX = radius;
        double minY = 0;
        double maxY = heightRemaining;
        double minZ = -radius;
        double maxZ = radius;

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.rotate(rotationAngle, 0.0F, 1.0F, 0.0F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // Face 1: West
        buffer.pos(minX, minY, minZ).tex(uMin, vMin).endVertex();
        buffer.pos(minX, maxY, minZ).tex(uMin, vMax).endVertex();
        buffer.pos(minX, maxY, maxZ).tex(uMax, vMax).endVertex();
        buffer.pos(minX, minY, maxZ).tex(uMax, vMin).endVertex();

        // Face 2: North
        buffer.pos(minX, minY, minZ).tex(uMin, vMin).endVertex();
        buffer.pos(minX, maxY, minZ).tex(uMin, vMax).endVertex();
        buffer.pos(maxX, maxY, minZ).tex(uMax, vMax).endVertex();
        buffer.pos(maxX, minY, minZ).tex(uMax, vMin).endVertex();

        // Face 3: East
        buffer.pos(maxX, minY, minZ).tex(uMin, vMin).endVertex();
        buffer.pos(maxX, maxY, minZ).tex(uMin, vMax).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(uMax, vMax).endVertex();
        buffer.pos(maxX, minY, maxZ).tex(uMax, vMin).endVertex();

        // Face 4: South
        buffer.pos(minX, minY, maxZ).tex(uMin, vMin).endVertex();
        buffer.pos(minX, maxY, maxZ).tex(uMin, vMax).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(uMax, vMax).endVertex();
        buffer.pos(maxX, minY, maxZ).tex(uMax, vMin).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
}
