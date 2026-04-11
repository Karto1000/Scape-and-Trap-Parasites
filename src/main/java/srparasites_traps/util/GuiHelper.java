package srparasites_traps.util;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;

public class GuiHelper {
    private final static ResourceLocation BUTTON_TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/button_spritesheet.png");

    public static ElementButton createNewButton(GuiContainerCore gui, String name, int xPosition, int yPosition) {
        return new ElementButton(
                gui,
                xPosition,
                yPosition,
                name,
                Constants.BUTTON_SPRITESHEET_X,
                Constants.BUTTON_SPRITESHEET_Y,
                Constants.BUTTON_SPRITESHEET_HOVER_X,
                Constants.BUTTON_SPRITESHEET_HOVER_Y,
                Constants.BUTTON_SPRITESHEET_DISABLED_X,
                Constants.BUTTON_SPRITESHEET_DISABLED_Y,
                Constants.BUTTON_SPRITESHEET_BUTTON_WIDTH,
                Constants.BUTTON_SPRITESHEET_BUTTON_HEIGHT,
                BUTTON_TEXTURE.toString()
        );
    }
}
