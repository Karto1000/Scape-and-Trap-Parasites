package srparasites_traps.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import srparasites_traps.SRParasitesTraps;

public class SpawnElectricityParticlePacket implements IMessage {
    public double x, y, z;

    public SpawnElectricityParticlePacket() {
    }

    public SpawnElectricityParticlePacket(Vec3d pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public static class Handler implements IMessageHandler<SpawnElectricityParticlePacket, IMessage> {
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
                SpawnElectricityParticlePacket message,
                MessageContext ctx
        ) {
            SRParasitesTraps.PROXY.handleElectricityParticlePacket(message);
            return null;
        }
    }
}
