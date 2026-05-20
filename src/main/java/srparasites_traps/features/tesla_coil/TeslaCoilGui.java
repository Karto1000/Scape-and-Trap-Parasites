package srparasites_traps.features.tesla_coil;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.tab.TabRedstoneControl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;

public class TeslaCoilGui extends GuiContainerCore {
    private final TeslaCoilTileEntity tileEntity;
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/tesla_coil_gui.png");
    private final static int ENERGY_X_POSITION_PX = 80;
    private final static int ENERGY_Y_POSITION_PX = 16;
    private final static int TAB_X_OFFSET_PX = -59;

    public TeslaCoilGui(EntityPlayer player, TeslaCoilTileEntity tileEntity) {
        super(new TeslaCoilContainer(player, tileEntity), TEXTURE);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.addElement(new ElementEnergyStored(this, ENERGY_X_POSITION_PX, ENERGY_Y_POSITION_PX, this.tileEntity.energyStorage.getRfEnergyStorage()));
        this.addTab(new TabRedstoneControl(this, this.tileEntity).setOffsets(TAB_X_OFFSET_PX, 0));
    }
}
