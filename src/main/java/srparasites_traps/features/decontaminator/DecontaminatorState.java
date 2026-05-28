package srparasites_traps.features.decontaminator;

import net.minecraft.util.IStringSerializable;
import srparasites_traps.util.StateTransition;

public enum DecontaminatorState implements IStringSerializable, StateTransition<DecontaminatorState> {
    IDLE,
    DISPENSING,
    OPENING,
    CLOSING;

    @Override
    public String getName() {
        switch (this) {
            case IDLE:
                return "idle";
            case DISPENSING:
                return "dispensing";
            case OPENING:
                return "opening";
            case CLOSING:
                return "closing";
        }
        ;

        return null;
    }

    @Override
    public DecontaminatorState switchState() {
        switch (this) {
            case IDLE:
                return OPENING;
            case OPENING:
                return DISPENSING;
            case DISPENSING:
                return CLOSING;
            case CLOSING:
                return IDLE;
            default:
                return null;
        }
    }
}
