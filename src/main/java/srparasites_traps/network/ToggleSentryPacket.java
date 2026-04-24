package srparasites_traps.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import srparasites_traps.features.sentry_turret.base.SentryTurretTileEntity;

public class ToggleSentryPacket implements IMessage {
    public BlockPos sentryBlockPosition;

    public ToggleSentryPacket() {
    }

    public ToggleSentryPacket(BlockPos sentryBlockPosition) {
        this.sentryBlockPosition = sentryBlockPosition;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.sentryBlockPosition = BlockPos.fromLong(buf.readLong());
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.sentryBlockPosition.toLong());
    }

    public static class Handler implements IMessageHandler<ToggleSentryPacket, IMessage> {
        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(ToggleSentryPacket message, MessageContext ctx) {
            FMLCommonHandler
                    .instance()
                    .getWorldThread(ctx.netHandler)
                    .addScheduledTask(() -> {
                        EntityPlayer player = ctx.getServerHandler().player;
                        World world = player.world;

                        TileEntity tileEntity = world.getTileEntity(message.sentryBlockPosition);
                        if (tileEntity == null) return;

                        if (tileEntity instanceof SentryTurretTileEntity) {
                            ((SentryTurretTileEntity) tileEntity).toggleEntity();
                        }
                    });
            return null;
        }
    }

}
