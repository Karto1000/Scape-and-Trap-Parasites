package srparasites_traps.features.sentry_turret.base;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.gui.container.ContainerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SentryTurretContainer extends ContainerCore {
    private final SentryTurretTileEntity tileEntity;

    public SentryTurretContainer(InventoryPlayer playerInv, SentryTurretTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        bindPlayerInventory(playerInv);
    }

    // Not actually anything to do with enchanting, but its being used for setting the control mode on the server
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
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {
        return 84;
    }

    @Override
    protected int getSizeInventory() {
        return 2;
    }
}
