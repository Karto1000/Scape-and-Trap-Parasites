package srparasites_traps.features.obsidian_blocks;

import net.minecraft.block.BlockLadder;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class ObsidianLadderBlock extends BlockLadder {
    public static final String REGISTRY_NAME = "obsidian_ladder";

    public ObsidianLadderBlock() {
        super();

        this.setRegistryName(SRParasitesTraps.MOD_ID, REGISTRY_NAME);
        this.setTranslationKey(getTranslationKeyFor(REGISTRY_NAME));
        this.setHardness(50);
        this.setResistance(2000);

        if (ForgeConfigHandler.obsidianBlocks.ENABLE_OBSIDIAN_BLOCKS)
            this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }
}
