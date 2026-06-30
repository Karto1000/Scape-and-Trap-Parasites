package srparasites_traps.util;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;

public class GuiHelper {
    private final static ResourceLocation BUTTON_TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/button_spritesheet.png");

    public static ElementButton createNewButton(
            GuiContainerCore gui,
            String name,
            int xPosition,
            int yPosition
    ) {
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

    public static class GuiConsole {
        public final int xPosition;
        public final int yPosition;
        public final int width;
        public final int height;
        public final double textScale;
        private final FontRenderer fontRenderer;

        private GuiConsole(
                FontRenderer fr,
                int xPosition,
                int yPosition,
                int width,
                int height,
                double textScale
        ) {
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            this.width = width;
            this.height = height;
            this.textScale = textScale;
            this.fontRenderer = fr;
        }

        public static GuiConsole init(
                FontRenderer fr,
                int xPosition,
                int yPosition,
                int width,
                int height,
                double textScale
        ) {
            return new GuiConsole(fr, xPosition, yPosition, width, height, textScale);
        }

        public void drawElements(
                String text,
                int color
        ) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(textScale, textScale, textScale);

            this.fontRenderer.drawSplitString(
                    text,
                    (int) (this.xPosition / textScale) + Constants.CONSOLE_TEXT_PADDING_PX,
                    (int) (this.yPosition / textScale) + Constants.CONSOLE_TEXT_PADDING_PX,
                    (int) ((this.width - Constants.CONSOLE_TEXT_PADDING_PX * 2) / textScale),
                    color
            );

            GlStateManager.popMatrix();
        }
    }
}
