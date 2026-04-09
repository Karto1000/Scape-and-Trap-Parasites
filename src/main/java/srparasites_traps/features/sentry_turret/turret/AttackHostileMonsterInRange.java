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

    public AttackHostileMonsterInRange(EntityLiving entity, World world) {
        this.entity = entity;
        this.world = world;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() == null || !this.entity.getAttackTarget().isEntityAlive();
    }

    @Override
    public void startExecuting() {
        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
                this.entity.getEntityBoundingBox().grow(16.0D, 4.0D, 16.0D));

        for (EntityLivingBase target : list) {
            if (target instanceof IMob && target.isEntityAlive() && this.entity.canEntityBeSeen(target)) {
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
