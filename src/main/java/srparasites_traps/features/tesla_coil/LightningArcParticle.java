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

    public LightningArcParticle(
            World worldIn,
            Vec3d from,
            Vec3d to,
            int maxParticleAge,
            int intensity
    ) {
        super(worldIn, from.x, from.y, from.z);

        this.from = from;
        this.to = to;
        this.maxParticleAge = maxParticleAge;
        this.maxStageVectors = (int) (to.subtract(from).length() * 2);
        this.intensity = intensity;

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
            float rotatePitch = (float) (this.rand.nextFloat() * Math.PI * 0.5);
            float rotateYaw = (float) (this.rand.nextFloat() * Math.PI * 0.5);

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
            float thickness
    ) {
        Vec3d vecSum = actualFrom;

        for (Vec3d v : this.stageVectors) {
            Vec3d toPos = vecSum.add(v);

            // The camera is implicitly at (0,0,0) in these particle coordinates
            Vec3d toCamera = vecSum.scale(-1).normalize();
            Vec3d dir = v.normalize();

            // Cross product gives us a perpendicular vector facing the camera
            Vec3d right = dir.crossProduct(toCamera)
                    .normalize()
                    .scale(thickness);

            // Fallback if segment points exactly at the camera
            if (right.length() < 1e-4) right = new Vec3d(thickness, 0, 0);

            // Quad vertices
            Vec3d v1 = vecSum.subtract(right);
            Vec3d v2 = toPos.subtract(right);
            Vec3d v3 = toPos.add(right);
            Vec3d v4 = vecSum.add(right);

            // Full brightness lightmap for glowing effect (240, 240)
            int skyLight = 240;
            int blockLight = 240;

            // Provide valid UVs (even generic ones like 0 to 1) so shaders don't crash
            bufferBuilder.pos(v1.x, v1.y, v1.z).tex(0, 0).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
            bufferBuilder.pos(v2.x, v2.y, v2.z).tex(0, 0).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
            bufferBuilder.pos(v3.x, v3.y, v3.z).tex(0, 0).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
            bufferBuilder.pos(v4.x, v4.y, v4.z).tex(0, 0).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();

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
        GlStateManager.depthMask(false);

        Vec3d actualFrom = this.from.subtract(interpPosX, interpPosY, interpPosZ);
        float outerThickness = (DEFAULT_OUTER_THICKNESS + this.intensity) * 0.005f;
        float innerThickness = (DEFAULT_INNER_THICKNESS + this.intensity) * 0.005f;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

        buildLineAlongStages(
                buffer,
                actualFrom,
                0.F,
                0.F,
                1.F,
                1.F / (this.particleAge == 0 ? 1.F : this.particleAge),
                outerThickness
        );

        buildLineAlongStages(
                buffer,
                actualFrom,
                1.F,
                1.F,
                1.F,
                1.F / (this.particleAge == 0 ? 1.F : this.particleAge),
                innerThickness
        );

        Tessellator.getInstance().draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
