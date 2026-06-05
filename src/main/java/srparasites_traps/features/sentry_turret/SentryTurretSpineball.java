package srparasites_traps.features.sentry_turret;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;

import java.util.ArrayList;
import java.util.Random;

public class SentryTurretSpineball extends EntityFireball {
    private final float damage = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_DAMAGE;
    private final int poisonDuration = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_POISON_DURATION;
    private final int poisonAmplifier = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_POISON_AMPLIFIER;
    private final int chanceToReduceResistance = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCE_CHANCE;
    private final int resistanceReductionAmount = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCTION_AMOUNT;

    public SentryTurretSpineball(World worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public SentryTurretSpineball(World worldIn, EntityLivingBase shootingEntity) {
        super(worldIn);
        this.shootingEntity = shootingEntity;
        this.setNoGravity(true);
    }

    @Override
    protected float getMotionFactor() {
        return 1;
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SLIME;
    }

    public boolean isBurning() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    private void meabyReduceResistanceOfParasite(EntityPMalleable parasite) {
        ArrayList<Integer> resistanceI = parasite.getResistanceI();
        ArrayList<String> resistanceS = parasite.getResistanceS();

        if (resistanceI.isEmpty() || resistanceS.isEmpty()) return;

        Random random = new Random();
        if (random.nextInt(100) < chanceToReduceResistance) {
            int randomResistance = random.nextInt(resistanceI.size());
            int newPoints = resistanceI.get(randomResistance) - resistanceReductionAmount;
            if (newPoints <= 0) {
                resistanceI.remove(randomResistance);
                resistanceS.remove(randomResistance);
            } else {
                resistanceI.set(randomResistance, newPoints);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) return;

        EntityLivingBase target = null;
        DamageSource damageSource = DamageSource.causeThrownDamage(this, this);

        // Entity is a parasite that can adapt
        if (result.entityHit instanceof EntityPMalleable) {
            meabyReduceResistanceOfParasite((EntityPMalleable) result.entityHit);
            target = ((EntityPMalleable) result.entityHit);
            // Entity is any living entity
        } else if (result.entityHit instanceof EntityLivingBase) {
            if (result.entityHit == this.shootingEntity) return;
            if (result.entityHit instanceof SentryTurretEntity) return;
            if (result.entityHit instanceof EntityPlayer) return;

            target = (EntityLivingBase) result.entityHit;
            // Entity is a parasite body part
        } else if (result.entityHit instanceof EntityBody) {
            // We want the spineballs to be able to remove limbs off of parasites
            result.entityHit.attackEntityFrom(damageSource, damage);
            meabyReduceResistanceOfParasite((EntityPMalleable) ((EntityBody) result.entityHit).getFather());
            return;
        } else if (result.entityHit instanceof EntityHitbox) {
            return;
        }

        if (target != null) {
            int hurtResistantTime = target.hurtResistantTime;
            target.hurtResistantTime = 0;
            target.attackEntityFrom(damageSource, damage);
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, this.poisonDuration, this.poisonAmplifier));
            target.hurtResistantTime = hurtResistantTime;
        }

        this.setDead();
    }
}
