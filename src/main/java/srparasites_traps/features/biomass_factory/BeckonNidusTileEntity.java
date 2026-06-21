package srparasites_traps.features.biomass_factory;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.NBTHelper;

public class BeckonNidusTileEntity extends TileCore implements ITickable {
    private int aliveTicks = 0;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("AliveTicks", this.aliveTicks);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.aliveTicks = NBTHelper.getIntegerOrElse(compound, "AliveTicks", () -> 0);
        super.readFromNBT(compound);
    }

    @Override
    public void update() {
        if (this.aliveTicks >= ForgeConfigHandler.common.BECKON_NIDUS_TICKS_TO_EVOLVE) {
            this.world.destroyBlock(this.pos, false);

            EntityVenkrol beckon = new EntityVenkrol(this.world);
            beckon.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
            this.world.spawnEntity(beckon);
            beckon.playSound(SRPSounds.VENKROLSI, 4.0f, 1.0f);

            this.world.removeTileEntity(this.pos);
            return;
        }

        this.aliveTicks++;
        this.markDirty();
    }
}