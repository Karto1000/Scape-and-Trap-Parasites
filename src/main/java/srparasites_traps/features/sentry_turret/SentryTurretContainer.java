package srparasites_traps.features.sentry_turret;

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

public class SentryTurretContainer extends ContainerCore implements IAugmentableContainer {
    private final SentryTurretTileEntity tileEntity;
    private final Slot[] augmentSlots = new Slot[ForgeConfigHandler.augments.SENTRY_TURRET_AUGMENT_SLOTS];

    public SentryTurretContainer(
            EntityPlayer player,
            SentryTurretTileEntity tileEntity
    ) {
        this.tileEntity = tileEntity;

        AugmentInventory<SentryTurretTileEntity> augmentInventory = new AugmentInventory<>(tileEntity);
        for (int i = 0; i < this.augmentSlots.length; i++) {
            this.augmentSlots[i] = new AugmentSlot<>(augmentInventory, i, 0, 0, this.tileEntity);
            this.addSlotToContainer(this.augmentSlots[i]);
        }

        bindPlayerInventory(player.inventory);
    }

    // Not actually anything to do with enchanting, but its being used for setting the control mode on the server
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
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {
        return 84;
    }

    @Override
    protected int getSizeInventory() {
        return ForgeConfigHandler.augments.SENTRY_TURRET_AUGMENT_SLOTS;
    }

    @Override
    public void setAugmentLock(boolean b) {
    }

    @Override
    public Slot[] getAugmentSlots() {
        return this.augmentSlots;
    }
}
