package srparasites_traps.features.obsidian_blocks;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModItems;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public abstract class ObsidianSlabBlock extends BlockSlab {
    public static final String REGISTRY_NAME = "obsidian_slab";
    public static final String REGISTRY_NAME_DOUBLE = REGISTRY_NAME + "_double";

    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public ObsidianSlabBlock(String registryName) {
        super(Material.ROCK, MapColor.BLACK);

        this.setRegistryName(SRParasitesTraps.MOD_ID, registryName);
        this.setTranslationKey(getTranslationKeyFor(registryName));
        this.setHardness(50);
        this.setResistance(2000);
        this.setHarvestLevel("pickaxe", 3);
        this.useNeighborBrightness = !this.isDouble();

        IBlockState bs = this.blockState.getBaseState();
        if (!this.isDouble()) bs = bs.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        this.setDefaultState(bs.withProperty(VARIANT, Variant.DEFAULT));

        if (ForgeConfigHandler.obsidianBlocks.ENABLE)
            this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (this.isDouble()) drops.add(new ItemStack(ModItems.OBSIDIAN_SLAB_ITEM, 2));
        else drops.add(new ItemStack(ModItems.OBSIDIAN_SLAB_ITEM, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        if (this.isDouble()) return new BlockStateContainer(this, VARIANT);
        return new BlockStateContainer(this, VARIANT, HALF);
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState bs = this.getDefaultState().withProperty(VARIANT, Variant.DEFAULT);
        if (!(meta == 0b0001))
            bs = bs.withProperty(HALF, meta == 0b0010 ? BlockSlab.EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        return bs;
    }

    public int getMetaFromState(IBlockState state) {
        if (this.isDouble()) return 0b0001;
        EnumBlockHalf half = state.getValue(HALF);
        return half == BlockSlab.EnumBlockHalf.BOTTOM ? 0b0010 : 0b0011;
    }

    @Override
    public String getTranslationKey(int meta) {
        return super.getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }

    public enum Variant implements IStringSerializable {
        DEFAULT;

        @Override
        public String getName() {
            return "default";
        }
    }

    public static class Half extends ObsidianSlabBlock {
        public Half() {
            super(REGISTRY_NAME);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static class Double extends ObsidianSlabBlock {
        public Double() {
            super(REGISTRY_NAME_DOUBLE);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
