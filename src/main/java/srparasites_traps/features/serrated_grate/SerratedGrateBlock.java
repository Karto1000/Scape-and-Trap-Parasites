package srparasites_traps.features.serrated_grate;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class SerratedGrateBlock extends Block {
    public int baseDamage = 2;
    public double damageThreshold = 0.1;
    public double slowDownAmount = 0.9;
    public int minHurtResistantTime = 10;

    public SerratedGrateBlock() {
        super(Material.IRON, MapColor.IRON);

        this.setRegistryName("serrated_grate");
        this.setTranslationKey(getTranslationKeyFor("serrated_grate"));
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn.hurtResistantTime > minHurtResistantTime) return;

        if (entityIn instanceof EntityPlayer) {
            if (entityIn.isSneaking()) return;
            entityIn.attackEntityFrom(DamageSource.CACTUS, (float) baseDamage);
        } else if (entityIn instanceof EntityLiving) {
            double dX = entityIn.prevPosX - entityIn.posX;
            double dY = entityIn.prevPosY - entityIn.posY;
            double dZ = entityIn.prevPosZ - entityIn.posZ;
            float motionSum = (float) (Math.abs(dX) + Math.abs(dY) + Math.abs(dZ));
            if (motionSum < damageThreshold) return;
            System.out.println(baseDamage * motionSum * 5);
            entityIn.attackEntityFrom(DamageSource.CACTUS, baseDamage * motionSum * 5);
        }

        entityIn.motionX *= slowDownAmount;
        entityIn.motionY *= slowDownAmount;
        entityIn.motionZ *= slowDownAmount;
    }
}
