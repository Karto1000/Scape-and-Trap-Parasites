package srparasites_traps.features.sentry_turret;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class SentryTurretEntity extends EntityLiving {
    public int attackCooldown = 0;
    public int attackDelay = 20;
    private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(SentryTurretEntity.class, DataSerializers.BOOLEAN);

    public SentryTurretEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.7F, 4.1F);
    }

    @Override
    public float getEyeHeight() {
        return 3.6F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ATTACKING, Boolean.FALSE);
    }

    public boolean isAttacking() {
        return this.dataManager.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.dataManager.set(ATTACKING, attacking);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.renderYawOffset = this.rotationYawHead;

        if (!this.world.isRemote) this.setAttacking(this.getAttackTarget() != null);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new SentryTurretAI(this, this.world));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new AttackHostileMonsterInRange(this, this.world));
    }
}
