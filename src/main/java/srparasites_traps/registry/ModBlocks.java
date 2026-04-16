package srparasites_traps.registry;

import net.minecraft.block.Block;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.features.relocator.RelocatorBlock;
import srparasites_traps.features.sentry_turret.base.SentryTurretBase;
import srparasites_traps.features.serrated_grate.SerratedGrateBlock;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModBlocks {
    public static Block SENTRY_TURRET_BASE = new SentryTurretBase();
    public static Block RELOCATOR = new RelocatorBlock();
    public static Block SERRATED_GRATE = new SerratedGrateBlock();

    public static ArrayList<Block> getBlockList() {
        ArrayList<Block> blocks = new ArrayList<>();

        for (Field field : ModBlocks.class.getDeclaredFields()) {
            if (field.getType() != Block.class) continue;

            try {
                Block block = (Block) field.get(null);
                blocks.add(block);
            } catch (IllegalAccessException e) {
                SRParasitesTraps.LOGGER.error("Failed to get block from field {}", field.getName());
            }
        }

        return blocks;
    }
}
