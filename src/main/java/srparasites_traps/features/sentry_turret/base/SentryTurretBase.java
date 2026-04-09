package srparasites_traps.features.sentry_turret.base;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;

import javax.annotation.Nullable;
import java.util.Optional;

import static srparasites_traps.SRParasitesTraps.MOD_ID;
import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class SentryTurretBase extends Block {
    public SentryTurretBase() {
        super(Material.IRON, MapColor.IRON);

        setRegistryName(MOD_ID, "sentry_turret_base");
        setTranslationKey(getTranslationKeyFor("sentry_turret_base"));
        setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    public static SentryTurretEntity spawnTurret(World world, BlockPos pos) {
        SentryTurretEntity entity = new SentryTurretEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, pos);
        world.spawnEntity(entity);
        return entity;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new SentryTurretTileEntity();
    }

    // When the tile entity is placed, spawn a SentryTurretEntity ontop, when it is broken, kill the SentryTurretEntity
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!worldIn.isRemote) {
            SentryTurretEntity entity = spawnTurret(worldIn, pos);
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof SentryTurretTileEntity)
                ((SentryTurretTileEntity) tileEntity).setAssignedSentryTurret(entity);
        }

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof SentryTurretTileEntity) {
                Optional<SentryTurretEntity> entity = ((SentryTurretTileEntity) tileEntity).getAssignedSentryTurret();
                if (!entity.isPresent()) return;
                worldIn.removeEntity(entity.get());
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof SentryTurretTileEntity) {
            SentryTurretTileEntity sentryTurretTileEntity = (SentryTurretTileEntity) tileEntity;
            FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing);
            playerIn.sendMessage(new TextComponentString(String.format("(%d / %d) Biomass", sentryTurretTileEntity.getBiomassAmount(), sentryTurretTileEntity.getBiomassCapacity())));
        }

        return true;
    }
}
