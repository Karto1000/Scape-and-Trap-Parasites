package srparasites_traps.features.sentry_turret;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.IDefaultValueHolder;
import srparasites_traps.features.IExtendedAugmentable;
import srparasites_traps.features.TurretTileEntity;
import srparasites_traps.features.augments.AttackSpeedAugment;
import srparasites_traps.features.augments.DamageAugment;
import srparasites_traps.features.augments.RangeAugment;
import srparasites_traps.util.Constants;
import srparasites_traps.util.DebugHelper;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.StateManager;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class SentryTurretTileEntity extends TurretTileEntity implements ITickable, ICapabilityProvider, IExtendedAugmentable, IDefaultValueHolder {
    public int attackDelay = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ATTACK_DELAY;
    public int biomassPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_BIOMASS_PER_SHOT;
    public int energyPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESPAWN_TIME;
    public int poisonDuration = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_POISON_DURATION;
    public int poisonAmplifier = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_POISON_AMPLIFIER;
    public int chanceToReduceResistance = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCE_CHANCE;
    public int resistanceReductionAmount = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCTION_AMOUNT;
    public int entityHealInterval = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENTITY_HEAL_INTERVAL;
    public int entityHealAmount = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENTITY_HEAL_AMOUNT;
    public double respawnTimeSeconds = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENERGY_PER_SHOT;
    public double attackRange = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RANGE;
    public double emergeTimeSeconds = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_EMERGE_TIME;
    public double damage = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_DAMAGE;

    private SentryTurretEntity assignedSentryTurret;
    private UUID assignedSentryTurretUUID;
    private boolean firstTick = true;
    private double currentRespawnTime = 0.;
    private final StateManager<SentryTileEntityState> state = new StateManager<>(SentryTileEntityState.DEAD, (oldState, newState) -> DebugHelper.dbp("State changed from " + oldState + " to " + newState));
    private final static int SENTRY_TURRET_ENTITY_HEIGHT_BLOCKS = 4;
    private final ItemStack[] augments = Stream.generate(() -> ItemStack.EMPTY)
            .limit(ForgeConfigHandler.augments.SENTRY_TURRET_AUGMENT_SLOTS)
            .toArray(ItemStack[]::new);

    public SentryTurretTileEntity() {
        super(ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_MAX_BIOMASS, ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_MAX_ENERGY);
    }

    public void setAssignedSentryTurret(SentryTurretEntity sentryTurret) {
        this.assignedSentryTurret = sentryTurret;
        this.assignedSentryTurretUUID = sentryTurret.getUniqueID();
        this.markDirty();
    }

    public SentryTileEntityState getState() {
        return this.state.getState();
    }

    public void setState(SentryTileEntityState state) {
        this.state.setState(state, this.world.getTotalWorldTime());
    }

    public void spawnTurret() {
        DebugHelper.dbp("Spawning sentry turret");

        SentryTurretEntity newTurret = new SentryTurretEntity(
                this.world,
                this.pos.getX() + 0.5,
                this.pos.getY() + 1,
                this.pos.getZ() + 0.5,
                this.pos,
                this
        );

        this.world.spawnEntity(newTurret);
        this.setAssignedSentryTurret(newTurret);
        this.markDirty();
    }

    public void despawnTurret() {
        DebugHelper.dbp("Despawning sentry turret");

        Optional<SentryTurretEntity> turret = this.getAssignedSentryTurret();
        if (!turret.isPresent()) return;

        this.world.removeEntity(this.assignedSentryTurret);
        this.clearAssignedSentryTurret();
        this.markDirty();
    }

    public Optional<SentryTurretEntity> getAssignedSentryTurret() {
        if (this.world.isRemote) return Optional.empty();

        if (this.assignedSentryTurret != null) {
            if (this.assignedSentryTurret.isDead) {
                DebugHelper.dbp("Clearing dead sentry turret");

                this.clearAssignedSentryTurret();
                this.markDirty();
                return Optional.empty();
            }

            return Optional.of(assignedSentryTurret);
        }

        DebugHelper.dbp("Getting assigned sentry turret entity from uuid");

        Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.assignedSentryTurretUUID);
        if (entity == null) {
            DebugHelper.dbp("Assigned sentry turret entity is null, aborting");

            this.assignedSentryTurretUUID = null;
            this.markDirty();
            return Optional.empty();
        }
        if (!(entity instanceof SentryTurretEntity)) {
            DebugHelper.dbp("Assigned sentry turret entity is not a sentry turret, aborting");

            this.assignedSentryTurretUUID = null;
            this.markDirty();
            return Optional.empty();
        }

        if (entity.isDead) {
            DebugHelper.dbp("Assigned sentry turret entity is dead, aborting");

            this.assignedSentryTurretUUID = null;
            this.markDirty();
            return Optional.empty();
        }

        this.assignedSentryTurret = (SentryTurretEntity) entity;
        this.markDirty();
        return Optional.of(this.assignedSentryTurret);
    }

    public void clearAssignedSentryTurret() {
        DebugHelper.dbp("Clearing assigned sentry turret");

        this.assignedSentryTurret = null;
        this.assignedSentryTurretUUID = null;
    }

    public boolean areBlocksAboveOccupied() {
        for (int i = 1; i <= SENTRY_TURRET_ENTITY_HEIGHT_BLOCKS; i++) {
            if (!this.world.isAirBlock(this.pos.up(i))) return true;
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setDouble("CurrentRespawnTime", this.currentRespawnTime);
        compound.setInteger("State", this.state.getState().ordinal());
        compound.setInteger("ControlMode", this.controlMode.ordinal());
        this.writeAugmentsToNBT(compound);

        if (this.assignedSentryTurretUUID != null)
            compound.setUniqueId("AssignedSentryTurret", this.assignedSentryTurretUUID);
        DebugHelper.dbp("Wrote assigned sentry turret uuid: " + this.assignedSentryTurretUUID);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.currentRespawnTime = NBTHelper.getDoubleOrElse(compound, "CurrentRespawnTime", () -> 0.0);
        this.state.setState(SentryTileEntityState.values()[NBTHelper.getIntegerOrElse(compound, "State", SentryTileEntityState.DEAD::ordinal)]);
        this.controlMode = ControlMode.values()[NBTHelper.getIntegerOrElse(compound, "ControlMode", ControlMode.DISABLED::ordinal)];
        this.readAugmentsFromNBT(compound);
        if (compound.hasUniqueId("AssignedSentryTurret"))
            this.assignedSentryTurretUUID = compound.getUniqueId("AssignedSentryTurret");
        DebugHelper.dbp("Read assigned sentry turret uuid: " + this.assignedSentryTurretUUID);
    }

    @Override
    public void update() {
        if (firstTick) {
            this.updateAugmentStatus();
            firstTick = false;
        }

        switch (this.state.getState()) {
            case DEAD:
                if (this.currentRespawnTime > 0) {
                    this.currentRespawnTime -= 1. / Constants.TPS_LIMIT;
                    return;
                }

                if (this.controlMode == ControlMode.LOW && !this.powered) return;
                if (this.controlMode == ControlMode.HIGH && !this.powered) return;
                if (this.areBlocksAboveOccupied()) return;

                this.spawnTurret();
                this.state.switchState(this.world.getTotalWorldTime());
                this.currentRespawnTime = this.respawnTimeSeconds;
                return;
            case INACTIVE:
                if (this.areBlocksAboveOccupied()) return;

                boolean shouldSpawn = this.controlMode == ControlMode.DISABLED;
                if (this.controlMode == ControlMode.LOW && this.powered) shouldSpawn = true;
                if (this.controlMode == ControlMode.HIGH && this.powered) shouldSpawn = true;
                if (!shouldSpawn) return;

                this.spawnTurret();
                this.state.switchState(this.world.getTotalWorldTime());

                return;
            case ACTIVE:
                boolean shouldHide = this.controlMode != ControlMode.DISABLED && !this.powered;
                if (!shouldHide) return;
                this.state.switchState(this.world.getTotalWorldTime());
                this.despawnTurret();
        }
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR, this.getState().ordinal());
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 1, (int) this.currentRespawnTime);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 2, (int) this.damage);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 3, this.attackDelay);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 4, (int) this.attackRange);
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case AVAILABLE_WINDOW_VAR:
                this.setState(SentryTileEntityState.values()[data]);
                break;
            case AVAILABLE_WINDOW_VAR + 1:
                this.currentRespawnTime = data;
                break;
            case AVAILABLE_WINDOW_VAR + 2:
                this.damage = data;
                break;
            case AVAILABLE_WINDOW_VAR + 3:
                this.attackDelay = data;
                break;
            case AVAILABLE_WINDOW_VAR + 4:
                this.attackRange = data;
                break;
        }
    }

    public void consumeBiomass(int amount) {
        if (this.biomassStorage.getFluidAmount() < amount) return;
        this.biomassStorage.drain(amount, true);
        this.markDirty();
    }

    public void consumeEnergy(int amount) {
        if (this.energyStorage.getEnergyStored() < amount) return;
        this.energyStorage.extractEnergy(amount, false);
        this.markDirty();
    }

    public boolean isMissingBiomass() {
        return this.biomassStorage.getFluidAmount() < this.biomassPerShot;
    }

    public boolean isMissingEnergy() {
        return this.energyStorage.getEnergyStored() < this.energyPerShot;
    }

    public double getCurrentRespawnTime() {
        return this.currentRespawnTime;
    }

    @Override
    public boolean isValidAugment(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        if (itemStack.getItem() instanceof AttackSpeedAugment) return true;
        if (itemStack.getItem() instanceof DamageAugment) return true;
        return itemStack.getItem() instanceof RangeAugment;
    }

    @Override
    public ItemStack[] getAugmentSlots() {
        return this.augments;
    }

    @Override
    public void applyAugment(ItemStack itemStack) {
        if (itemStack.isEmpty()) return;
        if (itemStack.getItem() instanceof AttackSpeedAugment)
            this.attackDelay -= ForgeConfigHandler.augments.SENTRY_TURRET_ATTACK_SPEED_INCREASE;
        if (itemStack.getItem() instanceof RangeAugment)
            this.attackRange += ForgeConfigHandler.augments.SENTRY_TURRET_RANGE_INCREASE;
        if (itemStack.getItem() instanceof DamageAugment)
            this.damage += ForgeConfigHandler.augments.SENTRY_TURRET_DAMAGE_INCREASE;
    }

    @Override
    public void applyDefaults() {
        this.attackDelay = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ATTACK_DELAY;
        this.biomassPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_BIOMASS_PER_SHOT;
        this.energyPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENERGY_PER_SHOT;
        this.respawnTimeSeconds = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESPAWN_TIME;
        this.attackRange = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RANGE;
        this.emergeTimeSeconds = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_EMERGE_TIME;
        this.damage = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_DAMAGE;
        this.poisonDuration = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_POISON_DURATION;
        this.poisonAmplifier = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_POISON_AMPLIFIER;
        this.chanceToReduceResistance = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCE_CHANCE;
        this.resistanceReductionAmount = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCTION_AMOUNT;
        this.entityHealInterval = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENTITY_HEAL_INTERVAL;
        this.entityHealAmount = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENTITY_HEAL_AMOUNT;
    }
}
