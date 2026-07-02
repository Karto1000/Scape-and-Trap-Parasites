package srparasites_traps.features.proximity_sensor;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ProximitySensorBlock extends Block {
    public static final String REGISTRY_NAME = "proximity_sensor";
    public static final PropertyBool active = PropertyBool.create("active");

    public ProximitySensorBlock() {
        super(Material.IRON, MapColor.IRON);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(1200);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(active, false));

        if (ForgeConfigHandler.proximitySensor.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(active) ? 1 : 0;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(active, meta == 1);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, active);
    }

    @Override
    public void randomDisplayTick(
            IBlockState stateIn,
            @Nonnull World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull Random rand
    ) {
        if (!stateIn.getValue(active)) return;

        worldIn.spawnParticle(
                EnumParticleTypes.REDSTONE,
                pos.getX() + Math.random() * 1.5,
                pos.getY() + Math.random() * 1.5,
                pos.getZ() + Math.random() * 1.5,
                0, 0, 0
        );
    }

    @Override
    public boolean canProvidePower(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(
            @Nonnull IBlockState blockState,
            IBlockAccess blockAccess,
            @Nonnull BlockPos pos,
            @Nonnull EnumFacing side
    ) {
        TileEntity tileEntity = blockAccess.getTileEntity(pos);

        if (tileEntity == null) return 0;
        if (!(tileEntity instanceof ProximitySensorTileEntity)) return 0;

        ProximitySensorState state = ((ProximitySensorTileEntity) tileEntity).getState();
        return state == ProximitySensorState.ACTIVE ? 15 : 0;
    }

    @Override
    public void breakBlock(
            World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull IBlockState state
    ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof ProximitySensorTileEntity) {
            ProximitySensorTileEntity pste = (ProximitySensorTileEntity) tileEntity;
            Optional<ItemStack> areaMarker = pste.getAreaMarker();
            areaMarker.ifPresent(itemStack -> worldIn.spawnEntity(new EntityItem(
                    worldIn,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    itemStack
            )));
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(
            @Nonnull World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull IBlockState state,
            EntityPlayer playerIn,
            @Nonnull EnumHand hand,
            @Nonnull EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ
    ) {
        playerIn.openGui(
                SRParasitesTraps.instance,
                Constants.PROXIMITY_SENSOR_GUI_ID,
                worldIn,
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        return true;
    }

    @Override
    public int getStrongPower(
            @Nonnull IBlockState blockState,
            @Nonnull IBlockAccess blockAccess,
            @Nonnull BlockPos pos,
            @Nonnull EnumFacing side
    ) {
        return getWeakPower(blockState, blockAccess, pos, side);
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
        return new ProximitySensorTileEntity();
    }

    @Override
    public void addInformation(
            @Nonnull ItemStack stack,
            @Nullable World worldIn,
            List<String> tooltip,
            @Nonnull ITooltipFlag flagIn
    ) {
        Translation.addMultilineTooltip(tooltip, "item." + REGISTRY_NAME);
    }
}
