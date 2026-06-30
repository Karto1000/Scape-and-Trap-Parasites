package srparasites_traps.handlers;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import srparasites_traps.features.infested_beacon.InfestedBeaconTileEntity;

@Mod.EventBusSubscriber
public class EntityHandler {
    private static boolean isEntityGrabbedByRelocator(EntityLivingBase entity) {
        return entity.getEntityData().getBoolean("GrabbedByRelocator");
    }

    @SubscribeEvent
    public void onEntitySuffocate(LivingAttackEvent event) {
        if (event.getSource() == DamageSource.IN_WALL) {
            EntityLivingBase victim = event.getEntityLiving();
            if (isEntityGrabbedByRelocator(victim)) event.setCanceled(true);
        } else if (event.getSource() == DamageSource.FLY_INTO_WALL) {
            EntityLivingBase victim = event.getEntityLiving();
            if (isEntityGrabbedByRelocator(victim)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityFall(LivingHurtEvent event) {
        if (event.getSource() == DamageSource.FALL) {
            EntityLivingBase victim = event.getEntityLiving();
            if (isEntityGrabbedByRelocator(victim)) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityTakeDamage(LivingDamageEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (!(victim instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) victim;
        PotionEffect effect = player.getActivePotionEffect(SRPPotions.PIVOT_E);
        if (effect == null) return;

        int amplifier = effect.getAmplifier();
        float damageAmount = event.getAmount();
        float damageReduced = (damageAmount / 100) *
                InfestedBeaconTileEntity.maxDamageReduction *
                ((float) (amplifier + 1) / InfestedBeaconTileEntity.maxLevel);
        event.setAmount(damageAmount - damageReduced);
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new EntityHandler());
    }
}
