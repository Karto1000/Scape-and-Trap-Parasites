package srparasites_traps.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import srparasites_traps.SRParasitesTraps;

public class SRParasitesTrapsNetwork {
    public static SimpleNetworkWrapper CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(SRParasitesTraps.MOD_ID);
        CHANNEL.registerMessage(ToggleSentryPacket.Handler.class, ToggleSentryPacket.class, 0, Side.SERVER);
        CHANNEL.registerMessage(SpawnLightningParticlePacket.Handler.class, SpawnLightningParticlePacket.class, 1, Side.CLIENT);
    }
}
