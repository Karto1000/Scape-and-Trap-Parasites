package srparasites_traps.features.tesla_coil;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class TeslaCoilBlock extends Block {
    public static final String REGISTRY_NAME = "tesla_coil";

    public TeslaCoilBlock() {
        super(Material.IRON, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);

        if (ForgeConfigHandler.teslaCoil.ENABLE_TESLA_COIL) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TeslaCoilTileEntity();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item." + REGISTRY_NAME));
    }
}
