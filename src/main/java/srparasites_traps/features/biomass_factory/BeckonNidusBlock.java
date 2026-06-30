package srparasites_traps.features.biomass_factory;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class BeckonNidusBlock extends Block {
    public static final String REGISTRY_NAME = "beckon_nidus";

    public BeckonNidusBlock() {
        super(Material.SPONGE);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
        this.setSoundType(SRPSoundTypes.FLESH);
        this.setDefaultState(this.blockState.getBaseState());
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
        return new BeckonNidusTileEntity();
    }
}
