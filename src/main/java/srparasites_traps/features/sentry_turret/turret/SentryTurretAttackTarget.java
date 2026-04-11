package srparasites_traps.features.sentry_turret.turret;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import srparasites_traps.features.sentry_turret.base.SentryTurretBaseTileEntity;

public class SentryTurretAttackTarget extends EntityAIBase {
    private final SentryTurretEntity sentryTurret;
    private final World world;
    private Vec3d lastTargetPosition;

    public SentryTurretAttackTarget(SentryTurretEntity sentryTurret, World world) {
        this.sentryTurret = sentryTurret;
        this.world = world;
    }

    @Override
    public void startExecuting() {
        int elapsedTicks = (int) (this.world.getTotalWorldTime() - this.sentryTurret.getTicksWhenTargetLost());
        if (elapsedTicks <= this.sentryTurret.currentAttackCooldown)
            this.sentryTurret.currentAttackCooldown -= elapsedTicks;
        else this.sentryTurret.currentAttackCooldown = this.sentryTurret.attackDelay;
        this.sentryTurret.setTicksWhenTargetLost(0);
    }

    @Override
    public boolean shouldExecute() {
        return this.sentryTurret.getAttackTarget() != null && this.sentryTurret.getEntityState() != SentryTurretEntityState.EMERGING;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.sentryTurret.getAttackTarget() == null) return false;
        EntityLivingBase target = this.sentryTurret.getAttackTarget();
        return (this.sentryTurret.canEntityBeSeen(target) && target.isEntityAlive() && this.sentryTurret.getDistance(target) <= this.sentryTurret.attackRangeBlocks);
    }

    @Override
    public void resetTask() {
        this.sentryTurret.setTicksWhenTargetLost(world.getTotalWorldTime());
        this.sentryTurret.setAttackTarget(null);
        this.sentryTurret.setEntityState(SentryTurretEntityState.IDLE);
    }

    private void shootSpineball(Vec3d direction, SentryTurretBaseTileEntity sentryTurret) {
        SentryTurretSpineball projectile = new SentryTurretSpineball(this.world, this.sentryTurret);
        projectile.setPosition(this.sentryTurret.posX + direction.x, this.sentryTurret.posY + this.sentryTurret.getEyeHeight(), this.sentryTurret.posZ + direction.z);
        projectile.setVelocity(direction.x, direction.y, direction.z);
        world.spawnEntity(projectile);
        this.sentryTurret.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
        sentryTurret.consumeBiomass(sentryTurret.biomassPerShot);
        sentryTurret.consumeEnergy(sentryTurret.energyPerShot);
    }

    @Override
    public void updateTask() {
        if (this.sentryTurret.getAttackTarget() == null) return;
        EntityLivingBase target = this.sentryTurret.getAttackTarget();
        this.sentryTurret.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        TileEntity tileEntity = this.world.getTileEntity(this.sentryTurret.baseBlockPosition);
        SentryTurretBaseTileEntity sentryTurretBaseTileEntity;
        if (tileEntity instanceof SentryTurretBaseTileEntity) {
            sentryTurretBaseTileEntity = (SentryTurretBaseTileEntity) tileEntity;
            if (!sentryTurretBaseTileEntity.hasEnoughBiomassToShoot()) return;
            if (!sentryTurretBaseTileEntity.hasEnoughEnergyToShoot()) return;
        } else {
            return;
        }

        if (this.sentryTurret.currentAttackCooldown > 0) {
            this.sentryTurret.currentAttackCooldown--;

            if (this.sentryTurret.currentAttackCooldown == 10) {
                this.sentryTurret.playSound(SRPSounds.UNVO_SHOOTING, 2.0F, 1.0F);
            }

            this.lastTargetPosition = target.getPositionVector();
            return;
        }


        Vec3d shootPosition = this.sentryTurret.getPositionVector().add(0, this.sentryTurret.getEyeHeight(), 0);
        Vec3d hitPosition = target.getPositionVector()
                .add(new Vec3d(0, target.height / 2, 0));

        Vec3d shooterToTargetDelta = hitPosition.subtract(shootPosition);
        Vec3d unCorrectedVelocity = shooterToTargetDelta.normalize();
        Vec3d targetDistanceTraveledSinceLastTick = target.getPositionVector().subtract(this.lastTargetPosition);

        double distanceToTarget = shooterToTargetDelta.length();
        int ticksUntilHit = (int) (distanceToTarget / unCorrectedVelocity.length());
        Vec3d actualVelocity = shooterToTargetDelta.add(targetDistanceTraveledSinceLastTick.scale(ticksUntilHit)).normalize();
        this.shootSpineball(actualVelocity, sentryTurretBaseTileEntity);

        this.sentryTurret.currentAttackCooldown = this.sentryTurret.attackDelay;
    }
}
