package srparasites_traps.features.proximity_sensor;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.IExtendedAugmentable;
import srparasites_traps.features.area_marker.AreaMarkerItem;
import srparasites_traps.features.augments.AugmentCompatibility;
import srparasites_traps.features.augments.TargetingAugment;
import srparasites_traps.registry.ModBlocks;
import srparasites_traps.registry.ModItems;
import srparasites_traps.util.Pair;
import srparasites_traps.util.StateManager;
import srparasites_traps.util.UpdateLimiter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ProximitySensorTileEntity extends TileCore implements ITickable, IExtendedAugmentable {
    private final UpdateLimiter updateLimiter = new UpdateLimiter(10);
    private final StateManager<ProximitySensorState> stateManager = new StateManager<>(
            ProximitySensorState.INACTIVE,
            (oldState, newState) -> {
                IBlockState state = this.world.getBlockState(this.pos).withProperty(
                        ProximitySensorBlock.active,
                        newState == ProximitySensorState.ACTIVE
                );
                this.world.setBlockState(this.pos, state, 3);
                this.world.notifyNeighborsOfStateChange(this.pos, ModBlocks.PROXIMITY_SENSOR, true);
            }
    );
    private final ItemStack[] augments = Stream.generate(() -> ItemStack.EMPTY)
            .limit(1)
            .toArray(ItemStack[]::new);

    public static final int DEFAULT_RANGE = 10;
    public ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (stack.getItem() != ModItems.AREA_MARKER_ITEM) return false;
            if (!isDistanceAllowed(stack)) return false;

            return AreaMarkerItem.hasBoundPosition(stack);
        }
    };

    private boolean isDistanceAllowed(ItemStack areaMarker) {
        NBTTagCompound tagCompound = areaMarker.getTagCompound();
        if (tagCompound == null) return false;

        Optional<AxisAlignedBB> aabb = AreaMarkerItem.getBoundAreaAsAABB(tagCompound);
        if (!aabb.isPresent()) return false;

        AxisAlignedBB searchArea = aabb.get();
        Pair<Double, Double> distances = AreaMarkerItem.getDistancesOfAreaTo(searchArea, this.getPos());
        return distances.first() <= ForgeConfigHandler.proximitySensor.DEFAULT_MAX_AREA_DISTANCE && distances.second() <= ForgeConfigHandler.proximitySensor.DEFAULT_MAX_AREA_DISTANCE;
    }

    public ProximitySensorState getState() {
        return stateManager.getState();
    }

    public Optional<ItemStack> getAreaMarker() {
        ItemStack areaMarker = this.inventory.getStackInSlot(0);
        if (areaMarker.isEmpty()) return Optional.empty();
        return Optional.of(areaMarker);
    }

    private AxisAlignedBB getSearchAABB() {
        AxisAlignedBB defaultAABB = new AxisAlignedBB(this.pos).grow(DEFAULT_RANGE);

        Optional<ItemStack> areaMarkerO = this.getAreaMarker();
        if (!areaMarkerO.isPresent()) return defaultAABB;

        ItemStack areaMarker = areaMarkerO.get();
        NBTTagCompound tagCompound = areaMarker.getTagCompound();
        if (tagCompound == null) return defaultAABB;

        return AreaMarkerItem.getBoundAreaAsAABB(tagCompound).orElse(defaultAABB);
    }

    @Override
    public boolean shouldRefresh(
            @Nonnull World world,
            @Nonnull BlockPos pos,
            IBlockState oldState,
            IBlockState newSate
    ) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.readAugmentsFromNBT(compound);
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag("inventory", this.inventory.serializeNBT());
        this.writeAugmentsToNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        switch (id) {
            case 0:
                this.stateManager.setState(ProximitySensorState.values()[data]);
                break;
        }
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        player.sendWindowProperty(container, 0, this.stateManager.getState().ordinal());
    }

    @Override
    public void update() {
        if (updateLimiter.tickDown()) return;
        updateLimiter.reset();

        AxisAlignedBB aabb = this.getSearchAABB();
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityParasiteBase.class, aabb);

        if (!this.augments[0].isEmpty()) {
            boolean anyValid = false;

            for (EntityLivingBase entity : entities) {
                if (TargetingAugment.isEntityValidForAugment(this.augments[0], entity)) {
                    anyValid = true;
                }
            }

            if (!anyValid) {
                if (stateManager.isState(ProximitySensorState.ACTIVE))
                    stateManager.switchState(this.world.getTotalWorldTime());
                return;
            }
        }

        if (entities.isEmpty()) {
            if (stateManager.isState(ProximitySensorState.ACTIVE))
                stateManager.switchState(this.world.getTotalWorldTime());
            return;
        }


        if (stateManager.isState(ProximitySensorState.ACTIVE)) return;
        stateManager.switchState(this.world.getTotalWorldTime());
    }

    @Override
    public void applyAugment(ItemStack augment) {

    }

    @Override
    public boolean isValidAugment(ItemStack itemStack) {
        return AugmentCompatibility.isValidFor(ProximitySensorTileEntity.class, itemStack);
    }

    @Override
    public ItemStack[] getAugmentSlots() {
        return augments;
    }

    @Override
    public void applyDefaults() {

    }
}
