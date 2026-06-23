package srparasites_traps.features.serrated_spikes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class SerratedSpikesDamageSource extends DamageSource {
    public SerratedSpikesDamageSource() {
        super("serrated_spikes");
    }

    @Nonnull
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
        return new TextComponentString(entityLivingBaseIn.getName() + " died to a thousand cuts.");
    }
}
