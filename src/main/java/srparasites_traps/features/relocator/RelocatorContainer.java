package srparasites_traps.features.relocator;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.gui.container.ContainerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.area_marker.AreaMarkerItem;

import javax.annotation.Nonnull;

import static srparasites_traps.util.Translation.getServerStatusFor;

public class RelocatorContainer extends ContainerCore {
    private final RelocatorTileEntity tileEntity;
    private final static int SLOT_SEARCH_X_POSITION_PX = 88;
    private final static int SLOT_SEARCH_Y_POSITION_PX = 8;
    private final static int SLOT_DESTINATION_X_POSITION_PX = 88;
    private final static int SLOT_DESTINATION_Y_POSITION_PX = 45;

    public RelocatorContainer(EntityPlayer player, RelocatorTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        addSlotToContainer(new SlotItemHandler(this.tileEntity.inventory, 0, SLOT_SEARCH_X_POSITION_PX, SLOT_SEARCH_Y_POSITION_PX) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                if (stack.isEmpty()) return false;
                if (!(stack.getItem() instanceof AreaMarkerItem)) return false;

                boolean valid = super.isItemValid(stack);

                if (!valid) {
                    if (!player.world.isRemote) {
                        player.sendMessage(
                                new TextComponentTranslation(
                                        getServerStatusFor("area_marker.invalid_areas"),
                                        ForgeConfigHandler.relocator.DEFAULT_MAX_AREA_DISTANCE
                                )
                        );
                    }
                }

                return valid;
            }
        });

        addSlotToContainer(new SlotItemHandler(this.tileEntity.inventory, 1, SLOT_DESTINATION_X_POSITION_PX, SLOT_DESTINATION_Y_POSITION_PX) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                if (stack.isEmpty()) return false;
                if (!(stack.getItem() instanceof AreaMarkerItem)) return false;

                boolean valid = super.isItemValid(stack);

                if (!valid) {
                    if (!player.world.isRemote) {
                        player.sendMessage(
                                new TextComponentTranslation(
                                        getServerStatusFor("area_marker.invalid_areas"),
                                        ForgeConfigHandler.relocator.DEFAULT_MAX_AREA_DISTANCE
                                )
                        );
                    }
                }

                return valid;
            }
        });

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
