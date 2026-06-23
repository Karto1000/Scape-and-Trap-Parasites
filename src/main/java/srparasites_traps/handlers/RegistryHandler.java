package srparasites_traps.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.registry.*;

import java.util.List;

@Mod.EventBusSubscriber(modid = SRParasitesTraps.MOD_ID)
public class RegistryHandler {

    public static void init() {
        ModTileEntities.registerTileEntities();
        ModSerializers.registerSerializers();
    }

    public static void registerItemRenderers() {
        for (Item item : ModItems.getItemList()) {
            SRParasitesTraps.LOGGER.info("Registering Item Renderer for {}", item);
            SRParasitesTraps.PROXY.registerItemRenderer(item, 0, "inventory");
        }
    }

    @SubscribeEvent
    public static void registerEntityEvent(RegistryEvent.Register<EntityEntry> event) {
        List<EntityEntry> entities = ModEntities.getEntityList();
        SRParasitesTraps.LOGGER.info("Registering Entities {}", entities);
        event.getRegistry().registerAll(entities.toArray(new EntityEntry[0]));
    }

    @SubscribeEvent
    public static void registerSoundEvent(RegistryEvent.Register<SoundEvent> event) {
        List<SoundEvent> sounds = ModSounds.getSoundList();
        SRParasitesTraps.LOGGER.info("Registering Sounds {}", sounds);
        event.getRegistry().registerAll(sounds.toArray(new SoundEvent[0]));
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        List<Potion> potions = ModPotions.getPotionList();
        SRParasitesTraps.LOGGER.info("Registering Potions {}", potions);
        event.getRegistry().registerAll(potions.toArray(new Potion[0]));
    }

    @SubscribeEvent
    public static void registerItemEvent(RegistryEvent.Register<Item> event) {
        List<Item> items = ModItems.getItemList();
        SRParasitesTraps.LOGGER.info("Registering Items {}", items);
        event.getRegistry().registerAll(items.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void registerBlockEvent(RegistryEvent.Register<Block> event) {
        List<Block> blocks = ModBlocks.getBlockList();
        SRParasitesTraps.LOGGER.info("Registering Blocks {}", blocks);
        event.getRegistry().registerAll(blocks.toArray(new Block[0]));
    }
}