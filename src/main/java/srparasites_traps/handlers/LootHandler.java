package srparasites_traps.handlers;

import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModItems;

@Mod.EventBusSubscriber
public class LootHandler {
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof EntityUnvo) {
            World world = event.getEntity().world;
            double chance = world.rand.nextInt(100);

            if (chance < ForgeConfigHandler.common.SENTRY_FLESH_DROP_CHANCE) {
                ItemStack stack = new ItemStack(ModItems.SENTRY_FLESH, world.rand.nextInt(ForgeConfigHandler.common.MAX_SENTRY_FLESH_DROP_AMOUNT + 1));
                EntityItem drop = new EntityItem(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
                event.getDrops().add(drop);
            }
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new LootHandler());
    }
}
