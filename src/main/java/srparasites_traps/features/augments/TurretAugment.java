package srparasites_traps.features.augments;

import cofh.api.item.IAugmentItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.features.IExtendedAugmentable;
import srparasites_traps.registry.ModTileEntities;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static srparasites_traps.util.Translation.getTranslationKeyFor;

public class TurretAugment extends Item implements IAugmentItem {
    public final String registryName;

    public TurretAugment(String registryName) {
        super();

        this.registryName = registryName;

        this.setRegistryName(SRParasitesTraps.MOD_ID, registryName);
        this.setTranslationKey(getTranslationKeyFor(registryName));
        if (ForgeConfigHandler.augments.ENABLE) this.setCreativeTab(SRParasitesTraps.CREATIVE_TAB);
    }

    @Override
    public AugmentType getAugmentType(ItemStack itemStack) {
        return AugmentType.BASIC;
    }

    @Override
    public String getAugmentIdentifier(ItemStack itemStack) {
        return this.registryName;
    }

    @Override
    public void addInformation(
            @Nonnull ItemStack stack,
            @Nullable World worldIn,
            List<String> tooltip,
            @Nonnull ITooltipFlag flagIn
    ) {
        String machinesWithAugments = ModTileEntities.TILE_ENTITIES.stream()
                .filter(t -> {
                    boolean inheritsAugmentable = IExtendedAugmentable.class.isAssignableFrom(t.tileEntityClass);
                    if (!inheritsAugmentable) return false;
                    return AugmentCompatibility.isValidFor(t.tileEntityClass, stack);
                })
                .map(t -> String.format("- %s", t.block.getLocalizedName()))
                .collect(Collectors.joining("\n"));

        Translation.addMultilineTooltip(tooltip, "item." + this.registryName);
        Translation.addMultilineTooltip(tooltip, "item.augment_general", machinesWithAugments);
    }
}
