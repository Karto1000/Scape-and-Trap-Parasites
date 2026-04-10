package srparasites_traps.features.sentry_turret.turret;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

import java.util.List;

public class AttackHostileMonsterInRange extends EntityAIBase {
    private final EntityLiving entity;
    private final World world;
    private final double range;

    public AttackHostileMonsterInRange(EntityLiving entity, World world, double range) {
        this.entity = entity;
        this.world = world;
        this.range = range;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() == null || !this.entity.getAttackTarget().isEntityAlive();
    }

    @Override
    public void startExecuting() {
        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.entity.getEntityBoundingBox().grow(range));

        for (EntityLivingBase target : list) {
            if (target instanceof IMob && target.isEntityAlive() && this.entity.canEntityBeSeen(target) && this.entity.getDistance(target) <= range) {
                this.entity.setAttackTarget(target);
                break;
            }
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = this.entity.getAttackTarget();
        return target != null && target.isEntityAlive();
    }


    @Override
    public void resetTask() {
        this.entity.setAttackTarget(null);
    }
}
