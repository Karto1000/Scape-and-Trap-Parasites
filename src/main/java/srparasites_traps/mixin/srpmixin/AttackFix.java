package srparasites_traps.mixin.srpmixin;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EntityParasiteBase.class)
public class AttackFix {
    @Inject(
            method = "canAttackClass",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    public void srparasites_traps_can_attack_class_fix(Class<? extends EntityLivingBase> cls, CallbackInfoReturnable<Boolean> cir) {
        // Because this function checks if an entity contains the name "srparasites" and our mod id is called "srparasites_traps".
        // The targeting function will always return false for any entity of this mod. To fix this, we need to check for it before.
        if (cls != EntityPlayer.class && cls != EntityPlayerMP.class) {
            try {
                String name = Objects.requireNonNull(EntityList.getKey(cls)).toString();
                if (name.startsWith("srparasites_traps")) cir.setReturnValue(true);
            } catch (Exception var4) {
                cir.setReturnValue(true);
            }
        }
    }
}
