package srparasites_traps.features.sentry_turret.base;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.sentry_turret.turret.SentryTileEntityState;
import srparasites_traps.network.SRParasitesTrapsNetwork;
import srparasites_traps.network.ToggleSentryPacket;
import srparasites_traps.util.Constants;
import srparasites_traps.util.GuiHelper;
import srparasites_traps.util.Translation;

public class SentryTurretBaseGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/sentry_turret.png");
    private final static ResourceLocation BUTTON_TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/button.png");
    private final static int TANK_X_POSITION_PX = 8;
    private final static int TANK_Y_POSITION_PX = 16;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = TANK_X_POSITION_PX + TANK_WIDTH_PX + 3;
    private final static int ENERGY_Y_POSITION_PX = TANK_Y_POSITION_PX - 1;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final static double textScale = 1;
    private final static int TEXT_PADDING_PX = 2;
    private final static int CONSOLE_X_POSITION_PX = 72;
    private final static int CONSOLE_Y_POSITION_PX = 15;
    private final static int BUTTON_X_POSITION_PX = 46;
    private final static int BUTTON_Y_POSITION_PX = 15;
    private final static String TOGGLE_BUTTON_NAME = "toggle";
    private final SentryTurretBaseTileEntity tileEntity;

    public SentryTurretBaseGui(InventoryPlayer playerInv, SentryTurretBaseTileEntity tileEntity) {
        super(new SentryTurretBaseContainer(playerInv, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        addElement(new ElementFluidTank(this, TANK_X_POSITION_PX, TANK_Y_POSITION_PX, this.tileEntity.biomassStorage).setSize(TANK_WIDTH_PX, TANK_HEIGHT_PX).setEnabled(true));
        addElement(new ElementEnergyStored(this, ENERGY_X_POSITION_PX, ENERGY_Y_POSITION_PX, this.tileEntity.energyStorage.getRfEnergyStorage())).setSize(ENERGY_WIDTH_PX, ENERGY_HEIGHT_PX).setEnabled(true);
        addElement(GuiHelper.createNewButton(this, TOGGLE_BUTTON_NAME, BUTTON_X_POSITION_PX, BUTTON_Y_POSITION_PX).setToolTip(Translation.getTooltipFor("gui.sentry_turret.toggle")));
    }

    enum SentryGuiState {
        INACTIVE, DEPLOYED, MISSING_ENERGY_SPAWN, DEPLOYED_MISSING_ENERGY, MISSING_BIOMASS_SHOOT, MISSING_BIOMASS_SPAWN, SENTRY_DEAD;

        public String toString(SentryTurretBaseTileEntity tileEntity) {
            if (this == DEPLOYED) return "Deployed";
            if (this == DEPLOYED_MISSING_ENERGY) return "Deployed\n> Missing energy";
            if (this == MISSING_BIOMASS_SHOOT) return "Deployed\n> Missing biomass";
            if (this == MISSING_ENERGY_SPAWN) return "Inactive\n> Missing energy\n for spawn";
            if (this == MISSING_BIOMASS_SPAWN) return "Inactive\n> Missing biomass\n for spawn";
            if (this == INACTIVE) return "Inactive\n> Press button to\n activate";
            if (this == SENTRY_DEAD)
                return String.format("Inactive\n> Cooldown %ds", (int) tileEntity.getCurrentRespawnTime());
            return "Unknown";
        }
    }

    @Override
    public void handleElementButtonClick(String buttonName, int mouseButton) {
        if (buttonName.equals(TOGGLE_BUTTON_NAME)) {
            if (mouseButton == Constants.MOUSE_BUTTON_LEFT) {
                playClickSound(1F);
                SRParasitesTrapsNetwork.CHANNEL.sendToServer(new ToggleSentryPacket(tileEntity.getPos()));
            }
        }
    }

    private SentryGuiState getSentryGuiState() {
        if (tileEntity.getState() == SentryTileEntityState.DEAD) return SentryGuiState.SENTRY_DEAD;
        if (!tileEntity.hasEnoughBiomassForSpawn() && tileEntity.getState() != SentryTileEntityState.ACTIVE)
            return SentryGuiState.MISSING_BIOMASS_SPAWN;
        if (tileEntity.getState() == SentryTileEntityState.INACTIVE && !tileEntity.hasEnoughEnergy())
            return SentryGuiState.MISSING_ENERGY_SPAWN;
        if (!tileEntity.hasEnoughBiomassToShoot() && tileEntity.getState() != SentryTileEntityState.DEAD)
            return SentryGuiState.MISSING_BIOMASS_SHOOT;
        if (tileEntity.getState() == SentryTileEntityState.INACTIVE) return SentryGuiState.INACTIVE;
        if (!tileEntity.hasEnoughEnergy()) return SentryGuiState.DEPLOYED_MISSING_ENERGY;
        return SentryGuiState.DEPLOYED;
    }

    private int getSentryStateColor(SentryGuiState state) {
        if (state == SentryGuiState.DEPLOYED) return 0x00FF00;
        if (state == SentryGuiState.INACTIVE) return 0xFFEB3B;
        return 0xFF0000;
    }

    @Override
    protected void drawElements(float partialTick, boolean foreground) {
        super.drawElements(partialTick, foreground);
        GlStateManager.pushMatrix();
        GlStateManager.scale(textScale, textScale, textScale);
        SentryGuiState sentryState = getSentryGuiState();
        this.fontRenderer.drawSplitString(String.format("> Sentry %s", sentryState.toString(tileEntity)), (int) (CONSOLE_X_POSITION_PX / textScale) + TEXT_PADDING_PX, (int) (CONSOLE_Y_POSITION_PX / textScale) + TEXT_PADDING_PX, 164, getSentryStateColor(sentryState));
        GlStateManager.popMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
