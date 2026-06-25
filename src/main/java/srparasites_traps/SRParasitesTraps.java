package srparasites_traps;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import srparasites_traps.config.SRParasitesTrapsCreativeTab;
import srparasites_traps.proxy.CommonProxy;

@Mod(modid = SRParasitesTraps.MOD_ID, version = SRParasitesTraps.VERSION, name = SRParasitesTraps.NAME, dependencies = "required-after:fermiumbooter;required-after:cofhcore;required-after:srparasites;required-after:redstoneflux")
public class SRParasitesTraps {
    public static final String MOD_ID = "srparasites_traps";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Scape and Trap Parasites";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;
    public static final SRParasitesTrapsCreativeTab CREATIVE_TAB = new SRParasitesTrapsCreativeTab();

    @SidedProxy(clientSide = "srparasites_traps.proxy.ClientProxy", serverSide = "srparasites_traps.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Instance(MOD_ID)
    public static SRParasitesTraps instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SRParasitesTraps.PROXY.preInit();
        if (event.getSide() == Side.CLIENT) OBJLoader.INSTANCE.addDomain(MOD_ID);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        SRParasitesTraps.PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        SRParasitesTraps.PROXY.postInit();
        completedLoading = true;
    }
}