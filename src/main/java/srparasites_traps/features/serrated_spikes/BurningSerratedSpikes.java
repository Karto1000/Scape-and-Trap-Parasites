package srparasites_traps.features.serrated_spikes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BurningSerratedSpikes extends SerratedSpikesBlock {
    public static final String REGISTRY_NAME = "burning_serrated_spikes";

    public BurningSerratedSpikes() {
        super(REGISTRY_NAME);
    }

    @Override
    protected void damageEntity(Entity entity, float damage) {
        super.damageEntity(entity, damage);
        entity.setFire(ForgeConfigHandler.serratedSpikes.DEFAULT_FLAME_DURATION);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Translation.addMultilineTooltip(tooltip, "item." + REGISTRY_NAME);
    }

    @Override
    public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, Random rand) {
        if (rand.nextInt(10) == 0) {
            worldIn.spawnParticle(
                    EnumParticleTypes.FLAME,
                    pos.getX() + rand.nextFloat(),
                    pos.getY() + rand.nextFloat(),
                    pos.getZ() + rand.nextFloat(),
                    0,
                    0,
                    0
            );
        }
    }
}
