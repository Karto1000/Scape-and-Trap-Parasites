package srparasites_traps.features.sentry_turret.turret;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import srparasites_traps.features.sentry_turret.base.SentryTurretTileEntity;

public class SentryTurretAI extends EntityAIBase {
    private final SentryTurretEntity sentryTurret;
    private final World world;

    public SentryTurretAI(SentryTurretEntity sentryTurret, World world) {
        this.sentryTurret = sentryTurret;
        this.world = world;
    }

    @Override
    public void startExecuting() {
        int elapsedTicks = (int) (this.world.getTotalWorldTime() - this.sentryTurret.ticksWhenTargetLost);
        if (elapsedTicks <= this.sentryTurret.currentAttackCooldown)
            this.sentryTurret.currentAttackCooldown -= elapsedTicks;
        else this.sentryTurret.currentAttackCooldown = this.sentryTurret.attackDelay;
        this.sentryTurret.ticksWhenTargetLost = 0;
    }

    @Override
    public boolean shouldExecute() {
        return this.sentryTurret.getAttackTarget() != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void resetTask() {
        this.sentryTurret.ticksWhenTargetLost = world.getTotalWorldTime();
    }

    private void shootSpineball(Vec3d direction, SentryTurretTileEntity sentryTurret) {
        SentryTurretSpineball projectile = new SentryTurretSpineball(this.world, this.sentryTurret);
        projectile.setPosition(this.sentryTurret.posX + direction.x, this.sentryTurret.posY + this.sentryTurret.getEyeHeight(), this.sentryTurret.posZ + direction.z);
        projectile.setVelocity(direction.x, direction.y, direction.z);
        world.spawnEntity(projectile);
        this.sentryTurret.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
        sentryTurret.consumeBiomass(this.sentryTurret.biomassPerShot);
    }

    @Override
    public void updateTask() {
        if (this.sentryTurret.getAttackTarget() == null) return;
        EntityLivingBase target = this.sentryTurret.getAttackTarget();
        this.sentryTurret.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        TileEntity tileEntity = this.world.getTileEntity(this.sentryTurret.baseBlockPosition);
        SentryTurretTileEntity sentryTurretTileEntity;
        if (tileEntity instanceof SentryTurretTileEntity) {
            sentryTurretTileEntity = (SentryTurretTileEntity) tileEntity;
            if (!sentryTurretTileEntity.hasMoreBiomassThan(this.sentryTurret.biomassPerShot)) return;
        } else {
            return;
        }

        if (this.sentryTurret.currentAttackCooldown > 0) {
            this.sentryTurret.currentAttackCooldown--;

            if (this.sentryTurret.currentAttackCooldown == 10) {
                this.sentryTurret.playSound(SRPSounds.UNVO_SHOOTING, 2.0F, 1.0F);
            }

            return;
        }

        Vec3d shootPosition = this.sentryTurret.getPositionVector().add(0, this.sentryTurret.getEyeHeight(), 0);
        Vec3d hitPosition = target.getPositionVector()
                .add(new Vec3d(0, target.width / 2, 0))
                .add(new Vec3d(target.motionX, target.motionY, target.motionZ).scale(2));

        Vec3d direction = hitPosition.subtract(shootPosition).normalize();
        this.shootSpineball(direction, sentryTurretTileEntity);

        this.sentryTurret.currentAttackCooldown = this.sentryTurret.attackDelay;
    }
}
