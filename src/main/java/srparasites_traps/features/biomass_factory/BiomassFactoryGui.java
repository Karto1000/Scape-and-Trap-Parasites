package srparasites_traps.features.biomass_factory;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementFluidTank;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Translation;

import java.util.Collections;

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

        this.addElement(new ElementFluidTank(this, 100, 8, this.tileEntity.biomassStorage).setSize(16, 53).setEnabled(true));
    }

    @Override
    public void drawScreen(int x, int y, float partialTick) {
        super.drawScreen(x, y, partialTick);

        Slot fluidInputSlot = this.inventorySlots.getSlot(BiomassFactoryContainer.FLUID_FILL_SLOT);
        if (isMouseOverSlot(fluidInputSlot, x, y)) {
            this.drawHoveringText(
                    Collections.singletonList(I18n.format(Translation.getSlotDescriptionFor("biomass_factory_fluid_input"))),
                    x,
                    y
            );
        }
    }
}
