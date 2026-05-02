package srparasites_traps.proxy;

import net.minecraft.item.Item;
import srparasites_traps.network.SpawnLightningParticlePacket;

public abstract class CommonProxy {

    public abstract void init();

    public abstract void registerItemRenderers(Item item, int meta, String id);

    public abstract void handleLightningParticlePacket(SpawnLightningParticlePacket packet);
}