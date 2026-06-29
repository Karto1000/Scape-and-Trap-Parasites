package srparasites_traps.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class SlowdownHandler {
    private static final UUID SLOWDOWN_UUID = UUID.fromString("7d35827c-69bc-4e75-9f4f-120d4d303bc7");
    private static final Map<Integer, Integer> trackedEntities = new HashMap<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(SlowdownHandler.class);
    }

    public static void applySlowdown(EntityLivingBase entity, double amount) {
        IAttributeInstance speedAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        if (speedAttribute.getModifier(SLOWDOWN_UUID) == null) {
            AttributeModifier modifier = new AttributeModifier(SLOWDOWN_UUID, "Block Slowdown", -amount, 1);
            speedAttribute.applyModifier(modifier);
        }

        trackedEntities.put(entity.getEntityId(), 5);
    }

    @SubscribeEvent
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLivingBase)) return;
        if (entity.world.isRemote) return;

        EntityLivingBase livingEntity = (EntityLivingBase) entity;
        int entityId = entity.getEntityId();

        if (!trackedEntities.containsKey(entityId)) return;
        int ticksLeft = trackedEntities.get(entityId) - 1;

        if (ticksLeft <= 0) {
            IAttributeInstance speedAttribute = livingEntity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (speedAttribute.getModifier(SLOWDOWN_UUID) != null) speedAttribute.removeModifier(SLOWDOWN_UUID);
            trackedEntities.remove(entityId);
            return;
        }

        trackedEntities.put(entityId, ticksLeft);
    }
}
