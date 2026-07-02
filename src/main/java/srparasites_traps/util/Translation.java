package srparasites_traps.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import srparasites_traps.SRParasitesTraps;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Translation {
    public static String getTranslationKeyFor(String id) {
        return SRParasitesTraps.MOD_ID + "." + id;
    }

    @SideOnly(Side.CLIENT)
    public static String getTooltipFor(String id) {
        return I18n.format("tooltip." + SRParasitesTraps.MOD_ID + "." + id);
    }

    @SideOnly(Side.CLIENT)
    public static String getTooltipFor(
            String id,
            Object... args
    ) {
        return I18n.format("tooltip." + SRParasitesTraps.MOD_ID + "." + id, args);
    }

    @SideOnly(Side.CLIENT)
    public static void addMultilineTooltip(
            List<String> tooltip,
            String id,
            Object... args
    ) {
        String text = getTooltipFor(id, args);
        List<String> lines = Arrays.stream(text.split("\n")).map(s -> TextFormatting.WHITE + s).collect(Collectors.toList());
        tooltip.addAll(lines);
    }

    // Can't pass args to server-side translation
    public static String getServerStatusFor(String id) {
        return "status." + SRParasitesTraps.MOD_ID + "." + id;
    }

    @SideOnly(Side.CLIENT)
    public static String getClientStatusFor(String id, Object... args) {
        return I18n.format("status." + SRParasitesTraps.MOD_ID + "." + id, args);
    }

    public static String getSlotDescriptionFor(String id) {
        return "slot." + SRParasitesTraps.MOD_ID + "." + id;
    }

    @SideOnly(Side.CLIENT)
    public static String getDamageSourceFor(
            String id,
            Object... args
    ) {
        return I18n.format("damage." + SRParasitesTraps.MOD_ID + "." + id, args);
    }
}
