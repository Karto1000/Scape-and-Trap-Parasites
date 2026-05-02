package srparasites_traps.util;

import net.minecraft.client.resources.I18n;
import srparasites_traps.SRParasitesTraps;

public class Translation {
    public static String getTranslationKeyFor(String id) {
        return SRParasitesTraps.MOD_ID + "." + id;
    }

    public static String getTooltipFor(String id) {
        return I18n.format("tooltip." + SRParasitesTraps.MOD_ID + "." + id);
    }

    public static String getTooltipFor(String id, Object... args) {
        return I18n.format("tooltip." + SRParasitesTraps.MOD_ID + "." + id, args);
    }

    public static String getServerStatusFor(String id) {
        return "status." + SRParasitesTraps.MOD_ID + "." + id;
    }
}
