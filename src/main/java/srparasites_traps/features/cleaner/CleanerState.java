package srparasites_traps.features.cleaner;

import net.minecraft.util.IStringSerializable;

public enum CleanerState implements IStringSerializable {
    IDLE,
    DISPENSING,
    OPENING,
    CLOSING;

    @Override
    public String getName() {
        switch (this) {
            case IDLE: return "idle";
            case DISPENSING: return "dispensing";
            case OPENING: return "opening";
            case CLOSING: return "closing";
        };

        return null;
    }
}
