package srparasites_traps.proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import srparasites_traps.features.augments.TargetingAugment;
import srparasites_traps.handlers.EntityHandler;
import srparasites_traps.handlers.GuiHandler;
import srparasites_traps.handlers.LootHandler;
import srparasites_traps.handlers.RegistryHandler;
import srparasites_traps.network.*;


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

    public void handleLightningParticlePacket(SpawnLightningParticlePacket packet) {
    }

    public void handleElectricityParticlePacket(SpawnElectricityParticlePacket packet) {
    }

    public void handleRemoveEntityFromTargetingAugmentPacket(RemoveEntityFromTargetingAugment packet, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        TargetingAugment.removeEntityFromNBT(player.getHeldItemMainhand(), packet.entityId);
    }

    public void handleUpdateTargetingModePacket(UpdateTargetingMode packet, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        TargetingAugment.setTargetingMode(player.getHeldItemMainhand(), packet.targetingMode);
    }

    public void registerItemRenderer(Item item, int meta, String id) {
    }
}