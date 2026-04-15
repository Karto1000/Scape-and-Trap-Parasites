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

    @Config.Name("Relocator")
    @Config.Comment("Settings for the relocator")
    public static final RelocatorConfig relocator = new RelocatorConfig();

    @Config.Name("Common")
    @Config.Comment("Settings that are common to all mods")
    public static final CommonConfig common = new CommonConfig();

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

        @Config.Name("Default sentry turret energy per tick (RF)")
        @Config.Comment("The amount of energy that a sentry turret uses per tick")
        public int DEFAULT_SENTRY_TURRET_ENERGY_PER_TICK = 50;

        @Config.Name("Default sentry turret respawn time after dying (Seconds)")
        @Config.Comment("The time that the sentry takes before being able to be deployed again")
        public int DEFAULT_SENTRY_TURRET_RESPAWN_TIME = 10;

        @Config.Name("Default sentry turret emerge time (Seconds)")
        @Config.Comment("The time that the sentry turret takes to emerge from the ground")
        public double DEFAULT_SENTRY_TURRET_EMERGE_TIME = 5.1F;
    }

    public static class RelocatorConfig {
        @Config.Name("Enable relocator")
        @Config.Comment("If true, the relocator will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE_RELOCATOR = true;

        @Config.Name("Default relocator relocation delay (Ticks)")
        @Config.Comment("The time in ticks between each possible relocation by a relocator")
        public int DEFAULT_RELOCATOR_RELOCATION_DELAY = 100;

        @Config.Name("Default relocation marker search volume (Blocks)")
        @Config.Comment("The search area (x * y * z) that the relocation marker can be set to")
        public int DEFAULT_RELOCATION_MARKER_MAX_SEARCH_VOLUME = 200;

        @Config.Name("Default relocation marker destination volume (Blocks)")
        @Config.Comment("The destination area (x * y * z) that the relocation marker can be set to")
        public int DEFAULT_RELOCATION_MARKER_MAX_DESTINATION_VOLUME = 200;

        @Config.Name("Default relocator entity emerge time (Seconds)")
        @Config.Comment("The time that relocator entity takes to emerge from the ground")
        public double DEFAULT_RELOCATOR_EMERGE_TIME = 4.0;

        @Config.Name("Default relocator entity max health")
        @Config.Comment("The max health of the relocator entity")
        public int DEFAULT_RELOCATOR_MAX_HEALTH = 30;

        @Config.Name("Default relocator block selection retries")
        @Config.Comment("The amount of times the relocator tries to find a valid block to relocate an entity to")
        public int DEFAULT_RELOCATOR_BLOCK_SELECTION_RETRIES = 10;

        @Config.Name("Default relocator block selection max block hardness")
        @Config.Comment("The max hardness that a block can have to be considered valid for a relocator to spawn")
        public int DEFAULT_RELOCATOR_MAX_BLOCK_HARDNESS = 5;

        @Config.Name("Default relocator max relocators in reserve")
        @Config.Comment("The max amount of relocators that can be spawned after one another")
        public int DEFAULT_RELOCATOR_MAX_RELOCATORS_IN_RESERVE = 2;

        @Config.Name("Default relocator max relocator spawn delay (Ticks)")
        @Config.Comment("The time in ticks between each possible relocator spawn")
        public int DEFAULT_RELOCATOR_RELOCATOR_CREATE_DELAY = 400;

        @Config.Name("Default relocator max energy (RF)")
        @Config.Comment("The maximum amount of energy that a relocation turret can hold")
        public int DEFAULT_RELOCATOR_MAX_ENERGY = 10000;

        @Config.Name("Default sentry turret max biomass (mB)")
        @Config.Comment("The maximum amount of biomass that a relocation turret can hold")
        public int DEFAULT_RELOCATOR_MAX_BIOMASS = 1000;

        @Config.Name("Default relocator energy per tick (RF)")
        @Config.Comment("The amount of energy that a relocation turret uses per tick")
        public int DEFAULT_RELOCATOR_ENERGY_PER_TICK = 25;

        @Config.Name("Default relocator biomass per relocator spawn (mB)")
        @Config.Comment("The amount of biomass that a relocator requires to spawn")
        public int DEFAULT_RELOCATOR_BIOMASS_FOR_SPAWN = 100;
    }

    public static class CommonConfig {
        @Config.Name("Debug Mode")
        public boolean DEBUG_MODE = false;

        @Config.Name("Sentry flesh drop chance (%)")
        @Config.Comment("The chance that a sentry will drop sentry flesh")
        public int SENTRY_FLESH_DROP_CHANCE = 20;

        @Config.Name("Sentry flesh max drop amount")
        @Config.Comment("The max amount of sentry flesh dropped by a sentry")
        public int MAX_SENTRY_FLESH_DROP_AMOUNT = 2;
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