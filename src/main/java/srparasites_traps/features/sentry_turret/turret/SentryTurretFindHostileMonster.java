package srparasites_traps.features.sentry_turret.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;
import srparasites_traps.util.UpdateLimiter;

import java.util.List;

public class SentryTurretFindHostileMonster extends EntityAIBase {
    private final SentryTurretEntity sentry;
    private final World world;
    private final UpdateLimiter updateLimiter = new UpdateLimiter(20);

    public SentryTurretFindHostileMonster(SentryTurretEntity sentry, World world, double range) {
        this.sentry = sentry;
        this.world = world;
    }

    @Override
    public boolean shouldExecute() {
        return (this.sentry.getAttackTarget() == null || !this.sentry.getAttackTarget().isEntityAlive()) && this.sentry.getEntityState() != SentryTurretEntityState.EMERGING;
    }

    @Override
    public void startExecuting() {
        updateLimiter.allowUpdate();
    }

    @Override
    public void updateTask() {
        if (updateLimiter.tickDown()) return;

        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.sentry.getEntityBoundingBox().grow(this.sentry.attackRangeBlocks));

        for (EntityLivingBase target : list) {
            if (target instanceof IMob && target.isEntityAlive() && this.sentry.canEntityBeSeen(target) && this.sentry.getDistance(target) <= this.sentry.attackRangeBlocks) {
                this.sentry.setAttackTarget(target);
                this.sentry.setEntityState(SentryTurretEntityState.ATTACKING);
                break;
            }
        }

        updateLimiter.reset();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }
}
