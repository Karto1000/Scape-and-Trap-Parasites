package srparasites_traps.proxy;

import com.dhanantry.scapeandrunparasites.client.SRPProjectile;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import srparasites_traps.features.sentry_turret.SentryTurretEntity;
import srparasites_traps.features.sentry_turret.SentryTurretRenderer;
import srparasites_traps.features.sentry_turret.SentryTurretSpineball;
import srparasites_traps.util.Constants;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        registerEntityRenderers();
    }

    public void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(
                SentryTurretEntity.class,
                SentryTurretRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                SentryTurretSpineball.class,
                manager -> new SRPProjectile(manager, 0.5F, new ResourceLocation(Constants.SRPARASITES_MOD_ID, "textures/entity/projectile/spineball.png"))
        );
    }
}