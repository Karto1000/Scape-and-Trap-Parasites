package srparasites_traps.features.sentry_turret;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SentryTurretRenderer extends RenderLiving<SentryTurretEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.SRPARASITES_MOD_ID, "textures/entity/monster/unvo.png");

    public SentryTurretRenderer(RenderManager manager) {
        super(manager, new SentryTurretModel(), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull SentryTurretEntity entity) {
        return TEXTURE;
    }
}