package srparasites_traps.handlers;

import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import srparasites_traps.registry.ModItems;

@Mod.EventBusSubscriber
public class LootHandler {
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof EntityUnvo) {
            double chance = event.getEntity().world.rand.nextDouble();
            double bias = event.getEntity().world.rand.nextDouble();
            bias = bias * bias;

            if (chance < 0.2) {
                ItemStack stack = new ItemStack(ModItems.SENTRY_FLESH, (int) (bias * 2) + 1);
                EntityItem drop = new EntityItem(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
                event.getDrops().add(drop);
            }
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new LootHandler());
    }
}
