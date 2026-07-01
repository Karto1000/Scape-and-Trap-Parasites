package srparasites_traps.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
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
    private void doDrop(
            LivingDropsEvent e,
            World world,
            double dropChance,
            int maxDropAmount,
            Item item
    ) {
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
        Entity entity = event.getEntity();

        if (entity instanceof EntityUnvo || entity instanceof EntityNak) {
            World world = entity.world;
            doDrop(event, world, ForgeConfigHandler.common.PARASITIC_MATTER_DROP_CHANCE, ForgeConfigHandler.common.MAX_PARASITIC_MATTER_DROP_AMOUNT, ModItems.PARASITIC_MATTER);
        } else if (entity instanceof EntityPDispatcher) {
            EntityPDispatcher dod = (EntityPDispatcher) entity;
            World world = entity.world;

            doDrop(event, world, ForgeConfigHandler.common.DISPATCHER_CEREBRUM_DROP_CHANCE * Math.max(dod.getStageV() * .75, 1), ForgeConfigHandler.common.MAX_DISPATCHER_CEREBRUM_DROP_AMOUNT, ModItems.DISPATCHER_CEREBRUM);
            doDrop(event, world, ForgeConfigHandler.common.DISPATCHER_BRAINSTEM_DROP_CHANCE * Math.max(dod.getStageV() * .75, 1), ForgeConfigHandler.common.MAX_DISPATCHER_BRAINSTEM_DROP_AMOUNT, ModItems.DISPATCHER_BRAINSTEM);
        } else if (entity instanceof EntityVenkrol) {
            EntityVenkrol venkrol = (EntityVenkrol) entity;
            World world = entity.world;

            doDrop(event, world, ForgeConfigHandler.common.BECKON_HEART_DROP_CHANCE * Math.max(venkrol.getStageV() * .75, 1), ForgeConfigHandler.common.MAX_BECKON_HEART_DROP_AMOUNT, ModItems.BECKON_HEART);
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new LootHandler());
    }
}
