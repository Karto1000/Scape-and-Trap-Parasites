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

    @Config.Name("Serrated Spikes")
    @Config.Comment("Settings for the serrated spikes")
    public static final SerratedSpikesConfig serratedSpikes = new SerratedSpikesConfig();

    @Config.Name("Biomass Factory")
    @Config.Comment("Settings for the biomass factory")
    public static final BiomassFactoryConfig biomassFactory = new BiomassFactoryConfig();

    @Config.Name("Decontaminator")
    @Config.Comment("Settings for the decontaminator")
    public static final DecontaminatorConfig decontaminator = new DecontaminatorConfig();

    @Config.Name("Obsidian Blocks")
    @Config.Comment("Settings for the obsidian blocks")
    public static final ObsidianBlocksConfig obsidianBlocks = new ObsidianBlocksConfig();

    @Config.Name("Tesla Coil")
    @Config.Comment("Settings for the tesla coil")
    public static final TeslaCoilConfig teslaCoil = new TeslaCoilConfig();

    @Config.Name("Proximity Sensor")
    @Config.Comment("Settings for the proximity sensor")
    public static final ProximitySensorConfig proximitySensor = new ProximitySensorConfig();

    @Config.Name("Hardness Analyzer")
    @Config.Comment("Settings for the hardness analyzer")
    public static final HardnessAnalyzerConfig hardnessAnalyzer = new HardnessAnalyzerConfig();

    @Config.Name("Augments")
    @Config.Comment("Settings for the augment system")
    public static final AugmentConfig augments = new AugmentConfig();

    @Config.Name("Common")
    @Config.Comment("Common settings for the mod")
    public static final CommonConfig common = new CommonConfig();

    public static class SentryTurretConfig {
        @Config.Name("Enable sentry turret")
        @Config.Comment("If true, the sentry turret will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Default attack delay (Ticks)")
        @Config.Comment("The time in ticks between each attack of a sentry turret")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ATTACK_DELAY = 20;

        @Config.Name("Default range (Blocks)")
        @Config.Comment("The range in blocks that a sentry turret can attack")
        @Config.RangeDouble(min = 0.0)
        public double DEFAULT_RANGE = 16;

        @Config.Name("Default projectile parasite resistance reduction chance (%)")
        @Config.Comment("The chance that the projectile the sentry shoots will reduce the resistance of the parasite it hits")
        @Config.RangeInt(min = 0, max = 100)
        public int DEFAULT_RESISTANCE_REDUCE_CHANCE = 25;

        @Config.Name("Default projectile parasite resistance reduction amount")
        @Config.Comment("The amount of resistance that the projectile the sentry shoots will reduce the parasite it hits")
        @Config.RangeInt(min = 0)
        public int DEFAULT_RESISTANCE_REDUCTION_AMOUNT = 4;

        @Config.Name("Default projectile damage")
        @Config.Comment("The damage that the projectile the sentry shoots will deal to the entity it hits")
        @Config.RangeDouble(min = 0.0)
        public float DEFAULT_DAMAGE = 20;

        @Config.Name("Apply Poison")
        @Config.Comment("Whether or not the projectile the sentry shoots will poison the entity it hits")
        public boolean APPLY_POISON = false;

        @Config.Name("Default projectile poison duration (Ticks)")
        @Config.Comment("The duration in ticks that the projectile the sentry shoots will poison the entity it hits")
        @Config.RangeInt(min = 0)
        public int DEFAULT_POISON_DURATION = 100;

        @Config.Name("Default projectile poison amplifier")
        @Config.Comment("The amplifier that the projectile the sentry shoots will poison the entity it hits (0 for none)")
        @Config.RangeInt(min = 0)
        public int DEFAULT_POISON_AMPLIFIER = 1;

        @Config.Name("Default max energy (RF)")
        @Config.Comment("The maximum amount of energy that a sentry turret can hold")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MAX_ENERGY = 10000;

        @Config.Name("Default max biomass (mB)")
        @Config.Comment("The maximum amount of biomass that a sentry turret can hold")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MAX_BIOMASS = 2000;

        @Config.Name("Default biomass per shot (mB)")
        @Config.Comment("The amount of biomass that a sentry turret uses per shot")
        @Config.RangeInt(min = 0)
        public int DEFAULT_BIOMASS_PER_SHOT = 20;

        @Config.Name("Default energy per shot (RF)")
        @Config.Comment("The amount of energy that a sentry turret uses per shot")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ENERGY_PER_SHOT = 1000;

        @Config.Name("Default entity respawn time after dying (Seconds)")
        @Config.Comment("The time that the sentry entity takes before being able to be deployed again")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ENTITY_RESPAWN_TIME = 10;

        @Config.Name("Default entity emerge time (Seconds)")
        @Config.Comment("The time that the sentry entity turret takes to emerge from the ground")
        @Config.RangeDouble(min = 0.0)
        public double DEFAULT_ENTITY_EMERGE_TIME = 5.1F;

        @Config.Name("Default entity health (Hearts)")
        @Config.Comment("The health of the sentry turret entity")
        @Config.RangeDouble(min = 0.0)
        public double DEFAULT_ENTITY_HEALTH = 40;

        @Config.Name("Default entity armor")
        @Config.Comment("The armor of the sentry turret entity")
        @Config.RangeDouble(min = 0.0)
        public double DEFAULT_ENTITY_ARMOR = 5.0;

        @Config.Name("Default entity heal interval (Ticks)")
        @Config.Comment("The interval between each heal")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ENTITY_HEAL_INTERVAL = 20;

        @Config.Name("Default entity heal amount (Hearts)")
        @Config.Comment("The amount of health that the sentry turret entity heals")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ENTITY_HEAL_AMOUNT = 2;
    }

    public static class RelocatorConfig {
        @Config.Name("Enable relocator")
        @Config.Comment("If true, the relocator will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Default area marker volume (Blocks)")
        @Config.Comment("The maximum area (x * y * z) that the relocation marker can be set to")
        @Config.RangeInt(min = 0)
        public int DEFAULT_AREA_MARKER_MAX_VOLUME = 200;

        @Config.Name("Default relocator entity emerge time (Seconds)")
        @Config.Comment("The time that relocator entity takes to emerge from the ground")
        @Config.RangeDouble(min = 0.0)
        public double DEFAULT_ENTITY_EMERGE_TIME = 1.0;

        @Config.Name("Default relocator entity max health")
        @Config.Comment("The max health of the relocator entity")
        @Config.RangeInt(min = 1)
        public int DEFAULT_ENTITY_MAX_HEALTH = 30;

        @Config.Name("Default block selection retries")
        @Config.Comment("The amount of times the relocator tries to find a valid block to relocate an entity to")
        @Config.RangeInt(min = 1)
        public int DEFAULT_BLOCK_SELECTION_RETRIES = 10;

        @Config.Name("Default block selection max block hardness")
        @Config.Comment("The max hardness that a block can have to be considered valid for a relocator to spawn")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MAX_BLOCK_HARDNESS = 5;

        @Config.Name("Default max relocators in reserve")
        @Config.Comment("The max amount of relocators that can be spawned after one another")
        @Config.RangeInt(min = 1)
        public int DEFAULT_MAX_RELOCATORS_IN_RESERVE = 2;

        @Config.Name("Default max relocator spawn delay (Ticks)")
        @Config.Comment("The time in ticks between each possible relocator spawn")
        @Config.RangeInt(min = 0)
        public int DEFAULT_RELOCATOR_CREATE_DELAY = 400;

        @Config.Name("Default max energy (RF)")
        @Config.Comment("The maximum amount of energy that a relocation turret can hold")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MAX_ENERGY = 10000;

        @Config.Name("Default max biomass (mB)")
        @Config.Comment("The maximum amount of biomass that a relocation turret can hold")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MAX_BIOMASS = 1000;

        @Config.Name("Default energy per tick (RF)")
        @Config.Comment("The amount of energy that a relocation turret uses per tick")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ENERGY_PER_TICK = 25;

        @Config.Name("Default biomass per relocator entity spawn (mB)")
        @Config.Comment("The amount of biomass that a relocator requires to spawn")
        @Config.RangeInt(min = 0)
        public int DEFAULT_BIOMASS_FOR_SPAWN = 100;

        @Config.Name("Default max area distance (Blocks)")
        @Config.Comment("The max distance the area can be away from the source block")
        @Config.RangeInt(min = 0)
        public final int DEFAULT_MAX_AREA_DISTANCE = 20;
    }

    public static class SerratedSpikesConfig {
        @Config.Name("Enable serrated spikes")
        @Config.Comment("If true, the serrated spikes will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Default damage")
        @Config.Comment("The damage that the serrated spikes deal to the entity they hit")
        @Config.RangeDouble(min = 0.0)
        public float DEFAULT_DAMAGE = 2;

        @Config.Name("Default damage threshold")
        @Config.Comment("The combined motion on the x,y and z axis that the entity must have to be damaged by the spikes")
        @Config.RangeDouble(min = 0.0)
        public float DEFAULT_SERRATED_SPIKES_DAMAGE_MOVE_THRESHOLD = 0.1F;

        @Config.Name("Default move sum multiplier")
        @Config.Comment("Multiplies motion sum that reduces the invulnerability time of the entity")
        @Config.RangeDouble(min = 0.0)
        public float DEFAULT_INVULNERABILITY_REDUCTION_MULTIPLIER = 15.0F;

        @Config.Name("Default slow down amount")
        @Config.Comment("The amount of motion that the entity will lose when the spikes hit it 0.1 -> lose 90% of movement speed")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public float DEFAULT_SLOW_DOWN_AMOUNT = 0.9F;

        @Config.Name("Default min hurt resistance time (Ticks)")
        @Config.Comment("The time in ticks that the hurtResistance has to be lower than to cause more damage")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MIN_HURT_RESISTANT_TIME = 10;

        @Config.Name("Deal bleeding damage?")
        @Config.Comment("If true, the serrated spikes will deal bleeding damage to the entity they hit")
        public boolean BLEEDING_DAMAGE = true;

        @Config.Name("Default bleeding duration (Ticks)")
        @Config.Comment("The duration in ticks that the entity will be bleeding")
        @Config.RangeInt(min = 0)
        public int DEFAULT_BLEEDING_DURATION = 80;

        @Config.Name("Default burning serrated spikes flame duration (Seconds)")
        @Config.Comment("The duration in seconds that the entity will be on fire")
        @Config.RangeInt(min = 0)
        public int DEFAULT_FLAME_DURATION = 5;

        @Config.Name("Default viral serrated spikes viral effect duration (Ticks)")
        @Config.Comment("The duration in ticks that the entity will have the viral effect")
        @Config.RangeInt(min = 0)
        public int DEFAULT_VIRAL_EFFECT_DURATION = 80;

        @Config.Name("Default viral serrated spikes viral effect amplifier")
        @Config.Comment("The amplifier level of the viral effect")
        @Config.RangeInt(min = 0)
        public int DEFAULT_VIRAL_AMPLIFIER = 0;
    }

    public static class BiomassFactoryConfig {
        @Config.Name("Enable biomass factory")
        @Config.Comment("If true, the biomass factory will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Default max biomass storage (mB)")
        @Config.Comment("The maximum amount of biomass that a biomass factory can store")
        @Config.RangeInt(min = 0)
        public int DEFAULT_MAX_BIOMASS = 1000;

        @Config.Name("Default item consume delay (Ticks)")
        @Config.Comment("The amount of ticks between each item consumption")
        @Config.RangeInt(min = 0)
        public int DEFAULT_CONSUME_DELAY = 2;
    }

    public static class DecontaminatorConfig {
        @Config.Name("Enable decontaminator")
        @Config.Comment("If true, the decontaminator will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Default max spray capacity")
        @Config.Comment("The amount of spray charges the decontaminator can hold")
        @Config.RangeInt(min = 0)
        public int DEFAULT_SPRAY_CAPACITY = 2;

        @Config.Name("Default spray charge duration (Ticks)")
        @Config.Comment("The amount of ticks that a decontaminator waits before spraying again")
        @Config.RangeInt(min = 0)
        public int DEFAULT_SPRAY_COOLDOWN_TICKS = 20;

        @Config.Name("Default open duration (Ticks)")
        @Config.Comment("The amount of ticks that a decontaminator takes to open")
        @Config.RangeInt(min = 0)
        public int DEFAULT_OPEN_DURATION_TICKS = 20;

        @Config.Name("Default close duration (Ticks)")
        @Config.Comment("The amount of ticks that a decontaminator takes to close")
        @Config.RangeInt(min = 0)
        public int DEFAULT_CLOSE_DURATION_TICKS = 20;

        @Config.Name("Default capacity regeneration time (Ticks)")
        @Config.Comment("The amount of ticks that it takes for the decontaminator to regenerate one charge of its capacity")
        @Config.RangeInt(min = 0)
        public int DEFAULT_CAPACITY_REGEN_TIME_TICKS = 200;
    }

    public static class ObsidianBlocksConfig {
        @Config.Name("Enable obsidian blocks")
        @Config.Comment("If true, the obsidian blocks will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;
    }

    public static class TeslaCoilConfig {
        @Config.Name("Enable Tesla coil")
        @Config.Comment("If true, the Tesla coil will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Enable static electricity generator")
        @Config.Comment("If true, the static electricity generator will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE_STATIC_ELECTRICITY_GENERATOR = true;

        @Config.Name("Default max energy (RF)")
        @Config.Comment("The maximum amount of energy that a Tesla coil can hold")
        @Config.RangeInt(min = 0)
        public int DEFAULT__MAX_ENERGY = 10000;

        @Config.Name("Default energy per shot (RF)")
        @Config.Comment("The amount of energy consumed by a Tesla coil per shot")
        @Config.RangeInt(min = 0)
        public int DEFAULT_ENERGY_PER_SHOT = 1000;

        @Config.Name("Default range (Blocks)")
        @Config.Comment("The range the Tesla coil can detect enemies")
        @Config.RangeInt(min = 0)
        public int DEFAULT_RANGE = 10;

        @Config.Name("Default fire delay (Ticks)")
        @Config.Comment("The time between each shot")
        @Config.RangeInt(min = 0)
        public int DEFAULT_FIRE_DELAY = 50;

        @Config.Name("Default charging delay (Ticks)")
        @Config.Comment("The time in ticks the Tesla coil has to charge up before shooting after finding an enemy")
        @Config.RangeInt(min = 0)
        public final int DEFAULT_CHARGING_DELAY = 5;

        @Config.Name("Default shocked arc chance (%)")
        @Config.Comment("The chance that an enemy hit by the Tesla coil will arc lightning to other nearby enemies")
        @Config.RangeInt(min = 0, max = 100)
        public int DEFAULT_SHOCKED_CHANCE_TO_ARC = 50;

        @Config.Name("Default shocked arc range (Blocks)")
        @Config.Comment("The range that the lightning arc can reach")
        @Config.RangeInt(min = 0)
        public int DEFAULT_SHOCKED_ARC_RANGE = 4;

        @Config.Name("Default shocked arc damage")
        @Config.Comment("The amount of damage dealt by a lightning arc, is multiplied by the amplifier of the shocked effect")
        @Config.RangeDouble(min = 0)
        public double DEFAULT_SHOCKED_ARC_DAMAGE = 3;

        @Config.Name("Default coil shocked arc amplifier")
        @Config.Comment("The amplifier of the shocked effect. The first enemy that is hit by the Tesla coil arc will have this amplifier. Each subsequent enemy that is hit in that chain will have an amplifier of this value - 1. The higher the amplifier, the more damage the lightning arc will deal and the further it will reach.")
        @Config.RangeInt(min = 0)
        public int DEFAULT_SHOCKED_ARC_AMPLIFIER = 3;

        @Config.Name("Default shocked arc jump limit")
        @Config.Comment("The maximum number of entities that can be hit by an electrified entity.")
        @Config.RangeInt(min = 0)
        public final int DEFAULT_SHOCKED_JUMP_LIMIT = 2;
    }

    public static class ProximitySensorConfig {
        @Config.Name("Enable proximity sensor")
        @Config.Comment("If true, the proximity sensor will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Default max area distance (Blocks)")
        @Config.Comment("The max distance the area from an area marker can be away from the source block")
        @Config.RangeInt(min = 0)
        public final int DEFAULT_MAX_AREA_DISTANCE = 20;
    }

    public static class HardnessAnalyzerConfig {
        @Config.Name("Enable hardness analyzer")
        @Config.Comment("If true, the hardness analyzer will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Gui text offset X (Px)")
        @Config.Comment("The X offset of the text in the hardness analyzer GUI")
        @Config.RangeInt(min = 0)
        public int GUI_TEXT_OFFSET_X = 5;

        @Config.Name("Gui text offset Y (Px)")
        @Config.Comment("The Y offset of the text in the hardness analyzer GUI")
        @Config.RangeInt(min = 0)
        public int GUI_TEXT_OFFSET_Y = 5;
    }

    public static class AugmentConfig {
        @Config.Name("Enable augment system")
        @Config.Comment("If true, the augment system will be enabled")
        @Config.RequiresMcRestart
        public boolean ENABLE = true;

        @Config.Name("Sentry turret range increase (Blocks)")
        @Config.Comment("The increase in range for the sentry turret in blocks when a range augment is applied")
        @Config.RangeInt(min = 0)
        public int SENTRY_TURRET_RANGE_INCREASE = 8;

        @Config.Name("Sentry turret attack speed increase (Ticks)")
        @Config.Comment("The increase in attack speed for the sentry turret in ticks when a speed augment is applied")
        @Config.RangeInt(min = 0)
        public int SENTRY_TURRET_ATTACK_SPEED_INCREASE = 5;

        @Config.Name("Sentry turret augment slots")
        @Config.Comment("The number of augment slots available for the sentry turret")
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int SENTRY_TURRET_AUGMENT_SLOTS = 3;

        @Config.Name("Sentry turret damage increase (Hearts)")
        @Config.Comment("The increase in damage for the sentry turret when a damage augment is applied")
        @Config.RangeInt(min = 0)
        public int SENTRY_TURRET_DAMAGE_INCREASE = 5;

        @Config.Name("Tesla coil augment slots")
        @Config.Comment("The number of augment slots available for the Tesla coil")
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int TESLA_COIL_AUGMENT_SLOTS = 2;

        @Config.Name("Tesla coil attack speed increase (Ticks)")
        @Config.Comment("The increase in attack speed for the Tesla coil when an attack speed augment is applied")
        @Config.RangeInt(min = 0)
        public int TESLA_COIL_ATTACK_SPEED_INCREASE = 20;

        @Config.Name("Tesla coil range increase (Blocks)")
        @Config.Comment("The increase in range for the Tesla coil in blocks when a range augment is applied")
        @Config.RangeInt(min = 0)
        public int TESLA_COIL_RANGE_INCREASE = 6;

        @Config.Name("Tesla coil shocked amplifier increase")
        @Config.Comment("The increase in amplifier for the shocked effect when a damage augment is applied")
        @Config.RangeInt(min = 0)
        public int TESLA_COIL_SHOCKED_AMPLIFIER_INCREASE = 2;
    }

    public static class CommonConfig {
        @Config.Name("Debug Mode")
        public boolean DEBUG_MODE = false;

        @Config.Name("Parasitic matter drop chance (%)")
        @Config.Comment("The chance that a sentry or seizer will drop parasitic matter")
        @Config.RangeInt(min = 0, max = 100)
        public int PARASITIC_MATTER_DROP_CHANCE = 20;

        @Config.Name("Parasitic matter max drop amount")
        @Config.Comment("The max amount of parasitic matter dropped")
        @Config.RangeInt(min = 0)
        public int MAX_PARASITIC_MATTER_DROP_AMOUNT = 2;

        @Config.Name("Dispatcher cerebrum drop chance (%)")
        @Config.Comment("The chance that a dispatcher will drop its cerebrum")
        @Config.RangeInt(min = 0, max = 100)
        public int DISPATCHER_CEREBRUM_DROP_CHANCE = 20;

        @Config.Name("Dispatcher cerebrum max drop amount")
        @Config.Comment("The max amount of cerebrums dropped by a dispatcher")
        @Config.RangeInt(min = 0)
        public int MAX_DISPATCHER_CEREBRUM_DROP_AMOUNT = 1;

        @Config.Name("Dispatcher brainstem drop chance (%)")
        @Config.Comment("The chance that a dispatcher will drop its brainstem")
        @Config.RangeInt(min = 0, max = 100)
        public int DISPATCHER_BRAINSTEM_DROP_CHANCE = 20;

        @Config.Name("Dispatcher brainstem max drop amount")
        @Config.Comment("The max amount of brainstems dropped by a dispatcher")
        @Config.RangeInt(min = 0)
        public int MAX_DISPATCHER_BRAINSTEM_DROP_AMOUNT = 1;

        @Config.Name("Beckon heart drop chance (%)")
        @Config.Comment("The chance that a beckon will drop a heart")
        @Config.RangeInt(min = 0, max = 100)
        public int BECKON_HEART_DROP_CHANCE = 20;

        @Config.Name("Beckon heart max drop amount")
        @Config.Comment("The max amount of hearts dropped by a beckon")
        @Config.RangeInt(min = 0)
        public int MAX_BECKON_HEART_DROP_AMOUNT = 1;
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