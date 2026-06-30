package srparasites_traps.features.infested_beacon;

import cofh.core.gui.GuiContainerCore;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Constants;
import srparasites_traps.util.GuiHelper;
import srparasites_traps.util.Translation;
import srparasites_traps.util.Vec2i;

import java.util.Collections;

public class InfestedBeaconGui extends GuiContainerCore {
    private final InfestedBeaconTileEntity tileEntity;
    private final static Vec2i POWER_METER_POS = new Vec2i(81, 56);
    private final static int POWER_METER_HEIGHT = 44;
    private final static int POWER_METER_WIDTH = 9;
    private final static Vec2i NORTH_WEST_LURE_SLOT_POS = new Vec2i(6, 10);
    private final static Vec2i NORTH_EAST_LURE_SLOT_POS = new Vec2i(
            NORTH_WEST_LURE_SLOT_POS.x + 32,
            NORTH_WEST_LURE_SLOT_POS.y
    );
    private final static Vec2i SOUTH_WEST_LURE_SLOT_POS = new Vec2i(
            NORTH_WEST_LURE_SLOT_POS.x,
            NORTH_WEST_LURE_SLOT_POS.y + 32
    );
    private final static Vec2i SOUTH_EAST_LURE_SLOT_POS = new Vec2i(
            NORTH_WEST_LURE_SLOT_POS.x + 32,
            NORTH_WEST_LURE_SLOT_POS.y + 32
    );

    public static final ResourceLocation TEXTURE = new ResourceLocation(
            SRParasitesTraps.MOD_ID,
            "textures/gui/infested_beacon_gui.png"
    );

    public ResourceLocation getLureTexture(int tier) {
        return new ResourceLocation(Constants.SRPARASITES_MOD_ID, "textures/blocks/lure" + tier + ".png");
    }

    public InfestedBeaconGui(EntityPlayer player, InfestedBeaconTileEntity tileEntity) {
        super(new InfestedBeaconContainer(player, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    private void drawLureHelpText(int lureX, int lureY, int mouseX, int mouseY) {
        if (mouseX >= this.guiLeft + lureX &&
                mouseX <= lureX + this.guiLeft + 16 &&
                mouseY >= lureY + this.guiTop &&
                mouseY <= lureY + this.guiTop + 16
        ) {
            this.drawHoveringText(
                    Collections.singletonList(I18n.format(Translation.getSlotDescriptionFor("infested_beacon_lure"))),
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    public void drawScreen(int x, int y, float partialTick) {
        super.drawScreen(x, y, partialTick);

        byte[] powerLevels = this.tileEntity.getPowerLevels();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();

        if (powerLevels[0] > 0) {
            mc.getTextureManager().bindTexture(getLureTexture(powerLevels[0]));
            InfestedBeaconGui.drawScaledCustomSizeModalRect(
                    this.guiLeft + NORTH_WEST_LURE_SLOT_POS.x,
                    this.guiTop + NORTH_WEST_LURE_SLOT_POS.y,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16,
                    16,
                    16
            );
        }

        if (powerLevels[1] > 0) {
            mc.getTextureManager().bindTexture(getLureTexture(powerLevels[1]));
            InfestedBeaconGui.drawScaledCustomSizeModalRect(
                    this.guiLeft + NORTH_EAST_LURE_SLOT_POS.x,
                    this.guiTop + NORTH_EAST_LURE_SLOT_POS.y,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16,
                    16,
                    16
            );
        }

        if (powerLevels[2] > 0) {
            mc.getTextureManager().bindTexture(getLureTexture(powerLevels[2]));
            InfestedBeaconGui.drawScaledCustomSizeModalRect(
                    this.guiLeft + SOUTH_EAST_LURE_SLOT_POS.x,
                    this.guiTop + SOUTH_EAST_LURE_SLOT_POS.y,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16,
                    16,
                    16
            );
        }

        if (powerLevels[3] > 0) {
            mc.getTextureManager().bindTexture(getLureTexture(powerLevels[3]));
            InfestedBeaconGui.drawScaledCustomSizeModalRect(
                    this.guiLeft + SOUTH_WEST_LURE_SLOT_POS.x,
                    this.guiTop + SOUTH_WEST_LURE_SLOT_POS.y,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16,
                    16,
                    16
            );
        }


        int totalPower = this.tileEntity.getTotalPower();
        float pxPerLevel = (float) POWER_METER_HEIGHT / this.tileEntity.getMaxPower();
        int powerMeterHeight = (int) (pxPerLevel * totalPower);

        this.drawSizedRect(
                this.guiLeft + POWER_METER_POS.x,
                this.guiTop + POWER_METER_POS.y - powerMeterHeight,
                this.guiLeft + POWER_METER_POS.x + POWER_METER_WIDTH,
                this.guiTop + POWER_METER_POS.y,
                0xFF00FBFF
        );

        GuiHelper.drawHoveringTextOnRect(
                this,
                79,
                10,
                POWER_METER_WIDTH + 4,
                POWER_METER_HEIGHT + 4,
                x,
                y,
                "infested_beacon_power",
                (int) ((float) totalPower / this.tileEntity.getMaxPower() * 100)
        );

        drawLureHelpText(NORTH_WEST_LURE_SLOT_POS.x, NORTH_WEST_LURE_SLOT_POS.y, x, y);
        drawLureHelpText(NORTH_EAST_LURE_SLOT_POS.x, NORTH_EAST_LURE_SLOT_POS.y, x, y);
        drawLureHelpText(SOUTH_EAST_LURE_SLOT_POS.x, SOUTH_EAST_LURE_SLOT_POS.y, x, y);
        drawLureHelpText(SOUTH_WEST_LURE_SLOT_POS.x, SOUTH_WEST_LURE_SLOT_POS.y, x, y);

        GlStateManager.enableLighting();
    }
}
