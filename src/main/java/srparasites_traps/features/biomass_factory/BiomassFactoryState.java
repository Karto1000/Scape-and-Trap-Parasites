package srparasites_traps.features.biomass_factory;

import srparasites_traps.util.StateTransition;

public enum BiomassFactoryState implements StateTransition<BiomassFactoryState> {
    IDLE,
    ACTIVE,
    ;

    @Override
    public BiomassFactoryState switchState() {
        return this == ACTIVE ? IDLE : ACTIVE;
    }
}
