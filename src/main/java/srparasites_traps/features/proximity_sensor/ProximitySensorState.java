package srparasites_traps.features.proximity_sensor;

import srparasites_traps.util.StateTransition;

public enum ProximitySensorState implements StateTransition<ProximitySensorState> {
    INACTIVE,
    ACTIVE;

    @Override
    public ProximitySensorState switchState() {
        return this == ACTIVE ? INACTIVE : ACTIVE;
    }
}
