package srparasites_traps.features.sentry_turret;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.tab.TabAugment;
import cofh.core.gui.element.tab.TabRedstoneControl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.GuiHelper;

public class SentryTurretGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/sentry_turret.png");
    private final static int TANK_X_POSITION_PX = 153;
    private final static int TANK_Y_POSITION_PX = 8;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = 118;
    private final static int ENERGY_Y_POSITION_PX = 7;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final SentryTurretTileEntity tileEntity;
    private GuiHelper.GuiConsole console;

    public SentryTurretGui(
            EntityPlayer player,
            SentryTurretTileEntity tileEntity
    ) {
        super(new SentryTurretContainer(player, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        int CONSOLE_X_POSITION_PX = 4;
        int CONSOLE_Y_POSITION_PX = 8;
        int CONSOLE_WIDTH_PX = 97;
        int CONSOLE_HEIGHT_PX = 55;
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

    enum SentryGuiState {
        INACTIVE, ACTIVE, CANT_SPAWN, MISSING_ENERGY_SHOOT, MISSING_BIOMASS_SHOOT, SENTRY_DEAD;

        public String toString(SentryTurretTileEntity tileEntity) {
            if (this == ACTIVE) return "Active";
            if (this == CANT_SPAWN)
                return "Can't Spawn\n> Sentry turret requires\n at least 4 air blocks\n above the origin";
            if (this == MISSING_ENERGY_SHOOT) return "Deployed\n> Missing energy";
            if (this == MISSING_BIOMASS_SHOOT) return "Deployed\n> Missing biomass";
            if (this == INACTIVE) return "Inactive";
            if (this == SENTRY_DEAD)
                return String.format("Dead\n> Cooldown %ds", (int) tileEntity.getCurrentRespawnTime());
            return "Unknown";
        }
    }

    private SentryGuiState getSentryGuiState() {
        if (tileEntity.areBlocksAboveOccupied()) return SentryGuiState.CANT_SPAWN;
        if (tileEntity.getState() == SentryTileEntityState.INACTIVE) return SentryGuiState.INACTIVE;
        if (tileEntity.getState() == SentryTileEntityState.DEAD) return SentryGuiState.SENTRY_DEAD;
        if (tileEntity.isMissingBiomass() && tileEntity.getState() != SentryTileEntityState.INACTIVE)
            return SentryGuiState.MISSING_BIOMASS_SHOOT;
        if (tileEntity.isMissingEnergy()) return SentryGuiState.MISSING_ENERGY_SHOOT;
        return SentryGuiState.ACTIVE;
    }

    private int getSentryStateColor(SentryGuiState state) {
        if (state == SentryGuiState.ACTIVE) return 0x00FF00;
        if (state == SentryGuiState.INACTIVE) return 0xFFEB3B;
        return 0xFF0000;
    }

    @Override
    protected void drawElements(
            float partialTick,
            boolean foreground
    ) {
        super.drawElements(partialTick, foreground);

        SentryGuiState sentryState = getSentryGuiState();
        String text = String.format(
                "> Turret: %s\n\n> Damage: %s Hearts\n> Fire Delay: %s Ticks\n> Range: %s Blocks",
                sentryState.toString(tileEntity),
                tileEntity.damage,
                tileEntity.attackDelay,
                tileEntity.attackRange
        );

        this.console.drawElements(text, getSentryStateColor(sentryState));
    }

    @Override
    public void drawScreen(
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
