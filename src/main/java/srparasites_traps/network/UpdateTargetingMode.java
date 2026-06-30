package srparasites_traps.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.augments.TargetingAugmentGui;

public class UpdateTargetingMode implements IMessage {
    public TargetingAugmentGui.TargetingMode targetingMode;

    public UpdateTargetingMode() {
    }

    public UpdateTargetingMode(TargetingAugmentGui.TargetingMode targetingMode) {
        this.targetingMode = targetingMode;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.targetingMode = TargetingAugmentGui.TargetingMode.values()[buf.readInt()];
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(targetingMode.ordinal());
    }

    public static class Handler implements IMessageHandler<UpdateTargetingMode, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(
                UpdateTargetingMode message,
                MessageContext ctx
        ) {
            SRParasitesTraps.PROXY.handleUpdateTargetingModePacket(message, ctx);
            return null;
        }
    }
}
