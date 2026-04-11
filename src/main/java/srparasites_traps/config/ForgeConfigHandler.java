package srparasites_traps.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import srparasites_traps.SRParasitesTraps;

@Config(modid = SRParasitesTraps.MOD_ID)
public class ForgeConfigHandler {

    @Config.Name("Sentry Turret")
    @Config.Comment("Settings for the sentry turret")
    public static final SentryTurretConfig sentry = new SentryTurretConfig();

    public static class SentryTurretConfig {
        @Config.Name("Enable sentry turret")
        @Config.Comment("If true, the sentry turret will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE_SENTRY_TURRET = true;

        @Config.Name("Default sentry turret attack delay (Ticks)")
        @Config.Comment("The time in ticks between each attack of a sentry turret")
        public int DEFAULT_SENTRY_TURRET_ATTACK_DELAY = 20;

        @Config.Name("Default sentry turret range (Blocks)")
        @Config.Comment("The range in blocks that a sentry turret can attack")
        public double DEFAULT_SENTRY_TURRET_RANGE = 16;

        @Config.Name("Default sentry turret projectile parasite resistance reduction chance (%)")
        @Config.Comment("The chance that the projectile the sentry shoots will reduce the resistance of the parasite it hits")
        public int DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCE_CHANCE = 25;

        @Config.Name("Default sentry turret projectile parasite resistance reduction amount")
        @Config.Comment("The amount of resistance that the projectile the sentry shoots will reduce the parasite it hits")
        public int DEFAULT_SENTRY_TURRET_RESISTANCE_REDUCTION_AMOUNT = 4;

        @Config.Name("Default sentry turret projectile damage")
        @Config.Comment("The damage that the projectile the sentry shoots will deal to the entity it hits")
        public float DEFAULT_SENTRY_TURRET_DAMAGE = 25;

        @Config.Name("Default sentry turret projectile poison duration (Ticks)")
        @Config.Comment("The duration in ticks that the projectile the sentry shoots will poison the entity it hits")
        public int DEFAULT_SENTRY_TURRET_POISON_DURATION = 100;

        @Config.Name("Default sentry turret projectile poison amplifier")
        @Config.Comment("The amplifier that the projectile the sentry shoots will poison the entity it hits (0 for none)")
        public int DEFAULT_SENTRY_TURRET_POISON_AMPLIFIER = 1;

        @Config.Name("Default sentry turret max energy (RF)")
        @Config.Comment("The maximum amount of energy that a sentry turret can hold")
        public int DEFAULT_SENTRY_TURRET_MAX_ENERGY = 10000;

        @Config.Name("Default sentry turret max biomass (mB)")
        @Config.Comment("The maximum amount of biomass that a sentry turret can hold")
        public int DEFAULT_SENTRY_TURRET_MAX_BIOMASS = 1000;

        @Config.Name("Default sentry turret biomass per shot (mB)")
        @Config.Comment("The amount of biomass that a sentry turret uses per shot")
        public int DEFAULT_SENTRY_TURRET_BIOMASS_PER_SHOT = 100;

        @Config.Name("Default sentry turret energy per shot (RF)")
        @Config.Comment("The amount of energy that a sentry turret uses per shot")
        public int DEFAULT_SENTRY_TURRET_ENERGY_PER_SHOT = 1000;

        @Config.Name("Default sentry turret biomass for spawn (mB)")
        @Config.Comment("The amount of biomass that a sentry turret spawns with")
        public int DEFAULT_SENTRY_TURRET_BIOMASS_FOR_SPAWN = 1000;

        @Config.Name("Default sentry turret respawn time after dying (Seconds)")
        @Config.Comment("The time that the sentry takes before being able to be deployed again")
        public int DEFAULT_SENTRY_TURRET_RESPAWN_TIME = 10;
    }

    @Mod.EventBusSubscriber(modid = SRParasitesTraps.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(SRParasitesTraps.MOD_ID)) {
                ConfigManager.sync(SRParasitesTraps.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}