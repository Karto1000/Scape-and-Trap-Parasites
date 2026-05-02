package srparasites_traps.util;

public class UpdateLimiter {
    public final int tickDuration;
    public int currentTickDelay;

    public UpdateLimiter(int tickDuration) {
        this.tickDuration = tickDuration;
        // Initialize with a random delay within the specified range so that updates of blocks placed on the same tick don't all happen at once.
        this.currentTickDelay = (int) (Math.random() * tickDuration);
    }

    public boolean tickDown() {
        return --this.currentTickDelay > 0;
    }

    public void allowUpdate() {
        this.currentTickDelay = 0;
    }

    public void reset() {
        this.currentTickDelay = this.tickDuration;
    }
}
