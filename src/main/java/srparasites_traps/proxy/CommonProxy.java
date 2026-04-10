package srparasites_traps.proxy;

import net.minecraft.item.Item;

public abstract class CommonProxy {

    public abstract void init();

    public abstract void registerItemRenderer(Item item, int meta, String id);
}