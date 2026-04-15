package srparasites_traps.features.relocator;

import cofh.core.gui.container.ContainerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

public class RelocatorContainer extends ContainerCore {
    private final RelocatorTileEntity tileEntity;
    private final static int SLOT_X_POSITION_PX = 49;
    private final static int SLOT_Y_POSITION_PX = 16;

    public RelocatorContainer(InventoryPlayer playerInv, RelocatorTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        addSlotToContainer(new SlotItemHandler(this.tileEntity.inventory, 0, SLOT_X_POSITION_PX, SLOT_Y_POSITION_PX));
        bindPlayerInventory(playerInv);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            this.tileEntity.sendGuiNetworkData(this, listener);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        this.tileEntity.receiveGuiNetworkData(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {
        return 84;
    }

    @Override
    protected int getSizeInventory() {
        return 1;
    }
}
