package srparasites_traps.features.infested_beacon;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class InfestedBeaconBlock extends Block {
    public static final String REGISTRY_NAME = "infested_beacon";

    public InfestedBeaconBlock() {
        super(Material.IRON, MapColor.GRAY);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);

        if (ForgeConfigHandler.infestedBeacon.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(
            @Nonnull World world,
            @Nonnull IBlockState state
    ) {
        return new InfestedBeaconTileEntity();
    }

    @Override
    public boolean onBlockActivated(
            @Nonnull World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull IBlockState state,
            @Nonnull EntityPlayer playerIn,
            @Nonnull EnumHand hand,
            @Nonnull EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ
    ) {
        playerIn.openGui(SRParasitesTraps.instance, Constants.INFESTED_BEACON_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public void addInformation(
            @Nonnull ItemStack stack,
            @Nullable World worldIn,
            @Nonnull List<String> tooltip,
            @Nonnull ITooltipFlag flagIn
    ) {
        Translation.addMultilineTooltip(tooltip, "item." + REGISTRY_NAME);
    }
}
