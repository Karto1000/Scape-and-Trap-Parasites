package srparasites_traps.util;

import java.util.function.BiConsumer;

public class StateManager<T extends Enum<T> & StateTransition<T>> {
    private T state;
    private long ticksWhenChange;
    private BiConsumer<T, T> onStateChange = (lastState, newState) -> {};

    public StateManager(T state) {
        this.state = state;
    }

    public StateManager(
            T state,
            BiConsumer<T, T> onStateChange
    ) {
        this.state = state;
        this.onStateChange = onStateChange;
    }

    public T getState() {
        return this.state;
    }

    public void setState(
            T state,
            long ticks
    ) {
        if (this.state == state) return;

        T oldState = this.state;
        this.ticksWhenChange = ticks;
        this.state = state;
        this.onStateChange.accept(oldState, state);
    }

    public void setState(T state) {
        if (this.state == state) return;

        T oldState = this.state;
        this.state = state;
        this.onStateChange.accept(oldState, state);
    }

    public void switchState(long ticks) {
        T newState = this.state.switchState();
        this.setState(newState, ticks);
    }

    public long getTicksSinceChange(long currentTicks) {
        return currentTicks - this.ticksWhenChange;
    }

    public boolean isState(T state) {
        return this.state == state;
    }
}
