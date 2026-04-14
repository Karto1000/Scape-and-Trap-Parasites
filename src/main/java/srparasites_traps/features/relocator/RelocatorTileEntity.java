package srparasites_traps.features.relocator;

import cofh.core.block.TileCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import srparasites_traps.features.relocation_marker.RelocationMarkerItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class RelocatorTileEntity extends TileCore implements ITickable {
    public int range = 10;
    private ItemStack assignedRelocationMarker = ItemStack.EMPTY;

    public RelocatorTileEntity() {
        super();
    }

    public void setRelocationMarker(ItemStack relocationMarker) {
        this.assignedRelocationMarker = relocationMarker;
    }

    public ItemStack getRelocationMarker() {
        return this.assignedRelocationMarker;
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

    @Override
    public void update() {
        if (this.world.isRemote) return;
        if (this.assignedRelocationMarker.isEmpty()) return;

        Optional<AxisAlignedBB> searchAABB = RelocationMarkerItem.getBoundSearchArea(this.assignedRelocationMarker);
        if (!searchAABB.isPresent()) return;

        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, searchAABB.get());
        System.out.println(list);
    }
}
