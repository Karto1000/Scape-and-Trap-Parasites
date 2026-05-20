package srparasites_traps.features.relocator;

import cofh.api.tileentity.IRedstoneControl;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.TurretTileEntity;
import srparasites_traps.features.relocation_marker.RelocationMarkerItem;
import srparasites_traps.util.DebugHelper;
import srparasites_traps.util.Pair;
import srparasites_traps.util.UpdateLimiter;
import srparasites_traps.util.VecHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RelocatorTileEntity extends TurretTileEntity implements ITickable, ICapabilityProvider, IRedstoneControl {
    public int randomBlockSelectionRetries = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_BLOCK_SELECTION_RETRIES;
    public int maxBlockHardness = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_BLOCK_HARDNESS;
    public int maxRelocatorsInReserve = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_RELOCATORS_IN_RESERVE;
    public int relocatorCreateDelay = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_RELOCATOR_CREATE_DELAY;
    public int biomassPerRelocatorSpawn = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_BIOMASS_FOR_SPAWN;
    public int energyPerTick = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_ENERGY_PER_TICK;
    public int allowedMaxSearchAreaDistance = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_SEARCH_AREA_DISTANCE;
    public int allowedMaxDestinationAreaDistance = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_DESTINATION_AREA_DISTANCE;

    private final UpdateLimiter updateLimiter = new UpdateLimiter(20);
    private ControlMode controlMode = ControlMode.DISABLED;
    private boolean powered = false;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (stack.isEmpty()) return false;

            if (!(stack.getItem() instanceof RelocationMarkerItem)) return false;

            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound == null) return false;

            Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundSearchArea(tagCompound);
            if (!searchAABB.isPresent()) return false;

            Pair<Double, Double> searchAreaDistances = RelocationMarkerItem.getDistancesOfAreaTo(searchAABB.get(), pos);
            if (searchAreaDistances.first() > allowedMaxSearchAreaDistance || searchAreaDistances.second() > allowedMaxDestinationAreaDistance)
                return false;

            Optional<AxisAlignedBB> destinationAABB = RelocationMarkerItem.getBoundDestinationArea(tagCompound);
            if (!destinationAABB.isPresent()) return false;

            Pair<Double, Double> destinationAreaDistances = RelocationMarkerItem.getDistancesOfAreaTo(destinationAABB.get(), pos);
            if (destinationAreaDistances.first() > allowedMaxDestinationAreaDistance || destinationAreaDistances.second() > allowedMaxDestinationAreaDistance)
                return false;

            return stack.getItem() instanceof RelocationMarkerItem;
        }
    };

    public void dropInventory() {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                this.world.spawnEntity(new EntityItem(this.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    private RelocatorTileEntityState state = RelocatorTileEntityState.IDLE;
    private RelocatorEntity spawnedRelocator;
    private UUID spawnedRelocatorUUID;

    private int currentRelocationDelay = 0;
    private int currentRelocatorCount = maxRelocatorsInReserve;
    private int currentRelocatorCreateDelay = 0;

    public RelocatorTileEntity() {
        super(
                ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_BIOMASS,
                ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_ENERGY
        );
    }

    public RelocatorTileEntityState getState() {
        return this.state;
    }

    public void setState(RelocatorTileEntityState state) {
        this.state = state;
    }

    public void setCurrentRelocatorCount(int count) {
        this.currentRelocatorCount = count;
    }

    public int getCurrentRelocatorCount() {
        return this.currentRelocatorCount;
    }

    public int getCurrentRelocatorCreateDelay() {
        return this.currentRelocatorCreateDelay;
    }

    public Optional<ItemStack> getAssignedRelocationMarker() {
        return Optional.of(this.inventory.getStackInSlot(0));
    }

    private Optional<RelocatorEntity> getSpawnedRelocator() {
        if (this.world.isRemote) return Optional.empty();

        if (this.spawnedRelocator != null) {
            if (this.spawnedRelocator.isDead) {
                this.spawnedRelocatorUUID = null;
                this.spawnedRelocator = null;
                return Optional.empty();
            }

            return Optional.of(spawnedRelocator);
        }

        Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.spawnedRelocatorUUID);

        if (!(entity instanceof RelocatorEntity)) {
            this.spawnedRelocatorUUID = null;
            return Optional.empty();
        }

        if (entity.isDead) {
            this.spawnedRelocatorUUID = null;
            return Optional.empty();
        }

        this.spawnedRelocator = (RelocatorEntity) entity;
        return Optional.of(this.spawnedRelocator);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("State")) {
            this.state = RelocatorTileEntityState.values()[compound.getInteger("State")];
        }
        if (compound.hasKey("CurrentRelocatorCount")) {
            this.currentRelocatorCount = compound.getInteger("CurrentRelocatorCount");
        }
        if (compound.hasKey("CurrentRelocatorCreateDelay")) {
            this.currentRelocatorCreateDelay = compound.getInteger("CurrentRelocatorCreateDelay");
        }
        if (compound.hasKey("CurrentRelocationDelay")) {
            this.currentRelocationDelay = compound.getInteger("CurrentRelocationDelay");
        }
        if (compound.hasKey("inventory")) {
            this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("State", this.state.ordinal());
        compound.setInteger("CurrentRelocatorCount", this.currentRelocatorCount);
        compound.setInteger("CurrentRelocatorCreateDelay", this.currentRelocatorCreateDelay);
        compound.setInteger("CurrentRelocationDelay", this.currentRelocationDelay);
        compound.setTag("inventory", this.inventory.serializeNBT());

        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR, this.getState().ordinal());
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 1, this.currentRelocatorCount);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 2, this.currentRelocatorCreateDelay);
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case AVAILABLE_WINDOW_VAR:
                this.setState(RelocatorTileEntityState.values()[data]);
            case AVAILABLE_WINDOW_VAR + 1:
                this.currentRelocatorCount = data;
            case AVAILABLE_WINDOW_VAR + 2:
                this.currentRelocatorCreateDelay = data;
        }
    }

    private boolean isBlockHardnessAcceptable(IBlockState blockState) {
        return blockState.getBlockHardness(world, pos) <= this.maxBlockHardness;
    }

    private Optional<BlockPos> getRandomDestinationPosition() {
        Optional<ItemStack> arm = getAssignedRelocationMarker();
        if (!arm.isPresent() || arm.get().isEmpty()) {
            DebugHelper.dbp("Relocation marker is not assigned, cannot get destination position");
            return Optional.empty();
        }
        ItemStack assignedRelocationMarker = arm.get();

        NBTTagCompound tagCompound = assignedRelocationMarker.getTagCompound();
        if (tagCompound == null) {
            DebugHelper.dbp("Relocation marker has no tag compound, cannot get destination position");
            return Optional.empty();
        }

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundDestinationArea(tagCompound);
        if (!searchAABB.isPresent()) {
            DebugHelper.dbp("Relocation marker destination area is not defined");
            return Optional.empty();
        }
        AxisAlignedBB aabb = searchAABB.get();

        Optional<BlockPos> result = Optional.empty();
        for (int i = 0; i < this.randomBlockSelectionRetries; i++) {
            if (result.isPresent()) break;

            BlockPos pos = VecHelper.getRandomPosition(this.world.rand, aabb);
            double deltaToBottom = pos.getY() - aabb.minY;

            for (int j = 0; j <= deltaToBottom; j++) {
                IBlockState blockState = this.world.getBlockState(pos.down(j));

                if (blockState.getMaterial() == Material.AIR || blockState.getMaterial().isLiquid()) {
                    DebugHelper.dbp(String.format("Skipping blockstate: %s air or liquid block at position " + pos.down(j), blockState));
                    continue;
                }
                ;
                if (!isBlockHardnessAcceptable(blockState)) {
                    DebugHelper.dbp("Skipping block with unacceptable hardness at position " + pos.down(j));
                    continue;
                }

                result = Optional.of(pos.down(j - 1));
                break;
            }
        }

        return result;
    }

    private static boolean isEntityValidForRelocation(EntityLivingBase entity) {
        return entity instanceof IMob && entity.isEntityAlive() && !entity.getEntityData().getBoolean("GrabbedByRelocator");
    }

    public List<EntityLivingBase> getRelocatableEntities() {
        Optional<ItemStack> arm = getAssignedRelocationMarker();
        if (!arm.isPresent() || arm.get().isEmpty()) return new ArrayList<>();
        ItemStack assignedRelocationMarker = arm.get();

        NBTTagCompound tagCompound = assignedRelocationMarker.getTagCompound();
        if (tagCompound == null) return new ArrayList<>();

        Optional<AxisAlignedBB> aabb = RelocationMarkerItem.getBoundSearchArea(tagCompound);
        if (!aabb.isPresent()) return new ArrayList<>();

        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb.get())
                .stream()
                .filter(RelocatorTileEntity::isEntityValidForRelocation)
                .collect(Collectors.toList());

        List<EntityLivingBase> resultList = new ArrayList<>();
        for (EntityLivingBase entity : entities) {
            IBlockState blockState = this.world.getBlockState(entity.getPosition().down(1));

            if (blockState.getMaterial() == Material.AIR || blockState.getMaterial().isLiquid()) continue;
            if (!isBlockHardnessAcceptable(blockState)) continue;

            resultList.add(entity);
        }

        return resultList;
    }

    @Override
    public void update() {
        if (this.state == RelocatorTileEntityState.RELOCATING) {
            Optional<RelocatorEntity> spawnedRelocator = getSpawnedRelocator();
            boolean isRelocatorValidForState = spawnedRelocator.isPresent() && spawnedRelocator.get().isEntityAlive();
            if (!isRelocatorValidForState) this.setState(RelocatorTileEntityState.IDLE);
        }

        if (this.energyStorage.getEnergyStored() < this.energyPerTick) return;
        else this.energyStorage.extractEnergy(this.energyPerTick, false);

        // Slowly increase the number of relocators in reserve
        if (this.currentRelocatorCount < this.maxRelocatorsInReserve && this.biomassStorage.getFluidAmount() >= this.biomassPerRelocatorSpawn) {
            if (this.currentRelocatorCreateDelay > 0) {
                this.currentRelocatorCreateDelay--;
            } else {
                this.currentRelocatorCreateDelay = this.relocatorCreateDelay;
                this.biomassStorage.drain(this.biomassPerRelocatorSpawn, true);
                this.currentRelocatorCount++;
            }
        }

        if (updateLimiter.tickDown()) return;
        updateLimiter.reset();

        Optional<ItemStack> arm = getAssignedRelocationMarker();
        if (!arm.isPresent() || arm.get().isEmpty()) {
            DebugHelper.dbp("No relocation marker assigned, cannot relocate");
            return;
        }
        ItemStack assignedRelocationMarker = arm.get();

        if (this.state != RelocatorTileEntityState.IDLE) return;
        if (this.currentRelocatorCount <= 0) return;
        if (this.controlMode != ControlMode.DISABLED && !this.powered) return;

        NBTTagCompound tagCompound = assignedRelocationMarker.getTagCompound();
        if (tagCompound == null) return;

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundSearchArea(tagCompound);
        if (!searchAABB.isPresent()) return;

        List<EntityLivingBase> entities = getRelocatableEntities();
        if (entities.isEmpty()) return;

        EntityLivingBase entity = entities.get(this.world.rand.nextInt(entities.size()));

        Optional<BlockPos> randomPos = getRandomDestinationPosition();
        if (!randomPos.isPresent()) return;

        this.spawnedRelocator = new RelocatorEntity(world, this.pos, entity.getPosition(), randomPos.get(), entity);
        world.spawnEntity(this.spawnedRelocator);
        this.setState(RelocatorTileEntityState.RELOCATING);

        this.currentRelocatorCount--;
    }

    @Override
    public boolean setControl(ControlMode controlMode) {
        this.controlMode = controlMode;

        if (this.world.isRemote) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player.openContainer == null) return false;
            mc.playerController.sendEnchantPacket(mc.player.openContainer.windowId, controlMode.ordinal());
        } else {
            this.markDirty();
        }

        return true;
    }

    @Override
    public ControlMode getControl() {
        return this.controlMode;
    }

    @Override
    public void setPowered(boolean b) {
        this.powered = b;
    }

    @Override
    public boolean isPowered() {
        return this.powered;
    }
}
