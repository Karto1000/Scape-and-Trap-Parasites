package srparasites_traps.util;

import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import net.minecraftforge.fluids.Fluid;

public class Constants {
    public static final String SRPARASITES_MOD_ID = "srparasites";
    public static final int SENTRY_TURRET_GUI_ID = 0;
    public static final int RELOCATOR_GUI_ID = 1;
    public static final int BIOMASS_FACTORY_GUI_ID = 2;
    public static final int TESLA_COIL_GUI_ID = 3;
    public static final int TARGETING_AUGMENT_GUI_ID = 4;
    public static final int INFESTED_BEACON_GUI_ID = 5;
    public static final Fluid TURRET_FUEL = SRPFluids.DEADBLOOD_FLUID;
    public static final int TPS_LIMIT = 20;
    public static final int BUTTON_SPRITESHEET_BUTTON_WIDTH = 22;
    public static final int BUTTON_SPRITESHEET_BUTTON_HEIGHT = 22;
    public static final int BUTTON_SPRITESHEET_X = 0;
    public static final int BUTTON_SPRITESHEET_Y = 0;
    public static final int BUTTON_SPRITESHEET_DISABLED_X = BUTTON_SPRITESHEET_BUTTON_WIDTH;
    public static final int BUTTON_SPRITESHEET_DISABLED_Y = 0;
    public static final int BUTTON_SPRITESHEET_HOVER_X = BUTTON_SPRITESHEET_BUTTON_WIDTH * 2;
    public static final int BUTTON_SPRITESHEET_HOVER_Y = 0;
    public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    public final static int CONSOLE_TEXT_PADDING_PX = 2;
    public final static int INVENTORY_SLOT_WIDTH_PX = 18;
    public final static int INVENTORY_SLOT_HEIGHT_PX = 18;
    public final static int FIRST_FREE_SLOT = 4 * 9;
}
