package srparasites_traps.features.tesla_coil;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import srparasites_traps.handlers.ClientHandler;

public class ElectricityParticle extends Particle {
    public ElectricityParticle(World worldIn, Vec3d pos) {
        super(worldIn, pos.x, pos.y, pos.z);

        this.setParticleTexture(ClientHandler.electricityParticleTexture);
        this.particleMaxAge = 6;
        this.setSize(32, 32);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
