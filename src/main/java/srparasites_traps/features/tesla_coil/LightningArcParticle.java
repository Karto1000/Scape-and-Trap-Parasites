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

import java.util.ArrayList;

public class LightningArcParticle extends Particle {
    private final int maxStageVectors;
    private final ArrayList<Vec3d> stageVectors = new ArrayList<>();

    private final int maxParticleAge;
    private final Vec3d from;
    private final Vec3d to;
    private final int intensity;
    private final static int DEFAULT_INNER_THICKNESS = 2;
    private final static int DEFAULT_OUTER_THICKNESS = DEFAULT_INNER_THICKNESS * 2;

    public LightningArcParticle(World worldIn, Vec3d from, Vec3d to, int maxParticleAge, int intensity) {
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

        for (int i = 0; i < maxStageVectors; i += 2){
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
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        GlStateManager.glLineWidth(thickness);

        Vec3d vecSum = actualFrom;
        for (Vec3d v : this.stageVectors) {
            Vec3d toPos = vecSum.add(v);

            bufferBuilder.pos(vecSum.x, vecSum.y, vecSum.z).color(r, g, b, a).endVertex();
            bufferBuilder.pos(toPos.x, toPos.y, toPos.z).color(r, g, b, a).endVertex();

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
            BufferBuilder buffer,
            Entity entityIn,
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

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        Vec3d actualFrom = this.from.subtract(interpPosX, interpPosY, interpPosZ);

        buildLineAlongStages(
                bufferbuilder,
                actualFrom,
                 0.F,
                0.F,
                1.F,
                 1.F / this.particleAge,
               DEFAULT_OUTER_THICKNESS + (float) this.intensity / 2
        );

        tessellator.draw();

        buildLineAlongStages(
                bufferbuilder,
                actualFrom,
                1.F,
                1.F,
                1.F,
                1.F / this.particleAge,
                DEFAULT_INNER_THICKNESS + (float) this.intensity / 2
        );

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.popMatrix();
    }
}
