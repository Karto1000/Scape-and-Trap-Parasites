package srparasites_traps.util;

import srparasites_traps.SRParasitesTraps;

public class Translation {
    public static String getTranslationKeyFor(String id) {
        return SRParasitesTraps.MOD_ID + "." + id;
    }
}
