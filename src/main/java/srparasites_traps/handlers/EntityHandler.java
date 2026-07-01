package srparasites_traps.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new EntityHandler());
    }
}
