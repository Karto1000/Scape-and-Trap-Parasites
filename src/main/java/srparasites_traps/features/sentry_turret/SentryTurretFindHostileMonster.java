package srparasites_traps.features.sentry_turret;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import srparasites_traps.features.augments.TargetingAugment;
import srparasites_traps.util.UpdateLimiter;

import java.util.List;

public class SentryTurretFindHostileMonster extends EntityAIBase {
    private final SentryTurretEntity sentry;
    private final World world;
    private final UpdateLimiter updateLimiter = new UpdateLimiter(5);

    public SentryTurretFindHostileMonster(
            SentryTurretEntity sentry,
            World world
    ) {
        this.sentry = sentry;
        this.world = world;
    }

    private boolean isEntityValid(Entity entity) {
        boolean prerequisites = entity.isEntityAlive() &&
                !(entity instanceof SentryTurretEntity) &&
                this.sentry.canEntityBeSeen(entity) &&
                this.sentry.getDistance(entity) <= this.sentry.tileEntity.attackRange;

        boolean hasTargetingAugment = false;
        for (ItemStack augment : sentry.tileEntity.getAugmentSlots()) {
            if (!(augment.getItem() instanceof TargetingAugment)) continue;
            hasTargetingAugment = true;
            if (TargetingAugment.isEntityValidForAugment(augment, entity)) return prerequisites;
        }

        if (hasTargetingAugment) return false;

        return entity instanceof IMob && prerequisites;
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

        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.sentry.getEntityBoundingBox().grow(this.sentry.tileEntity.attackRange));

        for (EntityLivingBase target : list) {
            if (isEntityValid(target)) {
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
