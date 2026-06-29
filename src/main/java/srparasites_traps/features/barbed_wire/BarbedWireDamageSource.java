package srparasites_traps.features.barbed_wire;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import srparasites_traps.util.Translation;

import javax.annotation.Nonnull;

public class BarbedWireDamageSource extends DamageSource {
    public static final String NAME = "barbed_wire";

    public BarbedWireDamageSource() {
        super(NAME);
    }

    @Nonnull
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
        return new TextComponentString(Translation.getDamageSourceFor(NAME, entityLivingBaseIn.getName()));
    }
}
