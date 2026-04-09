package srparasites_traps.features.sentry_turret.base;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import srparasites_traps.features.sentry_turret.turret.SentryTurretEntity;

import java.util.Optional;
import java.util.UUID;

public class SentryTurretTileEntity extends TileEntity implements ITickable {
    private SentryTurretEntity assignedSentryTurret;
    private UUID assignedSentryTurretUUID;

    public void setAssignedSentryTurret(SentryTurretEntity sentryTurret) {
        this.assignedSentryTurret = sentryTurret;
        this.assignedSentryTurretUUID = sentryTurret.getUniqueID();
        this.markDirty();
    }

    public Optional<SentryTurretEntity> getAssignedSentryTurret() {
        if (this.world.isRemote) return Optional.empty();

        if (this.assignedSentryTurret != null) {
            if (this.assignedSentryTurret.isDead) {
                this.assignedSentryTurret = null;
                this.assignedSentryTurretUUID = null;
                return Optional.empty();
            }
            return Optional.of(assignedSentryTurret);
        }

        Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.assignedSentryTurretUUID);
        if (entity == null) return Optional.empty();
        if (!(entity instanceof SentryTurretEntity)) return Optional.empty();

        if (entity.isDead) {
            this.assignedSentryTurret = null;
            this.assignedSentryTurretUUID = null;
            return Optional.empty();
        }

        this.assignedSentryTurret = (SentryTurretEntity) entity;
        return Optional.of(this.assignedSentryTurret);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.assignedSentryTurretUUID != null) {
            compound.setUniqueId("TurretUUID", this.assignedSentryTurretUUID);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasUniqueId("TurretUUID")) {
            this.assignedSentryTurretUUID = compound.getUniqueId("TurretUUID");
        }
    }

    @Override
    public void update() {

    }
}
