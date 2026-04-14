package srparasites_traps.features.relocator;

import cofh.core.block.TileCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import srparasites_traps.features.relocation_marker.RelocationMarkerItem;
import srparasites_traps.util.VecHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RelocatorTileEntity extends TileCore implements ITickable {
    private ItemStack assignedRelocationMarker = ItemStack.EMPTY;
    public int relocationDelay = 100;
    private int randomBlockSelectionRetries = 10;
    private int currentRelocationDelay = 0;
    private RelocatorTileEntityState state = RelocatorTileEntityState.IDLE;
    private RelocatorEntity spawnedRelocator;

    public RelocatorTileEntity() {
        super();
    }

    public void setRelocationMarker(ItemStack relocationMarker) {
        this.assignedRelocationMarker = relocationMarker;
    }

    public ItemStack getRelocationMarker() {
        return this.assignedRelocationMarker;
    }

    public RelocatorTileEntityState getState() {
        return this.state;
    }

    public void setState(RelocatorTileEntityState state) {
        this.state = state;
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

        if (compound.hasKey("relocationMarker")) {
            this.assignedRelocationMarker = new ItemStack(compound.getCompoundTag("relocationMarker"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (!this.assignedRelocationMarker.isEmpty()) {
            compound.setTag("relocationMarker", this.assignedRelocationMarker.serializeNBT());
        }

        return compound;
    }

    private Optional<BlockPos> getRandomDestinationPosition() {
        if (this.assignedRelocationMarker.isEmpty()) return Optional.empty();

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundDestinationArea(this.assignedRelocationMarker);
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

                result = Optional.of(pos.down(j - 1));
                break;
            }
        }

        return result;
    }

    @Override
    public void update() {
        if (this.world.isRemote) return;
        if (this.assignedRelocationMarker.isEmpty()) return;

        if (this.state == RelocatorTileEntityState.RELOCATING) {
            if (this.spawnedRelocator.isDead) {
                this.setState(RelocatorTileEntityState.IDLE);
                this.spawnedRelocator = null;
            }
        }

        if (this.currentRelocationDelay > 0) {
            this.currentRelocationDelay--;
            return;
        }

        if (this.state != RelocatorTileEntityState.IDLE) return;

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundSearchArea(this.assignedRelocationMarker);
        if (!searchAABB.isPresent()) return;

        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, searchAABB.get()).stream().filter(e -> e instanceof IMob && e.isEntityAlive()).collect(Collectors.toList());

        if (entities.isEmpty()) return;

        EntityLivingBase entity = entities.get(this.world.rand.nextInt(entities.size()));

        Optional<BlockPos> randomPos = getRandomDestinationPosition();
        if (!randomPos.isPresent()) return;

        this.spawnedRelocator = new RelocatorEntity(world, this.pos, entity.getPosition(), randomPos.get(), entity);
        world.spawnEntity(this.spawnedRelocator);
        this.setState(RelocatorTileEntityState.RELOCATING);

        this.currentRelocationDelay = this.relocationDelay;
    }
}
