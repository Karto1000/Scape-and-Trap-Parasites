package srparasites_traps.features.sentry_turret.turret;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.sentry_turret.base.SentryTurretTileEntity;
import srparasites_traps.util.Constants;
import srparasites_traps.util.Serializers;

public class SentryTurretEntity extends EntityLiving {
    public BlockPos baseBlockPosition;
    public int attackDelay = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ATTACK_DELAY;
    public int currentAttackCooldown = attackDelay;
    public double attackRangeBlocks = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RANGE;
    public double emergeTimeSeconds = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_EMERGE_TIME;

    private static final DataParameter<Long> ticksWhenTargetLost = EntityDataManager.createKey(SentryTurretEntity.class, Serializers.LONG);
    private static final DataParameter<Float> currentEmergeTime = EntityDataManager.createKey(SentryTurretEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> state = EntityDataManager.createKey(SentryTurretEntity.class, DataSerializers.VARINT);

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

    public double getCurrentEmergeTime() {
        return dataManager.get(SentryTurretEntity.currentEmergeTime);
    }

    public void setCurrentEmergeTime(double time) {
        dataManager.set(SentryTurretEntity.currentEmergeTime, (float) time);
    }

    public void setEntityState(SentryTurretEntityState eState) {
        this.dataManager.set(state, eState.ordinal());
    }

    public SentryTurretEntityState getEntityState() {
        int ordinal = this.dataManager.get(state);
        return SentryTurretEntityState.values()[ordinal];
    }

    public long getTicksWhenTargetLost() {
        return this.dataManager.get(ticksWhenTargetLost);
    }

    public void setTicksWhenTargetLost(long ticks) {
        this.dataManager.set(ticksWhenTargetLost, ticks);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        this.getEntityData().setInteger("srpcothimmunity", 0);
    }

    @Override
    public float getEyeHeight() {
        return 3.6F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(state, SentryTurretEntityState.EMERGING.ordinal());
        this.dataManager.register(currentEmergeTime, 1F);
        this.dataManager.register(ticksWhenTargetLost, 0L);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if (potioneffectIn.getPotion() == SRPPotions.COTH_E) {
            return false;
        }

        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.world.isRemote) return;

        TileEntity tileEntity = this.world.getTileEntity(this.baseBlockPosition);
        if (tileEntity == null) return;
        SentryTurretTileEntity sentryTurretTileEntity = (SentryTurretTileEntity) tileEntity;

        sentryTurretTileEntity.setState(SentryTileEntityState.DEAD);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        SentryTurretEntityState state = getEntityState();
        if (state == SentryTurretEntityState.EMERGING) {
            double currentEmergeTime = getCurrentEmergeTime();
            setCurrentEmergeTime(currentEmergeTime - 1. / (Constants.TPS_LIMIT * emergeTimeSeconds));

            if (getCurrentEmergeTime() <= 0.00) {
                setEntityState(SentryTurretEntityState.IDLE);
            }
        }

        this.renderYawOffset = this.rotationYawHead;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new SentryTurretAttackTarget(this, this.world));
        this.targetTasks.addTask(1, new SentryTurretFindHostileMonster(this, this.world, this.attackRangeBlocks));
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
