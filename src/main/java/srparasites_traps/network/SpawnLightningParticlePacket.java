package srparasites_traps.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import srparasites_traps.SRParasitesTraps;

public class SpawnLightningParticlePacket implements IMessage {
    public double fromX, fromY, fromZ;
    public double toX, toY, toZ;
    public int intensity;

    public SpawnLightningParticlePacket() {
    }

    public SpawnLightningParticlePacket(Vec3d from, Vec3d to, int intensity) {
        this.fromX = from.x;
        this.fromY = from.y;
        this.fromZ = from.z;
        this.toX = to.x;
        this.toY = to.y;
        this.toZ = to.z;
        this.intensity = intensity;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        fromX = buf.readDouble();
        fromY = buf.readDouble();
        fromZ = buf.readDouble();
        toX = buf.readDouble();
        toY = buf.readDouble();
        toZ = buf.readDouble();
        intensity = buf.readInt();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(fromX);
        buf.writeDouble(fromY);
        buf.writeDouble(fromZ);
        buf.writeDouble(toX);
        buf.writeDouble(toY);
        buf.writeDouble(toZ);
        buf.writeInt(intensity);
    }

    public static class Handler implements IMessageHandler<SpawnLightningParticlePacket, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(SpawnLightningParticlePacket message, MessageContext ctx) {
            SRParasitesTraps.PROXY.handleLightningParticlePacket(message);
            return null;
        }
    }
}
