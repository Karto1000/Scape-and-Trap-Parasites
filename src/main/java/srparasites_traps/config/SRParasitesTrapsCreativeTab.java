package srparasites_traps.config;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import srparasites_traps.registry.ModItems;

import javax.annotation.Nonnull;

public class SRParasitesTrapsCreativeTab extends CreativeTabs {
    public SRParasitesTrapsCreativeTab() {
        super("scape_and_trap_parasites");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.DISPATCHER_CEREBRUM);
    }
}
