package srparasites_traps.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import srparasites_traps.features.sentry_turret.base.SentryTurretBaseContainer;
import srparasites_traps.features.sentry_turret.base.SentryTurretBaseGui;
import srparasites_traps.features.sentry_turret.base.SentryTurretBaseTileEntity;
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
            return new SentryTurretBaseContainer(player.inventory, (SentryTurretBaseTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
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
            return new SentryTurretBaseGui(player.inventory, (SentryTurretBaseTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }

        return null;
    }
}
