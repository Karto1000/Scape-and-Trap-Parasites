package srparasites_traps.config;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import srparasites_traps.registry.ModItems;

public class SRParasitesTrapsCreativeTab extends CreativeTabs {
    public SRParasitesTrapsCreativeTab() {
        super(I18n.format("tab.srparasites_traps.name"));
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.DISPATCHER_CEREBRUM);
    }
}
