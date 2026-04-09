package srparasites_traps.features.sentry_turret.turret;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SentryTurretEntity extends EntityLiving {
    public int attackDelay = 20;
    public int currentAttackCooldown = attackDelay;
    public long ticksWhenTargetLost = 0;
    public BlockPos baseBlockPosition;
    private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(SentryTurretEntity.class, DataSerializers.BOOLEAN);

    public SentryTurretEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.7F, 4.1F);
    }

    public SentryTurretEntity(World worldIn, double x, double y, double z, BlockPos baseBlockPosition) {
        super(worldIn);
        this.setSize(0.7F, 4.1F);
        this.setPosition(x, y, z);
        this.baseBlockPosition = baseBlockPosition;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
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
        if (!this.world.isRemote)
            this.setAttacking(this.getAttackTarget() != null || this.world.getTotalWorldTime() - ticksWhenTargetLost <= attackDelay);
        this.setVelocity(0, 0, 0);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new SentryTurretAI(this, this.world));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AttackHostileMonsterInRange(this, this.world));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.baseBlockPosition != null) {
            compound.setLong("BaseBlockPosition", this.baseBlockPosition.toLong());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("BaseBlockPosition")) {
            this.baseBlockPosition = BlockPos.fromLong(compound.getLong("BaseBlockPosition"));
        }
    }
}
