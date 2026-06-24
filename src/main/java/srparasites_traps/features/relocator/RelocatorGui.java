package srparasites_traps.features.relocator;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.tab.TabAugment;
import cofh.core.gui.element.tab.TabRedstoneControl;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Translation;

import java.util.Collections;

public class RelocatorGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/relocator.png");
    private final static int TANK_X_POSITION_PX = 153;
    private final static int TANK_Y_POSITION_PX = 8;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = 118;
    private final static int ENERGY_Y_POSITION_PX = 7;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final RelocatorTileEntity tileEntity;

    public RelocatorGui(EntityPlayer player, RelocatorTileEntity tileEntity) {
        super(new RelocatorContainer(player, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        addElement(new ElementFluidTank(this, TANK_X_POSITION_PX, TANK_Y_POSITION_PX, this.tileEntity.biomassStorage).setSize(TANK_WIDTH_PX, TANK_HEIGHT_PX).setEnabled(true));
        addElement(new ElementEnergyStored(this, ENERGY_X_POSITION_PX, ENERGY_Y_POSITION_PX, this.tileEntity.energyStorage.getRfEnergyStorage())).setSize(ENERGY_WIDTH_PX, ENERGY_HEIGHT_PX).setEnabled(true);
        addTab(new TabAugment(this, (IAugmentableContainer) this.inventorySlots));
        addTab(new TabRedstoneControl(this, this.tileEntity));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (isMouseOverSlot(this.inventorySlots.getSlot(0), mouseX, mouseY)) {
            this.drawHoveringText(
                    Collections.singletonList(I18n.format(Translation.getSlotDescriptionFor("relocator_search"))),
                    mouseX,
                    mouseY
            );
        }

        if (isMouseOverSlot(this.inventorySlots.getSlot(1), mouseX, mouseY)) {
            this.drawHoveringText(
                    Collections.singletonList(I18n.format(Translation.getSlotDescriptionFor("relocator_destination"))),
                    mouseX,
                    mouseY
            );
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
