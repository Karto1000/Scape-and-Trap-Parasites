package srparasites_traps.features.sentry_turret.turret;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;

public class SentryTurretEntity extends EntityLiving {
    public BlockPos baseBlockPosition;
    public int attackDelay = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ATTACK_DELAY;
    public int currentAttackCooldown = attackDelay;
    public final double attackRangeBlocks = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RANGE;
    public long ticksWhenTargetLost = 0;
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

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if (potioneffectIn.getPotion() == SRPPotions.COTH_E) {
            return false;
        }

        return super.isPotionApplicable(potioneffectIn);
    }

    public boolean isAttacking() {
        return this.dataManager.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.dataManager.set(ATTACKING, attacking);
    }

    @Override
    public boolean canBePushed() {
        return false;
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
        this.targetTasks.addTask(1, new AttackHostileMonsterInRange(this, this.world, this.attackRangeBlocks));
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
