package srparasites_traps.proxy;

import com.dhanantry.scapeandrunparasites.client.SRPProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import srparasites_traps.features.cleaner.CleanerTESR;
import srparasites_traps.features.cleaner.CleanerTileEntity;
import srparasites_traps.features.relocator.RelocatorEntity;
import srparasites_traps.features.relocator.RelocatorEntityRenderer;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;
import srparasites_traps.features.sentry_turret.turret.SentryTurretRenderer;
import srparasites_traps.features.sentry_turret.turret.SentryTurretSpineball;
import srparasites_traps.features.tesla_coil.LightningParticle;
import srparasites_traps.handlers.RenderHandler;
import srparasites_traps.network.SpawnLightningParticlePacket;
import srparasites_traps.util.Constants;

import java.util.Objects;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        registerEntityRenderers();
        registerTileEntitySpecialRenderers();
        RenderHandler.init();
    }

    public void registerTileEntitySpecialRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(CleanerTileEntity.class, new CleanerTESR());
    }

    public void registerItemRenderers(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
    }

    @Override
    public void handleLightningParticlePacket(SpawnLightningParticlePacket packet) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            World world = Minecraft.getMinecraft().world;
            LightningParticle p = new LightningParticle(
                    world,
                    new Vec3d(packet.fromX, packet.fromY, packet.fromZ),
                    new Vec3d(packet.toX, packet.toY, packet.toZ),
                    20
            );
            Minecraft.getMinecraft().effectRenderer.addEffect(p);
        });
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

        RenderingRegistry.registerEntityRenderingHandler(
                RelocatorEntity.class,
                RelocatorEntityRenderer::new
        );
    }
}