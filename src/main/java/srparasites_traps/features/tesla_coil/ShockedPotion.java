package srparasites_traps.features.tesla_coil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.registry.ModSounds;

import java.util.List;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ShockedPotion extends Potion {
    public static final String REGISTRY_NAME = "shocked";
    public static final int SEARCH_RANGE_BLOCKS = 4;
    public static final double DEFAULT_CHANCE_TO_APPLY = 0.5;

    public ShockedPotion() {
        super(true, 0x90D5FF);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setPotionName("potion." + getTranslationKeyFor(REGISTRY_NAME));
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration == 5;
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        World world = entityLivingBaseIn.getEntityWorld();

        List<EntityLivingBase> entitiesInRange = world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                entityLivingBaseIn.getEntityBoundingBox().grow(SEARCH_RANGE_BLOCKS)
        );

        for (EntityLivingBase entity : entitiesInRange) {
            if (entity == entityLivingBaseIn) continue;
            if (!entity.isEntityAlive()) continue;
            if (entity.getActivePotionEffect(this) != null) continue;
            if (entity instanceof EntityPlayer) if (((EntityPlayer) entity).isCreative()) continue;

            RayTraceResult rayTraceResult = world.rayTraceBlocks(
                    entityLivingBaseIn.getPositionVector(),
                    entity.getPositionVector(),
                    false,
                    true,
                    false
            );

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) continue;

            if (world.rand.nextDouble() < DEFAULT_CHANCE_TO_APPLY + (amplifier * 0.1)) {
                entity.addPotionEffect(new PotionEffect(this, 5, amplifier - 1));

                LightningParticle lightningParticle = new LightningParticle(
                        world,
                        entityLivingBaseIn.getPositionVector().add(0, entityLivingBaseIn.getEyeHeight(), 0),
                        entity.getPositionVector().add(0, entity.getEyeHeight(), 0),
                        20
                );
                Minecraft.getMinecraft().effectRenderer.addEffect(lightningParticle);
                world.playSound(
                        null,
                        entityLivingBaseIn.getPositionVector().x,
                        entityLivingBaseIn.getPositionVector().y,
                        entityLivingBaseIn.getPositionVector().z,
                        ModSounds.ELECTRIC_ARC,
                        SoundCategory.NEUTRAL,
                        0.25F,
                        1.0F
                );
            }
        }

        entityLivingBaseIn.attackEntityFrom(DamageSource.GENERIC, amplifier * 2);
    }
}
