package srparasites_traps.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.biomass_factory.BiomassFactoryContainer;
import srparasites_traps.features.biomass_factory.BiomassFactoryGui;
import srparasites_traps.features.biomass_factory.BiomassFactoryTileEntity;
import srparasites_traps.features.relocator.RelocatorContainer;
import srparasites_traps.features.relocator.RelocatorGui;
import srparasites_traps.features.relocator.RelocatorTileEntity;
import srparasites_traps.features.sentry_turret.SentryTurretContainer;
import srparasites_traps.features.sentry_turret.SentryTurretGui;
import srparasites_traps.features.sentry_turret.SentryTurretTileEntity;
import srparasites_traps.features.tesla_coil.TeslaCoilContainer;
import srparasites_traps.features.tesla_coil.TeslaCoilGui;
import srparasites_traps.features.tesla_coil.TeslaCoilTileEntity;
import srparasites_traps.util.Constants;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    /**
     * Returns a Server side Container to be displayed to the user.
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Constants.SENTRY_TURRET_GUI_ID) {
            return new SentryTurretContainer(player, (SentryTurretTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        if (ID == Constants.RELOCATOR_GUI_ID) {
            return new RelocatorContainer(player, (RelocatorTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        if (ID == Constants.BIOMASS_FACTORY_GUI_ID) {
            return new BiomassFactoryContainer(player, (BiomassFactoryTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        if (ID == Constants.TESLA_COIL_GUI_ID) {
            return new TeslaCoilContainer(player, (TeslaCoilTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        return null;
    }

    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Constants.SENTRY_TURRET_GUI_ID) {
            return new SentryTurretGui(player, (SentryTurretTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        if (ID == Constants.RELOCATOR_GUI_ID) {
            return new RelocatorGui(player, (RelocatorTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        if (ID == Constants.BIOMASS_FACTORY_GUI_ID) {
            return new BiomassFactoryGui(player, (BiomassFactoryTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        if (ID == Constants.TESLA_COIL_GUI_ID) {
            return new TeslaCoilGui(player, (TeslaCoilTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        return null;
    }

    public static void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(SRParasitesTraps.instance, new GuiHandler());
    }
}
