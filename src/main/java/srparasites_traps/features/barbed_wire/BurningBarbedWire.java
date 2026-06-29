package srparasites_traps.features.barbed_wire;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BurningBarbedWire extends BarbedWireBlock {
    public static final String REGISTRY_NAME = "burning_barbed_wire";

    public BurningBarbedWire() {
        super(REGISTRY_NAME);
    }

    @Override
    public void onEntityCollision(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
        if (worldIn.isRemote) return;
        if (!(entityIn instanceof EntityLivingBase)) return;

        super.onEntityCollision(worldIn, pos, state, entityIn);
        entityIn.setFire(ForgeConfigHandler.barbedWire.BURNING_BARBED_WIRE_BURN_DURATION);
    }

    @Override
    public void randomDisplayTick(@Nonnull IBlockState stateIn, World worldIn, BlockPos pos, @Nonnull Random rand) {
        if (worldIn.rand.nextInt(20) != 0) return;

        worldIn.spawnParticle(
                EnumParticleTypes.FLAME,
                pos.getX() + Math.random(),
                pos.getY() + Math.random(),
                pos.getZ() + Math.random(),
                0, 0, 0
        );
    }

    @Nonnull
    @Override
    public SoundType getSoundType(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity entity) {
        return ModSounds.BURNING_BARBED_WIRE_ST;
    }
}
