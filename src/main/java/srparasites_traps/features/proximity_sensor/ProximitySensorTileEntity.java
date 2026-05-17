package srparasites_traps.features.proximity_sensor;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import srparasites_traps.util.StateManager;
import srparasites_traps.util.UpdateLimiter;

import java.util.List;

public class ProximitySensorTileEntity extends TileCore implements ITickable {
    public static final int DEFAULT_RANGE = 10;

    private final UpdateLimiter updateLimiter = new UpdateLimiter(10);
    private final StateManager<ProximitySensorState> stateManager = new StateManager<>(
            ProximitySensorState.INACTIVE,
            (oldState, newState) -> {
                IBlockState state = this.world.getBlockState(this.pos);
                this.world.notifyBlockUpdate(this.pos, state, state, 3);
                this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            }
    );

    public ProximitySensorState getState() {
        return stateManager.getState();
    }

    @Override
    public void update() {
        if (updateLimiter.tickDown()) return;
        updateLimiter.reset();

        AxisAlignedBB aabb = new AxisAlignedBB(this.pos).grow(DEFAULT_RANGE);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityParasiteBase.class, aabb);

        if (entities.isEmpty()) {
            if (stateManager.isState(ProximitySensorState.ACTIVE))
                stateManager.switchState(this.world.getTotalWorldTime());
            return;
        }

        if (stateManager.isState(ProximitySensorState.ACTIVE)) return;
        stateManager.switchState(this.world.getTotalWorldTime());
    }
}
