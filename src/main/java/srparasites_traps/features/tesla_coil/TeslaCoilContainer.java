package srparasites_traps.features.tesla_coil;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.gui.container.ContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.augments.AugmentInventory;
import srparasites_traps.features.augments.AugmentSlot;

import javax.annotation.Nonnull;

public class TeslaCoilContainer extends ContainerCore implements IAugmentableContainer {
    private final TeslaCoilTileEntity tileEntity;
    private final Slot[] augmentSlots = new Slot[ForgeConfigHandler.augments.TESLA_COIL_AUGMENT_SLOTS];

    public TeslaCoilContainer(
            EntityPlayer player,
            TeslaCoilTileEntity tileEntity
    ) {
        this.tileEntity = tileEntity;

        AugmentInventory<TeslaCoilTileEntity> augmentInventory = new AugmentInventory<>(tileEntity);
        for (int i = 0; i < this.augmentSlots.length; i++) {
            this.augmentSlots[i] = new AugmentSlot<>(augmentInventory, i, 0, 0, this.tileEntity);
            this.addSlotToContainer(this.augmentSlots[i]);
        }

        bindPlayerInventory(player.inventory);
    }

    @Override
    public boolean enchantItem(
            @Nonnull EntityPlayer playerIn,
            int id
    ) {
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
    public void updateProgressBar(
            int id,
            int data
    ) {
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
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void setAugmentLock(boolean b) {

    }

    @Override
    public Slot[] getAugmentSlots() {
        return this.augmentSlots;
    }
}
