package srparasites_traps.features.tesla_coil;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.gui.container.ContainerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TeslaCoilContainer extends ContainerCore {
    private final TeslaCoilTileEntity tileEntity;

    public TeslaCoilContainer(EntityPlayer player, TeslaCoilTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        bindPlayerInventory(player.inventory);
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (id < 0 || id >= IRedstoneControl.ControlMode.values().length) return false;
        IRedstoneControl.ControlMode controlMode = IRedstoneControl.ControlMode.values()[id];
        tileEntity.setControl(controlMode);
        return super.enchantItem(playerIn, id);
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
    protected int getPlayerInventoryVerticalOffset() {
        return 84;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
