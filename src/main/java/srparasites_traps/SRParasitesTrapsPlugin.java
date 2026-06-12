package srparasites_traps;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class SRParasitesTrapsPlugin implements IFMLLoadingPlugin {

    public SRParasitesTrapsPlugin() {
        MixinBootstrap.init();
        FermiumRegistryAPI.enqueueMixin(true, "mixins.srparasites_traps.srpmixin.json");
        //False for Vanilla/Coremod mixins, true for regular mod mixins
        //--> Replaced by @MixinConfig.MixinToggle in ForgeConfigHandler. This way is still an option for more complicated conditions
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}