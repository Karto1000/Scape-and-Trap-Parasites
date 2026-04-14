package srparasites_traps.features.relocator;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.features.relocation_marker.RelocationMarkerItem;

import javax.annotation.Nullable;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class RelocatorBlock extends Block {
    public RelocatorBlock() {
        super(Material.IRON, MapColor.IRON);
        setRegistryName("relocator");
        setTranslationKey(getTranslationKeyFor("relocator"));
        setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new RelocatorTileEntity();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);

        Item item = playerIn.getHeldItemMainhand().getItem();

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof RelocatorTileEntity)) return false;
        RelocatorTileEntity relocatorTileEntity = (RelocatorTileEntity) tileEntity;

        if (item instanceof RelocationMarkerItem) {
            ItemStack heldItem = playerIn.getHeldItemMainhand();
            if (!RelocationMarkerItem.hasBoundPositions(heldItem)) return false;

            ItemStack relocationMarkerItem = relocatorTileEntity.getRelocationMarker().copy();
            relocatorTileEntity.setRelocationMarker(heldItem.copy());
            relocatorTileEntity.markDirty();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            heldItem.shrink(1);

            if (relocationMarkerItem.isEmpty()) return true;

            // Swap the held item with the old one
            playerIn.setHeldItem(hand, relocationMarkerItem);
        } else if (item instanceof ItemAir) {
            ItemStack relocationMarker = relocatorTileEntity.getRelocationMarker();
            if (relocationMarker.isEmpty()) return false;

            playerIn.setHeldItem(hand, relocationMarker.copy());
            relocatorTileEntity.setRelocationMarker(ItemStack.EMPTY);
            relocatorTileEntity.markDirty();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
        }

        return true;
    }
}
