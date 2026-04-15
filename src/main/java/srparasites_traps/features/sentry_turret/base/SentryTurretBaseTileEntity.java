package srparasites_traps.features.sentry_turret.base;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.TurretTileEntity;
import srparasites_traps.features.sentry_turret.turret.SentryTileEntityState;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;
import srparasites_traps.util.Constants;

import java.util.Optional;
import java.util.UUID;

public class SentryTurretBaseTileEntity extends TurretTileEntity implements ITickable, ICapabilityProvider {
    private SentryTurretEntity assignedSentryTurret;
    private UUID assignedSentryTurretUUID;
    public int energyPerTick = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENERGY_PER_TICK;
    public int biomassPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_BIOMASS_PER_SHOT;
    public int energyPerShot = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_ENERGY_PER_SHOT;
    public int biomassForSpawn = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_BIOMASS_FOR_SPAWN;
    public double respawnTimeSeconds = ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_RESPAWN_TIME;
    private double currentRespawnTime = respawnTimeSeconds;
    private SentryTileEntityState state = SentryTileEntityState.INACTIVE;

    public SentryTurretBaseTileEntity() {
        super(ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_MAX_BIOMASS, ForgeConfigHandler.sentry.DEFAULT_SENTRY_TURRET_MAX_ENERGY);
    }

    public void setAssignedSentryTurret(SentryTurretEntity sentryTurret) {
        this.assignedSentryTurret = sentryTurret;
        this.assignedSentryTurretUUID = sentryTurret.getUniqueID();
        this.markDirty();
    }

    public SentryTileEntityState getState() {
        return this.state;
    }

    public void setState(SentryTileEntityState state) {
        this.state = state;
    }

    public Optional<SentryTurretEntity> getAssignedSentryTurret() {
        if (this.world.isRemote) return Optional.empty();

        if (this.assignedSentryTurret != null) {
            if (this.assignedSentryTurret.isDead) {
                this.clearAssignedSentryTurret();
                this.markDirty();
                return Optional.empty();
            }

            return Optional.of(assignedSentryTurret);
        }

        Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.assignedSentryTurretUUID);
        if (entity == null) {
            this.assignedSentryTurretUUID = null;
            this.markDirty();
            return Optional.empty();
        }
        if (!(entity instanceof SentryTurretEntity)) {
            this.assignedSentryTurretUUID = null;
            this.markDirty();
            return Optional.empty();
        }

        if (entity.isDead) {
            this.assignedSentryTurretUUID = null;
            this.markDirty();
            return Optional.empty();
        }

        this.assignedSentryTurret = (SentryTurretEntity) entity;
        this.markDirty();
        return Optional.of(this.assignedSentryTurret);
    }

    public void clearAssignedSentryTurret() {
        this.assignedSentryTurret = null;
        this.assignedSentryTurretUUID = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.assignedSentryTurretUUID != null) {
            compound.setUniqueId("TurretUUID", this.assignedSentryTurretUUID);
        }
        compound.setInteger("State", this.state.ordinal());
        compound.setDouble("CurrentRespawnTime", this.currentRespawnTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasUniqueId("TurretUUID")) {
            this.assignedSentryTurretUUID = compound.getUniqueId("TurretUUID");
        }
        if (compound.hasKey("State")) {
            this.state = SentryTileEntityState.values()[compound.getInteger("State")];
        }
        if (compound.hasKey("CurrentRespawnTime")) {
            this.currentRespawnTime = compound.getDouble("CurrentRespawnTime");
        }
    }

    public void removeTurret() {
        if (this.world.isRemote) return;
        Optional<SentryTurretEntity> entity = this.getAssignedSentryTurret();
        if (!entity.isPresent()) return;
        world.removeEntity(entity.get());
        this.clearAssignedSentryTurret();
        this.setState(SentryTileEntityState.INACTIVE);
        this.markDirty();
    }

    private void spawnTurret() {
        SentryTurretEntity newTurret = new SentryTurretEntity(this.world, this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5, this.pos);
        this.world.spawnEntity(newTurret);
        this.setAssignedSentryTurret(newTurret);
        this.consumeBiomass(this.biomassForSpawn);
        this.setState(SentryTileEntityState.ACTIVE);
        this.markDirty();
    }

    public void toggleEntity() {
        if (!this.getAssignedSentryTurret().isPresent()) {
            if (!this.hasEnoughBiomassForSpawn()) return;
            if (this.energyStorage.getEnergyStored() <= 0) return;
            if (this.state == SentryTileEntityState.DEAD) return;
            this.spawnTurret();
        } else this.removeTurret();
    }

    private void tryRespawn() {
        if (this.state != SentryTileEntityState.DEAD) return;

        if (this.currentRespawnTime > 0) {
            this.currentRespawnTime -= 1. / Constants.TPS_LIMIT;
            return;
        }

        if (!this.hasEnoughBiomassForSpawn()) return;

        this.spawnTurret();
        this.currentRespawnTime = this.respawnTimeSeconds;
    }

    @Override
    public void update() {
        if (this.world.isRemote) return;

        if (this.state == SentryTileEntityState.ACTIVE) {
            if (!this.consumeEnergy(this.energyPerTick)) {
                this.toggleEntity();
            }
        }

        tryRespawn();
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR, this.getState().ordinal());
        player.sendWindowProperty(container, AVAILABLE_WINDOW_VAR + 1, (int) this.currentRespawnTime);
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case AVAILABLE_WINDOW_VAR:
                this.setState(SentryTileEntityState.values()[data]);
            case AVAILABLE_WINDOW_VAR + 1:
                this.currentRespawnTime = data;
                break;
        }
    }

    public boolean consumeBiomass(int amount) {
        if (this.biomassStorage.getFluidAmount() < amount) return false;
        this.markDirty();
        return this.biomassStorage.drain(amount, true) != null;
    }

    public boolean consumeEnergy(int amount) {
        if (this.energyStorage.getEnergyStored() < amount) return false;
        this.markDirty();
        return this.energyStorage.extractEnergy(amount, false) == amount;
    }

    public boolean hasEnoughBiomassToShoot() {
        return this.biomassStorage.getFluidAmount() >= this.biomassPerShot;
    }

    public boolean hasEnoughBiomassForSpawn() {
        return this.biomassStorage.getFluidAmount() >= this.biomassForSpawn;
    }

    public boolean hasEnoughEnergy() {
        return this.energyStorage.getEnergyStored() >= this.energyPerShot && this.energyStorage.getEnergyStored() >= this.energyPerTick;
    }

    public double getCurrentRespawnTime() {
        return this.currentRespawnTime;
    }
}
