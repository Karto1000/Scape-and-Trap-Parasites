package srparasites_traps.features.biomass_factory;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementFluidTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;

public class BiomassFactoryGui extends GuiContainerCore {
    private final EntityPlayer player;
    private final BiomassFactoryTileEntity tileEntity;
    private final static ResourceLocation TEXTURE = new ResourceLocation(SRParasitesTraps.MOD_ID, "textures/gui/biomass_factory.png");

    public BiomassFactoryGui(EntityPlayer player, BiomassFactoryTileEntity tileEntity) {
        super(new BiomassFactoryContainer(player, tileEntity), TEXTURE);

        this.player = player;
        this.tileEntity = tileEntity;

    }

    @Override
    public void initGui() {
        super.initGui();

        this.addElement(new ElementFluidTank(this, 8, 16, this.tileEntity.biomassStorage).setSize(16, 53).setEnabled(true));
    }
}
