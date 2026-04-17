package srparasites_traps.features.relocator;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Constants;

import static srparasites_traps.util.Constants.*;

public class RelocatorGui extends GuiContainerCore {
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/relocator.png");
    private final static int TANK_X_POSITION_PX = 8;
    private final static int TANK_Y_POSITION_PX = 16;
    private final static int TANK_WIDTH_PX = 16;
    private final static int TANK_HEIGHT_PX = 53;
    private final static int ENERGY_X_POSITION_PX = TANK_X_POSITION_PX + TANK_WIDTH_PX + 3;
    private final static int ENERGY_Y_POSITION_PX = TANK_Y_POSITION_PX - 1;
    private final static int ENERGY_WIDTH_PX = 15;
    private final static int ENERGY_HEIGHT_PX = 42;
    private final static double textScale = 0.75;
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
    }

    @Override
    protected void drawElements(float partialTick, boolean foreground) {
        super.drawElements(partialTick, foreground);
        GlStateManager.pushMatrix();
        GlStateManager.scale(textScale, textScale, textScale);
        this.fontRenderer.drawSplitString(
                String.format("> %s Relocators available", tileEntity.getCurrentRelocatorCount()),
                (int) (CONSOLE_X_POSITION_PX / textScale) + CONSOLE_TEXT_PADDING_PX,
                (int) (CONSOLE_Y_POSITION_PX / textScale) + CONSOLE_TEXT_PADDING_PX,
                164,
                0xFFFFFF
        );
        this.fontRenderer.drawSplitString(
                String.format("> %ss until new Relocator", tileEntity.getCurrentRelocatorCreateDelay() / Constants.TPS_LIMIT),
                (int) (CONSOLE_X_POSITION_PX / textScale) + CONSOLE_TEXT_PADDING_PX,
                (int) (CONSOLE_Y_POSITION_PX / textScale) + CONSOLE_TEXT_PADDING_PX + 16,
                164,
                0xFFFFFF
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
