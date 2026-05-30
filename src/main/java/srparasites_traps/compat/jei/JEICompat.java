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
        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();

        if (!ForgeConfigHandler.sentry.ENABLE_SENTRY_TURRET) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.SENTRY_TURRET));
        }

        if (!ForgeConfigHandler.relocator.ENABLE_RELOCATOR) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.RELOCATOR));
        }

        if (!ForgeConfigHandler.serratedSpikes.ENABLE_SERRATED_SPIKES) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.SERRATED_SPIKES));
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.BURNING_SERRATED_SPIKES));
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.VIRAL_SERRATED_SPIKES));
        }

        if (!ForgeConfigHandler.biomassFactory.ENABLE_BIOMASS_FACTORY) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.BIOMASS_FACTORY));
        }

        if (!ForgeConfigHandler.decontaminator.ENABLE_DECONTAMINATOR) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.DECONTAMINATOR));
        }

        if (!ForgeConfigHandler.obsidianBlocks.ENABLE_OBSIDIAN_BLOCKS) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.OBSIDIAN_LADDER));
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.OBSIDIAN_SLAB));
        }

        if (!ForgeConfigHandler.teslaCoil.ENABLE_TESLA_COIL) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.TESLA_COIL));
            blacklist.addIngredientToBlacklist(ModItems.COIL);
        }

        if (!ForgeConfigHandler.proximitySensor.ENABLE_PROXIMITY_SENSOR) {
            blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.PROXIMITY_SENSOR));
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
