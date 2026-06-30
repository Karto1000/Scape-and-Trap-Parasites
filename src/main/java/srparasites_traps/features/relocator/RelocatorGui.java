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
import srparasites_traps.util.Constants;
import srparasites_traps.util.GuiHelper;
import srparasites_traps.util.Translation;

import java.util.Collections;

public class RelocatorGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/relocator_gui.png");
    private final static int TANK_X_POSITION_PX = 153;
    private final static int TANK_Y_POSITION_PX = 8;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = 118;
    private final static int ENERGY_Y_POSITION_PX = 7;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final RelocatorTileEntity tileEntity;
    private GuiHelper.GuiConsole console;

    public RelocatorGui(
            EntityPlayer player,
            RelocatorTileEntity tileEntity
    ) {
        super(new RelocatorContainer(player, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        int CONSOLE_X_POSITION_PX = 7;
        int CONSOLE_Y_POSITION_PX = 7;
        int CONSOLE_WIDTH_PX = 64;
        int CONSOLE_HEIGHT_PX = 64;
        double TEXT_SCALE = 0.75;
        this.console = GuiHelper.GuiConsole.init(
                this.fontRenderer,
                CONSOLE_X_POSITION_PX,
                CONSOLE_Y_POSITION_PX,
                CONSOLE_WIDTH_PX,
                CONSOLE_HEIGHT_PX,
                TEXT_SCALE
        );

        addElement(new ElementFluidTank(this, TANK_X_POSITION_PX, TANK_Y_POSITION_PX, this.tileEntity.biomassStorage).setSize(TANK_WIDTH_PX, TANK_HEIGHT_PX).setEnabled(true));
        addElement(new ElementEnergyStored(this, ENERGY_X_POSITION_PX, ENERGY_Y_POSITION_PX, this.tileEntity.energyStorage.getRfEnergyStorage())).setSize(ENERGY_WIDTH_PX, ENERGY_HEIGHT_PX).setEnabled(true);
        addTab(new TabAugment(this, (IAugmentableContainer) this.inventorySlots));
        addTab(new TabRedstoneControl(this, this.tileEntity));
    }

    private enum RelocatorGuiState {
        IDLE, RELOCATING, MISSING_ENERGY, MISSING_BIOMASS;

        public String toString() {
            if (this == RELOCATING) return "Relocating";
            if (this == MISSING_ENERGY) return "\n> Missing energy";
            if (this == MISSING_BIOMASS) return "\n> Missing biomass";
            if (this == IDLE) return "Idle";
            return "Unknown";
        }
    }

    private RelocatorGuiState getRelocatorGuiState() {
        if (tileEntity.isMissingBiomass()) return RelocatorGuiState.MISSING_BIOMASS;
        if (tileEntity.isMissingEnergy()) return RelocatorGuiState.MISSING_ENERGY;
        if (tileEntity.getState() == RelocatorTileEntityState.RELOCATING) return RelocatorGuiState.RELOCATING;
        if (tileEntity.getState() == RelocatorTileEntityState.IDLE) return RelocatorGuiState.IDLE;
        return RelocatorGuiState.IDLE;
    }

    private int getRelocatorStateColor(RelocatorGuiState state) {
        if (state == RelocatorGuiState.MISSING_ENERGY || state == RelocatorGuiState.MISSING_BIOMASS) return 0xFF0000;
        return 0x00FF00;
    }

    @Override
    protected void drawElements(
            float partialTick,
            boolean foreground
    ) {
        super.drawElements(partialTick, foreground);

        RelocatorGuiState state = getRelocatorGuiState();
        String text = String.format(
                "> Turret: %s\n\n> %s Units\n> %ss Create Cooldown",
                state.toString(),
                this.tileEntity.getCurrentRelocatorCount(),
                this.tileEntity.getCurrentRelocatorCreateDelay() / Constants.TPS_LIMIT
        );
        this.console.drawElements(
                text,
                getRelocatorStateColor(state)
        );
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
