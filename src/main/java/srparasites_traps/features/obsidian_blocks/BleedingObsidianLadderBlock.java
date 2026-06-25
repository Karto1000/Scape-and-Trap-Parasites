package srparasites_traps.features.obsidian_blocks;

public class BleedingObsidianLadderBlock extends ObsidianLadderBlock {
    public static final String REGISTRY_NAME = "bleeding_obsidian_ladder";

    public BleedingObsidianLadderBlock() {
        super(REGISTRY_NAME);

        this.setHardness(2.3f);
        this.setHarvestLevel("pickaxe", 1);
    }
}
