package srparasites_traps.features.sentry_turret.turret;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SentryTurretSpineball extends EntityFireball {
    private final float damage = 25;
    private final int poisonDuration = 100;
    private final int poisonAmplifier = 1;

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

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) return;

        EntityLivingBase target = null;
        DamageSource damageSource = DamageSource.causeThrownDamage(this, this);
        if (result.entityHit instanceof EntityLivingBase) {
            if (result.entityHit == this.shootingEntity) return;
            if (result.entityHit instanceof SentryTurretEntity) return;
            if (result.entityHit instanceof EntityPlayer) return;

            target = (EntityLivingBase) result.entityHit;
        } else if (result.entityHit instanceof EntityBody) {
            // We want the spineballs to be able to remove limbs off of parasites
            result.entityHit.attackEntityFrom(damageSource, damage);
            return;
        } else if (result.entityHit instanceof EntityHitbox) {
            target = ((EntityHitbox) result.entityHit).getParent();
        }

        if (target != null) {
            target.attackEntityFrom(damageSource, damage);
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, this.poisonDuration, this.poisonAmplifier));
        }

        this.setDead();
    }
}
