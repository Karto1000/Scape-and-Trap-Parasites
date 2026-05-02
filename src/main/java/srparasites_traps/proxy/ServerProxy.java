package srparasites_traps.proxy;

import net.minecraft.item.Item;
import srparasites_traps.network.SpawnLightningParticlePacket;

public class ServerProxy extends CommonProxy {
    @Override
    public void init() {

    }

    @Override
    public void registerItemRenderers(Item item, int meta, String id) {

    }

    @Override
    public void handleLightningParticlePacket(SpawnLightningParticlePacket packet) {

    }
}
