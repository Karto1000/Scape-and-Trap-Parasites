package srparasites_traps.registry;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.handlers.RegistryHandler;

@Mod.EventBusSubscriber(modid = SRParasitesTraps.MOD_ID, value = Side.CLIENT)
public class ClientModRegistry {

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        RegistryHandler.registerItemRenderers();
    }
}