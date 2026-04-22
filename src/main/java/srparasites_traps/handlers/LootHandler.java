package srparasites_traps.handlers;

import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
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
    private void doDrop(LivingDropsEvent e, World world, int dropChance, int maxDropAmount, Item item) {
        double chance = world.rand.nextDouble();

        if (chance < dropChance / 100. + e.getLootingLevel() * 0.05) {
            ItemStack stack = new ItemStack(item, world.rand.nextInt(maxDropAmount + 1));
            Entity entity = e.getEntity();
            EntityItem drop = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack);
            e.getDrops().add(drop);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof EntityUnvo) {
            World world = event.getEntity().world;
            doDrop(event, world, ForgeConfigHandler.common.SENTRY_FLESH_DROP_CHANCE, ForgeConfigHandler.common.MAX_SENTRY_FLESH_DROP_AMOUNT, ModItems.SENTRY_SKIN);
        } else if (event.getEntity() instanceof EntityDod) {
            World world = event.getEntity().world;
            doDrop(event, world, ForgeConfigHandler.common.DISPATCHER_CEREBRUM_DROP_CHANCE, ForgeConfigHandler.common.MAX_DISPATCHER_CEREBRUM_DROP_AMOUNT, ModItems.DISPATCHER_CEREBRUM);
            doDrop(event, world, ForgeConfigHandler.common.DISPATCHER_BRAINSTEM_DROP_CHANCE, ForgeConfigHandler.common.MAX_DISPATCHER_BRAINSTEM_DROP_AMOUNT, ModItems.DISPATCHER_BRAINSTEM);
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new LootHandler());
    }
}
