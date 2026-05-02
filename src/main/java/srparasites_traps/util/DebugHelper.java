package srparasites_traps.util;

import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

public class DebugHelper {
    public static void dbp(String s) {
        if (!ForgeConfigHandler.common.DEBUG_MODE) return;
        SRParasitesTraps.LOGGER.info(s);
    }
}
