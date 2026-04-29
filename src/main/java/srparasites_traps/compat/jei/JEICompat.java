package srparasites_traps.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.registry.ModBlocks;
import srparasites_traps.registry.ModItems;

@JEIPlugin
public class JEICompat implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IModPlugin.super.registerCategories(registry);
    }

    @Override
    public void register(IModRegistry registry) {
        if (!ForgeConfigHandler.sentry.ENABLE_SENTRY_TURRET) {
            IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.sentry_turret));
        }

        if (!ForgeConfigHandler.relocator.ENABLE_RELOCATOR) {
            IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.RELOCATOR));
        }

        if (!ForgeConfigHandler.serratedSpikes.ENABLE_SERRATED_SPIKES) {
            IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.SERRATED_SPIKES));
            return;
        }

        if (!ForgeConfigHandler.biomassFactory.ENABLE_BIOMASS_FACTORY) {
            IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.BIOMASS_FACTORY));
            return;
        }

        if (!ForgeConfigHandler.cleaner.ENABLE_CLEANER) {
            IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.CLEANER));
            return;
        }

        for (Item item : ModItems.getItemList()) {
            String infoKey = String.format("info.srparasites_traps.%s", item.getRegistryName().getPath());
            SRParasitesTraps.LOGGER.info("Adding info for {}", infoKey);
            if (I18n.hasKey(infoKey)) {
                registry.addIngredientInfo(
                        new ItemStack(item),
                        VanillaTypes.ITEM,
                        infoKey
                );
            }
        }

        IModPlugin.super.register(registry);
    }
}
