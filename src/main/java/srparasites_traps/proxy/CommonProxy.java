package srparasites_traps.proxy;

import net.minecraft.item.Item;
import srparasites_traps.handlers.EntityHandler;
import srparasites_traps.handlers.GuiHandler;
import srparasites_traps.handlers.LootHandler;
import srparasites_traps.handlers.RegistryHandler;
import srparasites_traps.network.SRParasitesTrapsNetwork;
import srparasites_traps.network.SpawnElectricityParticlePacket;
import srparasites_traps.network.SpawnLightningParticlePacket;

public class CommonProxy {

    public void preInit() {
    }

    public void init() {
        RegistryHandler.init();
        GuiHandler.init();
        LootHandler.init();
        EntityHandler.init();
        SRParasitesTrapsNetwork.init();
    }

    public void postInit() {
    }

    public void handleLightningParticlePacket(SpawnLightningParticlePacket packet) {}

    public void handleElectricityParticlePacket(SpawnElectricityParticlePacket packet) {}

    public void registerItemRenderer(Item item, int meta, String id) {}
}