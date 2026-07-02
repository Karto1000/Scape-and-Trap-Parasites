package srparasites_traps.features.proximity_sensor;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.tab.TabAugment;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.GuiHelper;
import srparasites_traps.util.Translation;

import java.util.Collections;

public class ProximitySensorGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(
            SRParasitesTraps.MOD_ID,
            "textures/gui/proximity_sensor_gui.png"
    );
    private final ProximitySensorTileEntity tileEntity;
    private GuiHelper.GuiConsole guiConsole;

    public ProximitySensorGui(EntityPlayer player, ProximitySensorTileEntity tileEntity) {
        super(new ProximitySensorContainer(player, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.addTab(new TabAugment(this, (IAugmentableContainer) this.inventorySlots));

        this.guiConsole = GuiHelper.GuiConsole.init(
                this.fontRenderer,
                39,
                8,
                131,
                61,
                1
        );
    }

    @Override
    protected void drawElements(float partialTick, boolean foreground) {
        super.drawElements(partialTick, foreground);

        String state = this.tileEntity.getState() == ProximitySensorState.INACTIVE ? "Unpowered" : "Powered";
        String consoleText = String.format(
                "> State: %s\n> %s",
                state,
                this.inventorySlots.getSlot(0).getStack().isEmpty() ?
                        "No Area Marker installed, Searching for entities in a " + ProximitySensorTileEntity.DEFAULT_RANGE + " Block radius."
                        : "Searching in the area defined by the Area Marker."
        );

        this.guiConsole.drawElements(consoleText, 0xFFFFFF);
    }

    @Override
    public void drawScreen(
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (isMouseOverSlot(this.inventorySlots.getSlot(0), mouseX, mouseY)) {
            this.drawHoveringText(
                    Collections.singletonList(I18n.format(Translation.getSlotDescriptionFor("proximity_sensor_search"))),
                    mouseX,
                    mouseY
            );
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
