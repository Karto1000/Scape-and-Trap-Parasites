package srparasites_traps.proxy;

import com.dhanantry.scapeandrunparasites.client.SRPProjectile;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;
import srparasites_traps.features.sentry_turret.turret.SentryTurretRenderer;
import srparasites_traps.features.sentry_turret.turret.SentryTurretSpineball;
import srparasites_traps.handlers.GuiHandler;
import srparasites_traps.util.Constants;

import java.util.Objects;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        registerEntityRenderers();
        registerGuiHandlers();
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
    }

    public void registerGuiHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(SRParasitesTraps.instance, new GuiHandler());
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