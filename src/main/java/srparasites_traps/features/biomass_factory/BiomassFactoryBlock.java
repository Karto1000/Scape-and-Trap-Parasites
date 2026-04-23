package srparasites_traps.features.biomass_factory;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

import static srparasites_traps.util.Translation.getTooltipFor;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BiomassFactoryBlock extends Block {
    public BiomassFactoryBlock() {
        super(Material.SPONGE, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, "biomass_factory");
        this.setTranslationKey(getTranslationKeyFor("biomass_factory"));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + getTooltipFor("item.biomass_factory"));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BiomassFactoryTileEntity();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        playerIn.openGui(SRParasitesTraps.instance, Constants.BIOMASS_FACTORY_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
