package srparasites_traps.features.tesla_coil;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class LightningArcParticle extends Particle {
    private final int maxStageVectors;
    private final ArrayList<Vec3d> stageVectors = new ArrayList<>();

    private final int maxParticleAge;
    private final Vec3d from;
    private final Vec3d to;
    private final int intensity;
    private final static int DEFAULT_INNER_THICKNESS = 1;
    private final static int DEFAULT_OUTER_THICKNESS = DEFAULT_INNER_THICKNESS * 2;
    private final int primaryColor;
    private final int secondaryColor;

    public LightningArcParticle(
            World worldIn,
            Vec3d from,
            Vec3d to,
            int maxParticleAge,
            int intensity,
            int primaryColor,
            int secondaryColor
    ) {
        super(worldIn, from.x, from.y, from.z);

        this.from = from;
        this.to = to;
        this.maxParticleAge = maxParticleAge;
        this.maxStageVectors = (int) (to.subtract(from).length() * 2);
        this.intensity = intensity;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;

        makeStageVectors();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    private void makeStageVectors() {
        Vec3d delta = this.to.subtract(this.from);
        Vec3d unitStageVector = delta.scale((double) 1 / this.maxStageVectors);

        for (int i = 0; i < maxStageVectors; i += 2) {
            float rotatePitch = (float) (((this.rand.nextFloat() * 2) - 1) * Math.PI * 0.5);
            float rotateYaw = (float) (((this.rand.nextFloat() * 2) - 1) * Math.PI * 0.5);

            double lengthMultiplier = 0.4 + this.rand.nextDouble() * 1.2;

            Vec3d rotated = unitStageVector
                    .rotatePitch(rotatePitch)
                    .rotateYaw(rotateYaw)
                    .scale(lengthMultiplier);

            this.stageVectors.add(rotated);

            Vec3d backOnLine = unitStageVector.scale(2).subtract(rotated);
            this.stageVectors.add(backOnLine);
        }
    }

    private void buildLineAlongStages(
            BufferBuilder bufferBuilder,
            Vec3d actualFrom,
            float r,
            float g,
            float b,
            float a,
            float thickness,
            boolean isInner
    ) {
        Vec3d vecSum = actualFrom;

        for (Vec3d v : this.stageVectors) {
            Vec3d toPos = vecSum.add(v);

            Vec3d toCamera = vecSum.scale(-1).normalize();
            Vec3d dir = v.normalize();

            Vec3d right = dir.crossProduct(toCamera)
                    .normalize()
                    .scale(thickness);

            if (right.length() < 1e-4) right = new Vec3d(thickness, 0, 0);

            Vec3d stopZLayerFight = isInner ? toCamera.scale(0.001) : Vec3d.ZERO;

            // Quad vertices
            Vec3d v1 = vecSum.subtract(right).add(stopZLayerFight);
            Vec3d v2 = toPos.subtract(right).add(stopZLayerFight);
            Vec3d v3 = toPos.add(right).add(stopZLayerFight);
            Vec3d v4 = vecSum.add(right).add(stopZLayerFight);

            // Provide valid UVs (even generic ones like 0 to 1) so shaders don't crash
            bufferBuilder.pos(v1.x, v1.y, v1.z).tex(0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.pos(v2.x, v2.y, v2.z).tex(0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.pos(v3.x, v3.y, v3.z).tex(0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.pos(v4.x, v4.y, v4.z).tex(0, 0).color(r, g, b, a).endVertex();

            vecSum = toPos;
        }
    }

    @Override
    public boolean isAlive() {
        return this.particleAge < this.maxParticleAge;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticle(
            @Nonnull BufferBuilder buffer,
            @Nonnull Entity entityIn,
            float partialTicks,
            float rotationX,
            float rotationZ,
            float rotationYZ,
            float rotationXY,
            float rotationXZ
    ) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        Vec3d actualFrom = this.from.subtract(interpPosX, interpPosY, interpPosZ);
        float outerThickness = (DEFAULT_OUTER_THICKNESS + this.intensity) * 0.005f;
        float innerThickness = (DEFAULT_INNER_THICKNESS + this.intensity) * 0.005f;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        float sa = (float) ((this.secondaryColor >> 24) & 0xFF) / 255;
        float sr = (float) ((this.secondaryColor >> 16) & 0xFF) / 255;
        float sg = (float) ((this.secondaryColor >> 8) & 0xFF) / 255;
        float sb = (float) (this.secondaryColor & 0xFF) / 255;

        buildLineAlongStages(
                buffer,
                actualFrom,
                sr,
                sg,
                sb,
                sa - sa / maxParticleAge * this.particleAge,
                outerThickness,
                false
        );

        float pa = (float) ((this.primaryColor >> 24) & 0xFF) / 255;
        float pr = (float) ((this.primaryColor >> 16) & 0xFF) / 255;
        float pg = (float) ((this.primaryColor >> 8) & 0xFF) / 255;
        float pb = (float) (this.primaryColor & 0xFF) / 255;

        buildLineAlongStages(
                buffer,
                actualFrom,
                pr,
                pg,
                pb,
                pa - pa / maxParticleAge * this.particleAge,
                innerThickness,
                true
        );

        Tessellator.getInstance().draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
