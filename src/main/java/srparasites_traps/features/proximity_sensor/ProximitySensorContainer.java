package srparasites_traps.features.proximity_sensor;

import cofh.core.gui.container.ContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.augments.AugmentInventory;
import srparasites_traps.features.augments.AugmentSlot;
import srparasites_traps.registry.ModItems;
import srparasites_traps.util.Vec2i;

import javax.annotation.Nonnull;

import static srparasites_traps.util.Translation.getClientStatusFor;

public class ProximitySensorContainer extends ContainerCore implements IAugmentableContainer {
    private final ProximitySensorTileEntity tileEntity;
    private final static Vec2i AREA_MARKER_SLOT = new Vec2i(7, 41);
    private final Slot[] augmentSlots = new Slot[1];

    public ProximitySensorContainer(EntityPlayer player, ProximitySensorTileEntity tileEntity) {
        this.tileEntity = tileEntity;

        this.addSlotToContainer(
                new SlotItemHandler(tileEntity.inventory, 0, AREA_MARKER_SLOT.x, AREA_MARKER_SLOT.y) {
                    @Override
                    public boolean isItemValid(@Nonnull ItemStack stack) {
                        if (stack.isEmpty()) return false;
                        if (stack.getItem() != ModItems.AREA_MARKER_ITEM) return false;

                        if (!super.isItemValid(stack)) {
                            if (!player.world.isRemote) return false;

                            player.sendStatusMessage(
                                    new TextComponentString(
                                            getClientStatusFor(
                                                    "area_marker.invalid_areas",
                                                    ForgeConfigHandler.proximitySensor.DEFAULT_MAX_AREA_DISTANCE
                                            )),
                                    true
                            );

                            return false;
                        }

                        return true;
                    }
                }
        );

        AugmentInventory<ProximitySensorTileEntity> augmentInventory = new AugmentInventory<>(tileEntity);
        for (int i = 0; i < this.augmentSlots.length; i++) {
            this.augmentSlots[i] = new AugmentSlot<>(augmentInventory, i, 0, 0, this.tileEntity);
            this.addSlotToContainer(this.augmentSlots[i]);
        }

        this.bindPlayerInventory(player.inventory);
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
        return 1;
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
        return augmentSlots;
    }
}
