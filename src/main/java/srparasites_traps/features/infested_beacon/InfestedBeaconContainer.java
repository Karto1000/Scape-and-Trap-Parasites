package srparasites_traps.features.infested_beacon;

import cofh.core.gui.container.ContainerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class InfestedBeaconContainer extends ContainerCore {
    private final InfestedBeaconTileEntity tileEntity;

    public InfestedBeaconContainer(EntityPlayer player, InfestedBeaconTileEntity tileEntity) {
        super();

        this.tileEntity = tileEntity;
        bindPlayerInventory(player.inventory);
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
}
