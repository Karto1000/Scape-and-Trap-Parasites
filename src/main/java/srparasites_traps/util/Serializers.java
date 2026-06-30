package srparasites_traps.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Serializers {
    public static final DataSerializer<Long> LONG = new DataSerializer<Long>() {
        public void write(
                PacketBuffer buf,
                @Nonnull Long value
        ) {
            buf.writeLong(value);
        }

        @Nonnull
        public Long read(PacketBuffer buf) throws IOException {
            return buf.readLong();
        }

        @Nonnull
        public DataParameter<Long> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Nonnull
        public Long copyValue(@Nonnull Long value) {
            return value;
        }
    };
}
