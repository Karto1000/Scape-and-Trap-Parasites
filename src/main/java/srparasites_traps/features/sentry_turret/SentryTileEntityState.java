package srparasites_traps.features.sentry_turret;

import srparasites_traps.util.StateTransition;

public enum SentryTileEntityState implements StateTransition<SentryTileEntityState> {
    INACTIVE,
    ACTIVE,
    DEAD;

    @Override
    public SentryTileEntityState switchState() {
        if (this == DEAD || this == INACTIVE) return ACTIVE;
        return INACTIVE;
    }
}
