package srparasites_traps.features.relocator;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.util.Constants;

import javax.annotation.Nullable;

public class RelocatorEntityRenderer extends RenderLiving<RelocatorEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.SRPARASITES_MOD_ID, "textures/entity/monster/nak.png");

    public RelocatorEntityRenderer(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new RelocatorEntityModel(), 0.4F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(RelocatorEntity entity) {
        return TEXTURE;
    }
}
