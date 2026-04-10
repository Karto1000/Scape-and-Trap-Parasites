package srparasites_traps.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import srparasites_traps.SRParasitesTraps;
import srparasites_traps.registry.ModItems;

@JEIPlugin
public class JEICompat implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IModPlugin.super.registerCategories(registry);
    }

    @Override
    public void register(IModRegistry registry) {
        for (Item item : ModItems.getItemList()) {
            String infoKey = String.format("info.srp_end_of_days.%s", item.getRegistryName().getPath());
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
