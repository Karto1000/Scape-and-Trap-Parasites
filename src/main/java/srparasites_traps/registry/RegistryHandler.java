package srparasites_traps.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import srparasites_traps.SRParasitesTraps;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = SRParasitesTraps.MOD_ID)
public class RegistryHandler {

    public static void init() {
    }

    @SubscribeEvent
    public static void registerEntityEvent(RegistryEvent.Register<EntityEntry> event) {
        ArrayList<EntityEntry> entities = ModEntities.getEntityList();
        SRParasitesTraps.LOGGER.info("Registering Entities {}", entities);
        event.getRegistry().registerAll(entities.toArray(new EntityEntry[0]));
    }

    @SubscribeEvent
    public static void registerItemEvent(RegistryEvent.Register<Item> event) {
        ArrayList<Item> items = ModItems.getItemList();
        SRParasitesTraps.LOGGER.info("Registering Items {}", items);
        event.getRegistry().registerAll(items.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void registerBlockEvent(RegistryEvent.Register<Block> event) {
        ArrayList<Block> blocks = ModBlocks.getBlockList();
        SRParasitesTraps.LOGGER.info("Registering Blocks {}", blocks);
        event.getRegistry().registerAll(blocks.toArray(new Block[0]));
    }
}