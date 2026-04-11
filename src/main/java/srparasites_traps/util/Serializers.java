package srparasites_traps.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import java.io.IOException;

public class Serializers {
    public static final DataSerializer<Long> LONG = new DataSerializer<Long>() {
        public void write(PacketBuffer buf, Long value) {
            buf.writeLong(value);
        }

        public Long read(PacketBuffer buf) throws IOException {
            return buf.readLong();
        }

        public DataParameter<Long> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        public Long copyValue(Long value) {
            return value;
        }
    };
}
