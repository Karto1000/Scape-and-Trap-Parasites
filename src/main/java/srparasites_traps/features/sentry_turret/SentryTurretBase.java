package srparasites_traps.features.sentry_turret;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import static srparasites_traps.SRParasitesTraps.MOD_ID;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class SentryTurretBase extends Block {
    public SentryTurretBase() {
        super(Material.IRON, MapColor.IRON);

        setRegistryName(MOD_ID, "sentry_turret_base");
        setTranslationKey(getTranslationKeyFor("sentry_turret_base"));
        setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }
}
