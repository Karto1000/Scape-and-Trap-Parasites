package srparasites_traps.features.biomass_factory;

import cofh.core.gui.container.ContainerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.items.SlotItemHandler;
import srparasites_traps.util.Constants;

import javax.annotation.Nonnull;

public class BiomassFactoryContainer extends ContainerCore {
    private final BiomassFactoryTileEntity tileEntity;
    private final EntityPlayer player;
    private final static int INVENTORY_WIDTH = 2;
    private final static int INVENTORY_HEIGHT = 3;
    private final static int FIRST_SLOT_X_POSITION = 136;
    private final static int FIRST_SLOT_Y_POSITION_PX = 8;
    private static final int BIOMASS_AMOUNT_ID = 0;
    private int lastBiomassAmount = -1;

    public static final int INPUT_SLOT_START = 0;
    public static final int INPUT_SLOT_COUNT = INVENTORY_WIDTH * INVENTORY_HEIGHT;
    public static final int FLUID_FILL_SLOT = INPUT_SLOT_START + INPUT_SLOT_COUNT;
    public static final int FLUID_OUTPUT_SLOT = FLUID_FILL_SLOT + 1;
    public static final int TILE_SLOT_COUNT = FLUID_OUTPUT_SLOT + 1;


    public BiomassFactoryContainer(
            EntityPlayer player,
            BiomassFactoryTileEntity tileEntity
    ) {
        super();

        this.player = player;
        this.tileEntity = tileEntity;

        for (int y = 0; y < INVENTORY_HEIGHT; y++) {
            for (int x = 0; x < INVENTORY_WIDTH; x++) {
                addSlotToContainer(new SlotItemHandler(
                        this.tileEntity.inputInventory,
                        x + y * INVENTORY_WIDTH,
                        FIRST_SLOT_X_POSITION + x * Constants.INVENTORY_SLOT_WIDTH_PX,
                        FIRST_SLOT_Y_POSITION_PX + y * Constants.INVENTORY_SLOT_HEIGHT_PX
                ));
            }
        }

        addSlotToContainer(new SlotItemHandler(this.tileEntity.fluidFillInventory, 0, 63, 8));
        addSlotToContainer(new SlotItemHandler(this.tileEntity.fluidOutputInventory, 0, 63, 45));

        bindPlayerInventory(player.inventory);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        int biomassAmount = this.tileEntity.biomassStorage.getFluidAmount();

        if (this.lastBiomassAmount != biomassAmount) {
            this.lastBiomassAmount = biomassAmount;

            for (IContainerListener listener : this.listeners) {
                listener.sendWindowProperty(this, BIOMASS_AMOUNT_ID, biomassAmount);
            }
        }
    }

    @Override
    public void updateProgressBar(
            int id,
            int data
    ) {
        super.updateProgressBar(id, data);

        if (id == BIOMASS_AMOUNT_ID) {
            this.tileEntity.biomassStorage.set(data);
        }
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
        return TILE_SLOT_COUNT;
    }
}
