package srparasites_traps.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import srparasites_traps.SRParasitesTraps;

import java.nio.charset.StandardCharsets;

public class SetEntityForTargetingAugment implements IMessage {
    public String entityId;

    public SetEntityForTargetingAugment() {
    }

    public SetEntityForTargetingAugment(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        this.entityId = buf.readCharSequence(length, StandardCharsets.UTF_8).toString();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId.length());
        buf.writeCharSequence(entityId, StandardCharsets.UTF_8);
    }

    public static class Handler implements IMessageHandler<SetEntityForTargetingAugment, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(SetEntityForTargetingAugment message, MessageContext ctx) {
            SRParasitesTraps.PROXY.handleSetEntityForTargetingAugmentPacket(message, ctx);
            return null;
        }
    }
}
