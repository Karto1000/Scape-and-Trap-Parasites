package srparasites_traps.config;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ProximitySensorConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(
            JsonContext context,
            JsonObject json
    ) {
        return () -> ForgeConfigHandler.proximitySensor.ENABLE;
    }
}
