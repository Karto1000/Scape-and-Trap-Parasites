package srparasites_traps.features.sentry_turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class SentryTurretAlafhaBall extends SentryTurretSpineball {
    public SentryTurretAlafhaBall(World worldIn) {
        super(worldIn);
    }

    public SentryTurretAlafhaBall(
            World worldIn,
            SentryTurretEntity sentryTurretEntity
    ) {
        super(worldIn, sentryTurretEntity);
        this.setSize(0.3F, 0.3F);
    }

    @Nonnull
    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (this.world.isRemote) return;
        if (this.shootingEntity == null) return;

        SentryTurretEntity ste = (SentryTurretEntity) this.shootingEntity;
        if (ste.tileEntity == null) return;

        DamageSource damageSource = this.shootingEntity.isEntityAlive()
                ? DamageSource.causeThrownDamage(this, this.shootingEntity)
                : DamageSource.causeThrownDamage(this, this);

        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                this.getEntityBoundingBox().grow(ste.tileEntity.alafhaBallExplosionRadius)
        );

        for (EntityLivingBase entitylivingbase : entities) {
            if (entitylivingbase instanceof EntityPlayer) continue;
            if (entitylivingbase instanceof SentryTurretEntity) continue;
            if (entitylivingbase == result.entityHit) continue;

            entitylivingbase.attackEntityFrom(damageSource, (float) ste.tileEntity.alafhaBallExplosionDamage);
        }

        this.world.playSound(
                null,
                this.posX,
                this.posY,
                this.posZ,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.BLOCKS,
                4.0F,
                (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F
        );

        super.onImpact(result);
    }
}
