package srparasites_traps.features.sentry_turret.base;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;

public class SentryTurretBaseGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/sentry_turret.png");
    private final static int TANK_X_POSITION_PX = 8;
    private final static int TANK_Y_POSITION_PX = 16;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = TANK_X_POSITION_PX + TANK_WIDTH_PX + 5;
    private final static int ENERGY_Y_POSITION_PX = TANK_Y_POSITION_PX - 1;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final static double textScale = 1;
    private final static int CONSOLE_X_POSITION_PX = 74;
    private final static int CONSOLE_Y_POSITION_PX = 18;
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
    }

    enum SentryState {
        ONLINE,
        MISSING_ENERGY,
        MISSING_BIOMASS;

        public String toString() {
            if (this == ONLINE) return "Online";
            if (this == MISSING_ENERGY) return "Offline\n> Reason: Missing\n energy";
            if (this == MISSING_BIOMASS) return "Offline\n> Reason: Missing\n biomass";
            return "Unknown";
        }
    }

    private SentryState getSentryState() {
        if (!tileEntity.hasEnoughEnergyToShoot()) return SentryState.MISSING_ENERGY;
        if (!tileEntity.hasEnoughBiomassToShoot()) return SentryState.MISSING_BIOMASS;
        return SentryState.ONLINE;
    }

    private int getSentryStateColor(SentryState state) {
        if (state == SentryState.ONLINE) return 0x00FF00;
        return 0xFF0000;
    }

    @Override
    protected void drawElements(float partialTick, boolean foreground) {
        super.drawElements(partialTick, foreground);
        GlStateManager.pushMatrix();
        GlStateManager.scale(textScale, textScale, textScale);
        SentryState sentryState = getSentryState();
        this.fontRenderer.drawSplitString(
                String.format("> Sentry %s", sentryState),
                (int) (CONSOLE_X_POSITION_PX / textScale),
                (int) (CONSOLE_Y_POSITION_PX / textScale),
                164,
                getSentryStateColor(sentryState)
        );
        GlStateManager.popMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
