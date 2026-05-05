package srparasites_traps.handlers;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import srparasites_traps.SRParasitesTraps;

@Mod.EventBusSubscriber(modid = SRParasitesTraps.MOD_ID, value = Side.CLIENT)
public class ClientHandler {
    public static TextureAtlasSprite electricityParticleTexture;

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent event) {
        electricityParticleTexture = event.getMap().registerSprite(new ResourceLocation(SRParasitesTraps.MOD_ID, "particle/electricity"));
    }
}
