package srparasites_traps.features.tesla_coil;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.network.SRParasitesTrapsNetwork;
import srparasites_traps.network.SpawnLightningParticlePacket;
import srparasites_traps.registry.ModSounds;

import java.lang.reflect.Field;
import java.util.List;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ShockedPotion extends Potion {
    public static final String REGISTRY_NAME = "shocked";
    public final int range = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_ARC_RANGE;
    public final double chanceToTransmit = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_CHANCE_TO_ARC;
    public final double damage = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_ARC_DAMAGE;
    public final int jumpLimit = ForgeConfigHandler.teslaCoil.DEFAULT_SHOCKED_JUMP_LIMIT;

    public ShockedPotion() {
        super(true, 0x90D5FF);

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setPotionName("potion." + getTranslationKeyFor(REGISTRY_NAME));
    }

    @Override
    public boolean isReady(
            int duration,
            int amplifier
    ) {
        return duration == 5;
    }

    @Override
    public void performEffect(
            EntityLivingBase entityLivingBaseIn,
            int amplifier
    ) {
        World world = entityLivingBaseIn.getEntityWorld();

        if (amplifier - 1 > 0) {
            List<EntityLivingBase> entitiesInRange = world.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    entityLivingBaseIn.getEntityBoundingBox().grow(range)
            );

            int jumpCount = 0;
            for (EntityLivingBase entity : entitiesInRange) {
                if (jumpCount >= jumpLimit) break;

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

                if (world.rand.nextDouble() < (chanceToTransmit / 100) + (amplifier * 0.1)) {
                    entity.addPotionEffect(new PotionEffect(this, 5, amplifier - 1));

                    Vec3d origin = entityLivingBaseIn.getPositionVector().add(0, entityLivingBaseIn.getEyeHeight(), 0);
                    Vec3d target = entity.getPositionVector().add(0, entity.getEyeHeight(), 0);

                    SRParasitesTrapsNetwork.CHANNEL.sendToAllAround(
                            new SpawnLightningParticlePacket(origin, target, amplifier - 1),
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), origin.x, origin.y, origin.z, 32)
                    );

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
                    jumpCount++;
                }
            }
        }

        int hurtResistance = entityLivingBaseIn.hurtResistantTime;
        entityLivingBaseIn.hurtResistantTime = 0;
        entityLivingBaseIn.attackEntityFrom(DamageSource.GENERIC, (float) (damage * amplifier));
        entityLivingBaseIn.hurtResistantTime = hurtResistance;

        // Make parasites explode immediately
        if (entityLivingBaseIn instanceof EntityParasiteBase && !entityLivingBaseIn.isEntityAlive()) {
            EntityParasiteBase parasite = (EntityParasiteBase) entityLivingBaseIn;
            try {
                Field timeSinceIgnited = EntityParasiteBase.class.getDeclaredField("timeSinceIgnited");
                Field fuseTime = EntityParasiteBase.class.getDeclaredField("fuseTime");

                fuseTime.setAccessible(true);
                int fuseTimeValue = fuseTime.getInt(parasite);

                timeSinceIgnited.setAccessible(true);
                timeSinceIgnited.setInt(parasite, fuseTimeValue);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
    }
}
