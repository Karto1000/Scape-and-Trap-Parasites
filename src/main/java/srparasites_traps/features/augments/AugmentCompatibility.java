package srparasites_traps.features.augments;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import srparasites_traps.features.proximity_sensor.ProximitySensorTileEntity;
import srparasites_traps.features.relocator.RelocatorTileEntity;
import srparasites_traps.features.sentry_turret.SentryTurretTileEntity;
import srparasites_traps.features.tesla_coil.TeslaCoilTileEntity;

import java.util.*;

public final class AugmentCompatibility {
    private static final Map<Class<? extends TileEntity>, List<Class<? extends Item>>> VALID_AUGMENTS = new HashMap<>();

    static {
        VALID_AUGMENTS.put(
                SentryTurretTileEntity.class, Arrays.asList(
                        AttackSpeedAugment.class,
                        DamageAugment.class,
                        RangeAugment.class,
                        TargetingAugment.class
                )
        );

        VALID_AUGMENTS.put(
                TeslaCoilTileEntity.class, Arrays.asList(
                        AttackSpeedAugment.class,
                        DamageAugment.class,
                        RangeAugment.class,
                        TargetingAugment.class
                )
        );

        VALID_AUGMENTS.put(
                RelocatorTileEntity.class, Arrays.asList(
                        AttackSpeedAugment.class,
                        TargetingAugment.class
                )
        );

        VALID_AUGMENTS.put(
                ProximitySensorTileEntity.class, Collections.singletonList(
                        TargetingAugment.class
                )
        );
    }

    private AugmentCompatibility() {
    }

    public static boolean isValidFor(
            Class<? extends TileEntity> tileEntityClass,
            ItemStack stack
    ) {
        if (stack.isEmpty()) return false;

        List<Class<? extends Item>> validAugments = VALID_AUGMENTS.get(tileEntityClass);
        if (validAugments == null) return false;

        Item item = stack.getItem();

        for (Class<? extends Item> augmentClass : validAugments) {
            if (augmentClass.isInstance(item)) {
                return true;
            }
        }

        return false;
    }
}
