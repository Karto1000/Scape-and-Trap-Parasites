package srparasites_traps.features.barbed_wire;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ViralBarbedWire extends BarbedWireBlock {
    public static final String REGISTRY_NAME = "viral_barbed_wire";

    public ViralBarbedWire() {
        super(REGISTRY_NAME);
    }

    @Override
    public void onEntityCollision(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
        if (worldIn.isRemote) return;
        if (!(entityIn instanceof EntityLivingBase)) return;

        super.onEntityCollision(worldIn, pos, state, entityIn);

        ((EntityLivingBase) entityIn).addPotionEffect(
                new PotionEffect(
                        SRPPotions.VIRA_E,
                        ForgeConfigHandler.barbedWire.VIRAL_BARBED_WIRE_VIRAL_EFFECT_DURATION,
                        ForgeConfigHandler.barbedWire.VIRAL_BARBED_WIRE_VIRAL_AMPLIFIER
                )
        );
    }

    @Nonnull
    @Override
    public SoundType getSoundType(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity entity) {
        return ModSounds.BARBED_WIRE_ST;
    }
}
