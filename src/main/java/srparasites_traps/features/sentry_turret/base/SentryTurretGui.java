package srparasites_traps.features.sentry_turret.base;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.tab.TabRedstoneControl;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.sentry_turret.turret.SentryTileEntityState;
import srparasites_traps.util.Constants;

public class SentryTurretGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/sentry_turret.png");
    private final static int TANK_X_POSITION_PX = 8;
    private final static int TANK_Y_POSITION_PX = 16;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = TANK_X_POSITION_PX + TANK_WIDTH_PX + 3;
    private final static int ENERGY_Y_POSITION_PX = TANK_Y_POSITION_PX - 1;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final static double textScale = 0.75;
    private final SentryTurretTileEntity tileEntity;

    public SentryTurretGui(EntityPlayer player, SentryTurretTileEntity tileEntity) {
        super(new SentryTurretContainer(player.inventory, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        addElement(new ElementFluidTank(this, TANK_X_POSITION_PX, TANK_Y_POSITION_PX, this.tileEntity.biomassStorage).setSize(TANK_WIDTH_PX, TANK_HEIGHT_PX).setEnabled(true));
        addElement(new ElementEnergyStored(this, ENERGY_X_POSITION_PX, ENERGY_Y_POSITION_PX, this.tileEntity.energyStorage.getRfEnergyStorage())).setSize(ENERGY_WIDTH_PX, ENERGY_HEIGHT_PX).setEnabled(true);
        addTab(new TabRedstoneControl(this, this.tileEntity));
    }

    enum SentryGuiState {
        INACTIVE, ACTIVE, CANT_SPAWN, MISSING_ENERGY_SHOOT, MISSING_BIOMASS_SHOOT, SENTRY_DEAD;

        public String toString(SentryTurretTileEntity tileEntity) {
            if (this == ACTIVE) return "Active";
            if (this == CANT_SPAWN) return "Can't Spawn\n> Sentry turret requires\n at least 4 air blocks\n above the origin";
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
    protected void drawElements(float partialTick, boolean foreground) {
        super.drawElements(partialTick, foreground);
        GlStateManager.pushMatrix();
        GlStateManager.scale(textScale, textScale, textScale);
        SentryGuiState sentryState = getSentryGuiState();
        this.fontRenderer.drawSplitString(String.format("> Sentry %s", sentryState.toString(tileEntity)), (int) (Constants.CONSOLE_X_POSITION_PX / textScale) + Constants.CONSOLE_TEXT_PADDING_PX, (int) (Constants.CONSOLE_Y_POSITION_PX / textScale) + Constants.CONSOLE_TEXT_PADDING_PX, 164, getSentryStateColor(sentryState));
        GlStateManager.popMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
