package srparasites_traps.features.sentry_turret;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SentryTurretAI extends EntityAIBase {
    private final SentryTurretEntity sentryTurret;
    private final World world;

    public SentryTurretAI(SentryTurretEntity sentryTurret, World world) {
        this.sentryTurret = sentryTurret;
        this.world = world;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
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
    public void updateTask() {
        if (this.sentryTurret.getAttackTarget() == null) return;

        EntityLivingBase target = this.sentryTurret.getAttackTarget();
        this.sentryTurret.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        if (this.sentryTurret.attackCooldown > 0) {
            this.sentryTurret.attackCooldown--;
            return;
        }

        Vec3d shootPosition = this.sentryTurret.getPositionVector().add(0, this.sentryTurret.getEyeHeight(), 0);
        Vec3d hitPosition = target.getPositionVector().add(new Vec3d(0, target.width / 2, 0));

        Vec3d direction = hitPosition.subtract(shootPosition).normalize();
        SentryTurretSpineball projectile = new SentryTurretSpineball(this.world, this.sentryTurret);
        projectile.setPosition(this.sentryTurret.posX + direction.x, this.sentryTurret.posY + this.sentryTurret.getEyeHeight(), this.sentryTurret.posZ + direction.z);
        projectile.setVelocity(direction.x, direction.y, direction.z);
        world.spawnEntity(projectile);
        this.sentryTurret.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);

        this.sentryTurret.attackCooldown = this.sentryTurret.attackDelay;
    }
}
