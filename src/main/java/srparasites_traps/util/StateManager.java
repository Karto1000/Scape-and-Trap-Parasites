package srparasites_traps.util;

public class StateManager<T extends Enum<T>> {
    private T state;
    private long ticksWhenChange;

    public StateManager(T state) {
        this.state = state;
    }

    public T getState() {
        return this.state;
    }

    public void setState(T state, long ticks) {
        this.state = state;
        this.ticksWhenChange = ticks;
    }

    public void setState(T state) {
        this.state = state;
    }

    public long getTicksSinceChange(long currentTicks) {
        return currentTicks - this.ticksWhenChange;
    }

    public boolean isState(T state) {
        return this.state == state;
    }
}
