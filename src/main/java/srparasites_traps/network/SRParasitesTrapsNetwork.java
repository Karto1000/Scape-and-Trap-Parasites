package srparasites_traps.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import srparasites_traps.SRParasitesTraps;

public class SRParasitesTrapsNetwork {
    public static SimpleNetworkWrapper CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(SRParasitesTraps.MOD_ID);
        CHANNEL.registerMessage(SpawnLightningParticlePacket.Handler.class, SpawnLightningParticlePacket.class, 0, Side.CLIENT);
        CHANNEL.registerMessage(SpawnElectricityParticlePacket.Handler.class, SpawnElectricityParticlePacket.class, 1, Side.CLIENT);
        CHANNEL.registerMessage(SetEntityForTargetingAugment.Handler.class, SetEntityForTargetingAugment.class, 2, Side.SERVER);
        CHANNEL.registerMessage(RemoveEntityFromTargetingAugment.Handler.class, RemoveEntityFromTargetingAugment.class, 3, Side.SERVER);
        CHANNEL.registerMessage(UpdateTargetingMode.Handler.class, UpdateTargetingMode.class, 4, Side.SERVER);
    }
}
