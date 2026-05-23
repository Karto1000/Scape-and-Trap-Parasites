package srparasites_traps.features.proximity_sensor;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.registry.ModItems;
import srparasites_traps.util.StateManager;
import srparasites_traps.util.UpdateLimiter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ProximitySensorTileEntity extends TileCore implements ITickable {
    public static final int DEFAULT_RANGE = 10;

    @Nullable
    private ItemStack areaMarker;

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

    public Optional<ItemStack> getAreaMarker() {
        return Optional.ofNullable(this.areaMarker);
    }

    public void setAreaMarker(ItemStack areaMarker) {
        this.areaMarker = areaMarker;
    }

    private AxisAlignedBB getSearchAABB() {
        AxisAlignedBB defaultAABB = new AxisAlignedBB(this.pos).grow(DEFAULT_RANGE);

        if (this.areaMarker == null) return defaultAABB;

        NBTTagCompound tagCompound = this.areaMarker.getTagCompound();
        if (tagCompound == null) return defaultAABB;

        return AreaMarkerItem.getBoundAreaAsAABB(tagCompound).orElse(defaultAABB);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("areaMarkerNBT", Constants.NBT.TAG_COMPOUND)) {
            this.areaMarker = new ItemStack(ModItems.AREA_MARKER_ITEM);
            this.areaMarker.setTagCompound(compound.getCompoundTag("areaMarkerNBT"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (this.areaMarker != null && this.areaMarker.getTagCompound() != null)
            compound.setTag("areaMarkerNBT", this.areaMarker.getTagCompound());
        return super.writeToNBT(compound);
    }

    @Override
    public void update() {
        if (updateLimiter.tickDown()) return;
        updateLimiter.reset();

        AxisAlignedBB aabb = this.getSearchAABB();
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
