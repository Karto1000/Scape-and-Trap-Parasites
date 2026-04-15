package srparasites_traps.features.relocator;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.Constants;
import srparasites_traps.util.NBTHelper;

import java.util.Optional;
import java.util.UUID;

public class RelocatorEntity extends EntityLiving {
    public double emergeTimeSeconds = ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_EMERGE_TIME;
    private static final DataParameter<Float> currentEmergeTime = EntityDataManager.createKey(RelocatorEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> state = EntityDataManager.createKey(RelocatorEntity.class, DataSerializers.VARINT);

    private BlockPos tileEntityPosition;
    private BlockPos targetPosition;
    private BlockPos spawnPosition;
    private EntityLivingBase entityToRelocate;
    private UUID entityToRelocateUUID;

    public RelocatorEntity(World worldIn) {
        super(worldIn);
    }

    public RelocatorEntity(World worldIn, BlockPos tileEntityPosition, BlockPos spawnPosition, BlockPos targetPosition, EntityLivingBase entityToRelocate) {
        super(worldIn);
        this.spawnPosition = spawnPosition;
        this.tileEntityPosition = tileEntityPosition;
        this.targetPosition = targetPosition;
        this.entityToRelocate = entityToRelocate;

        entityToRelocate.getEntityData().setBoolean("GrabbedByRelocator", true);

        this.entityToRelocateUUID = entityToRelocate.getUniqueID();
        this.setPosition(spawnPosition.getX() + 0.5, spawnPosition.getY(), spawnPosition.getZ() + 0.5);
    }

    public double getCurrentEmergeTime() {
        return dataManager.get(RelocatorEntity.currentEmergeTime);
    }

    public void setCurrentEmergeTime(double time) {
        dataManager.set(RelocatorEntity.currentEmergeTime, (float) time);
    }

    public RelocatorEntityState getEntityState() {
        int ordinal = this.dataManager.get(state);
        return RelocatorEntityState.values()[ordinal];
    }

    public void setEntityState(RelocatorEntityState eState) {
        this.dataManager.set(state, eState.ordinal());
    }

    public float getEyeHeight() {
        return 1.8F;
    }

    private void releaseEntity() {
        if (this.entityToRelocate != null) {
            this.entityToRelocate.getEntityData().setBoolean("GrabbedByRelocator", false);
        }

        this.entityToRelocate = null;
        this.entityToRelocateUUID = null;
    }

    private Optional<EntityLivingBase> getEntityToRelocate() {
        if (this.world.isRemote) return Optional.empty();

        if (this.entityToRelocate != null) {
            if (this.entityToRelocate.isDead) {
                this.releaseEntity();
                return Optional.empty();
            }

            return Optional.of(entityToRelocate);
        }

        Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.entityToRelocateUUID);

        if (entity == null) {
            this.entityToRelocateUUID = null;
            return Optional.empty();
        }

        if (!(entity instanceof EntityLivingBase)) {
            this.entityToRelocateUUID = null;
            return Optional.empty();
        }

        if (entity.isDead) {
            this.entityToRelocateUUID = null;
            return Optional.empty();
        }

        this.entityToRelocate = (EntityLivingBase) entity;
        return Optional.of(this.entityToRelocate);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ForgeConfigHandler.relocator.DEFAULT_RELOCATOR_MAX_HEALTH);
        this.getEntityData().setInteger("srpcothimmunity", 0);
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
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setDouble("CurrentEmergeTime", getCurrentEmergeTime());
        compound.setInteger("State", getEntityState().ordinal());
        compound.setLong("TargetPosition", targetPosition.toLong());
        compound.setLong("TileEntityPosition", tileEntityPosition.toLong());
        compound.setUniqueId("EntityToRelocate", entityToRelocateUUID);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        setCurrentEmergeTime(
                NBTHelper.getDoubleOrElse(
                        compound,
                        "CurrentEmergeTime",
                        () -> 0.0
                )
        );

        setEntityState(
                NBTHelper.getOrElse(
                        compound,
                        "State",
                        () -> RelocatorEntityState.values()[compound.getInteger("State")],
                        () -> RelocatorEntityState.EMERGING
                )
        );

        spawnPosition = NBTHelper.getBlockPosOrDie(
                this,
                compound,
                "SpawnPosition"
        );

        targetPosition = NBTHelper.getBlockPosOrDie(
                this,
                compound,
                "TargetPosition"
        );

        tileEntityPosition = NBTHelper.getBlockPosOrDie(
                this,
                compound,
                "TileEntityPosition"
        );

        entityToRelocateUUID = NBTHelper.getUniqueIdOrDie(
                this,
                compound,
                "EntityToRelocate"
        );
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(currentEmergeTime, 1F);
        this.dataManager.register(state, RelocatorEntityState.EMERGING.ordinal());
    }

    public void despawn() {
        if (this.world.isRemote) return;

        TileEntity tileEntity = this.world.getTileEntity(this.tileEntityPosition);
        if (tileEntity instanceof RelocatorTileEntity) {
            RelocatorTileEntity relocatorTileEntity = (RelocatorTileEntity) tileEntity;
            relocatorTileEntity.setState(RelocatorTileEntityState.IDLE);
            if (relocatorTileEntity.getCurrentRelocatorCount() < relocatorTileEntity.maxRelocatorsInReserve) {
                relocatorTileEntity.setCurrentRelocatorCount(relocatorTileEntity.getCurrentRelocatorCount() + 1);
            }
        }

        this.world.removeEntity(this);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        this.releaseEntity();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.setVelocity(0, 0, 0);

        RelocatorEntityState state = getEntityState();
        if (state == RelocatorEntityState.EMERGING) {
            double currentEmergeTime = getCurrentEmergeTime();
            setCurrentEmergeTime(currentEmergeTime - 1. / (Constants.TPS_LIMIT * emergeTimeSeconds));

            Optional<EntityLivingBase> entityToRelocate = getEntityToRelocate();
            entityToRelocate.ifPresent(e -> e.setPositionAndUpdate(spawnPosition.getX() + 0.5, spawnPosition.getY(), spawnPosition.getZ() + 0.5));

            if (getCurrentEmergeTime() <= 0.00) {
                setEntityState(RelocatorEntityState.RETRACTING);
            }
        } else if (state == RelocatorEntityState.RETRACTING) {
            double currentEmergeTime = getCurrentEmergeTime();
            setCurrentEmergeTime(currentEmergeTime + 1. / (Constants.TPS_LIMIT * emergeTimeSeconds));

            Optional<EntityLivingBase> entityToRelocate = getEntityToRelocate();
            entityToRelocate.ifPresent(e -> {
                // The entity should only attack them once it is actually grabbed
                e.setRevengeTarget(this);
                if (e instanceof EntityParasiteBase) ((EntityParasiteBase) e).setAttackTarget(this);
                e.setPositionAndUpdate(spawnPosition.getX() + 0.5, spawnPosition.getY() - (this.getEyeHeight() + 1) * currentEmergeTime, spawnPosition.getZ() + 0.5);
                e.setVelocity(0, 0, 0);
            });

            if (getCurrentEmergeTime() >= 1.00) {
                if (!entityToRelocate.isPresent()) {
                    despawn();
                    return;
                }

                setEntityState(RelocatorEntityState.RELOCATING);
                this.setPosition(targetPosition.getX() + 0.5, targetPosition.getY(), targetPosition.getZ() + 0.5);
            }
        } else if (state == RelocatorEntityState.RELOCATING) {
            double currentEmergeTime = getCurrentEmergeTime();
            setCurrentEmergeTime(currentEmergeTime - 1. / (Constants.TPS_LIMIT * emergeTimeSeconds));

            Optional<EntityLivingBase> entityToRelocate = getEntityToRelocate();
            // Move the entity to the target position and a bit above so it doesn't clip through the block
            entityToRelocate.ifPresent(e -> {
                e.setPositionAndUpdate(targetPosition.getX() + 0.5, targetPosition.getY() - (this.getEyeHeight() + 1) * currentEmergeTime + 0.25, targetPosition.getZ() + 0.5);
                e.setVelocity(0, 0, 0);
            });

            if (getCurrentEmergeTime() <= 0.00) {
                setEntityState(RelocatorEntityState.RETRACTING);
                releaseEntity();
            }
        }
    }
}
