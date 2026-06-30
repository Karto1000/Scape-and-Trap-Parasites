package srparasites_traps.features.infested_beacon;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.UpdateLimiter;

import java.util.List;

public class InfestedBeaconTileEntity extends TileCore implements ITickable {
    public static final int range = ForgeConfigHandler.infestedBeacon.RANGE;
    public static final int pivotDuration = ForgeConfigHandler.infestedBeacon.PIVOT_DURATION;
    public static final int maxDamageReduction = ForgeConfigHandler.infestedBeacon.MAX_DAMAGE_REDUCTION_PERCENT;
    public static final int maxPivotEffectLevel = ForgeConfigHandler.infestedBeacon.MAX_PIVOT_EFFECT_LEVEL;

    private final UpdateLimiter updateLimiter = new UpdateLimiter(10);
    private byte[] powerLevels = new byte[4];

    private byte getPowerLevelOfBlock(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != SRPBlocks.evolutionLure) return 0;

        BlockEvolutionLure.EnumType variant = state.getValue(BlockEvolutionLure.VARIANT);
        return (byte) (variant.ordinal() + 1);
    }

    private byte[] calculatePowerLevels() {
        byte[] newPowerLevels = new byte[4];
        newPowerLevels[0] = getPowerLevelOfBlock(pos.north().west());
        newPowerLevels[1] = getPowerLevelOfBlock(pos.north().east());
        newPowerLevels[2] = getPowerLevelOfBlock(pos.south().east());
        newPowerLevels[3] = getPowerLevelOfBlock(pos.south().west());
        return newPowerLevels;
    }

    private int getMaxPower() {
        // Right now this is the last obtainable one
        return (BlockEvolutionLure.EnumType.SIX.ordinal() + 1) * powerLevels.length;
    }

    public byte[] getPowerLevels() {
        return powerLevels;
    }

    public int getTotalPower() {
        int sum = 0;
        for (byte powerLevel : powerLevels) sum += powerLevel;
        return sum;
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        int powerLevelInt = this.powerLevels[0] << 24 | this.powerLevels[1] << 16 | this.powerLevels[2] << 8 | this.powerLevels[3];
        player.sendWindowProperty(container, 0, powerLevelInt);
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        switch (id) {
            case 0:
                this.powerLevels[0] = (byte) (data >> 24);
                this.powerLevels[1] = (byte) (data >> 16);
                this.powerLevels[2] = (byte) (data >> 8);
                this.powerLevels[3] = (byte) data;
                break;
        }
    }

    @Override
    public void update() {
        if (updateLimiter.tickDown()) return;
        powerLevels = calculatePowerLevels();

        AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(range);
        List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        for (EntityPlayer entity : entities) {
            int amplifier = (int) Math.floor((double) getTotalPower() / getMaxPower() * maxPivotEffectLevel);
            entity.addPotionEffect(new PotionEffect(SRPPotions.PIVOT_E, pivotDuration, amplifier));
        }

        updateLimiter.reset();
    }
}
