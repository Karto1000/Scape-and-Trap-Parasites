package srparasites_traps.features.relocator;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import srparasites_traps.util.VecHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RelocatorTileEntity extends TurretTileEntity implements ITickable, ICapabilityProvider {
    public int relocationDelay = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_RELOCATION_DELAY;
    public int randomBlockSelectionRetries = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_BLOCK_SELECTION_RETRIES;
    public int maxBlockHardness = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_BLOCK_HARDNESS;
    public int maxRelocatorsInReserve = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_RELOCATORS_IN_RESERVE;
    public int relocatorCreateDelay = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_RELOCATOR_CREATE_DELAY;
    public int biomassPerRelocatorSpawn = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_BIOMASS_FOR_SPAWN;
    public int energyPerTick = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_ENERGY_PER_TICK;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof RelocationMarkerItem;
        }
    };

    private RelocatorTileEntityState state = RelocatorTileEntityState.IDLE;
    private RelocatorEntity spawnedRelocator;
    private UUID spawnedRelocatorUUID;

    private int currentRelocationDelay = 0;
    private int currentRelocatorCount = 0;
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
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case AVAILABLE_WINDOW_VAR:
                this.setState(RelocatorTileEntityState.values()[data]);
        }
    }

    private boolean isBlockHardnessAcceptable(IBlockState blockState) {
        return blockState.getBlockHardness(world, pos) <= this.maxBlockHardness;
    }

    private Optional<BlockPos> getRandomDestinationPosition() {
        Optional<ItemStack> arm = getAssignedRelocationMarker();
        if (!arm.isPresent() || arm.get().isEmpty()) return Optional.empty();
        ItemStack assignedRelocationMarker = arm.get();

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundDestinationArea(assignedRelocationMarker);
        if (!searchAABB.isPresent()) return Optional.empty();
        AxisAlignedBB aabb = searchAABB.get();

        Optional<BlockPos> result = Optional.empty();
        for (int i = 0; i < this.randomBlockSelectionRetries; i++) {
            if (result.isPresent()) break;

            BlockPos pos = VecHelper.getRandomPosition(this.world.rand, aabb);
            double deltaToBottom = pos.getY() - aabb.minY;

            for (int j = 0; j <= deltaToBottom; j++) {
                IBlockState blockState = this.world.getBlockState(pos.down(j));

                if (blockState.getMaterial() == Material.AIR || blockState.getMaterial().isLiquid()) continue;
                if (!isBlockHardnessAcceptable(blockState)) continue;

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

        Optional<AxisAlignedBB> aabb = RelocationMarkerItem.getBoundSearchArea(assignedRelocationMarker);
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
        if (this.world.isRemote) return;

        if (this.state == RelocatorTileEntityState.RELOCATING) {
            Optional<RelocatorEntity> spawnedRelocator = getSpawnedRelocator();
            boolean isRelocatorValidForState = spawnedRelocator.isPresent() && spawnedRelocator.get().isEntityAlive();
            if (!isRelocatorValidForState) this.setState(RelocatorTileEntityState.IDLE);
        }

        Optional<ItemStack> arm = getAssignedRelocationMarker();
        if (!arm.isPresent() || arm.get().isEmpty()) return;
        ItemStack assignedRelocationMarker = arm.get();

        if (this.energyStorage.getEnergyStored() < this.energyPerTick) return;
        else this.energyStorage.extractEnergy(this.energyPerTick, false);

        // Slowly increase the number of relocators in reserve
        if (this.currentRelocatorCount < this.maxRelocatorsInReserve) {
            if (this.currentRelocatorCreateDelay > 0) {
                this.currentRelocatorCreateDelay--;
            } else {
                this.currentRelocatorCreateDelay = this.relocatorCreateDelay;
                this.currentRelocatorCount++;
            }
        }

        if (this.currentRelocationDelay > 0) {
            this.currentRelocationDelay--;
            return;
        }

        if (this.state != RelocatorTileEntityState.IDLE) return;
        if (this.currentRelocatorCount <= 0) return;

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundSearchArea(assignedRelocationMarker);
        if (!searchAABB.isPresent()) return;

        List<EntityLivingBase> entities = getRelocatableEntities();
        if (entities.isEmpty()) return;

        EntityLivingBase entity = entities.get(this.world.rand.nextInt(entities.size()));

        Optional<BlockPos> randomPos = getRandomDestinationPosition();
        if (!randomPos.isPresent()) return;

        this.spawnedRelocator = new RelocatorEntity(world, this.pos, entity.getPosition(), randomPos.get(), entity);
        world.spawnEntity(this.spawnedRelocator);
        this.setState(RelocatorTileEntityState.RELOCATING);
        this.biomassStorage.drain(this.biomassPerRelocatorSpawn, true);

        this.currentRelocatorCount--;
        this.currentRelocationDelay = this.relocationDelay;
    }
}
