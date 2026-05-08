package com.moskowitz.realisticcruelty;

import com.moskowitz.realisticcruelty.event.ClientGoreEvent;
import com.moskowitz.realisticcruelty.event.GoreEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

public class CruelConfig {
    public static final ModConfigSpec client;
    public static final ModConfigSpec common;
    
    public static final ModConfigSpec.ConfigValue<Float> bloodAmount;
    public static final ModConfigSpec.ConfigValue<Double> bloodSpeed;
    public static final ModConfigSpec.ConfigValue<Double> bloodSpread;
    public static final ModConfigSpec.ConfigValue<Boolean> splatEnable;
    public static final ModConfigSpec.ConfigValue<Float> fogSize;
    public static final ModConfigSpec.ConfigValue<Integer> splashAmount;
    public static final ModConfigSpec.ConfigValue<Float> soundVolume;
    public static final ModConfigSpec.ConfigValue<Integer> clientMelee;
    public static final ModConfigSpec.ConfigValue<Integer> clientTrident;
    public static final ModConfigSpec.ConfigValue<Boolean> serverEnable;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("particle");
        builder.push("blood");
        bloodAmount = builder.define("amount", 1.0F);
        bloodSpeed = builder.define("speed", 0.3D);
        bloodSpread = builder.define("spread", 60.0D);
        builder.pop();
        
        builder.push("splat");
        splatEnable = builder.define("enable", true);
        builder.pop();
        
        builder.push("fog");
        fogSize = builder.define("size", 0.5F);
        builder.pop();
        
        builder.push("splash");
        splashAmount = builder.define("amount", 3);
        builder.pop();
        
        soundVolume = builder.define("soundVolume", 1.0F);
        builder.pop();
        
        builder.push("clientOnlyGore");
        clientMelee = builder.define("melee", 0);
        clientTrident = builder.define("trident", 0);
        builder.pop();
        client = builder.build();

        builder = new ModConfigSpec.Builder();
        serverEnable = builder.define("enable", true);
        common = builder.build();
    }

    public static void register(IEventBus modEventBus) {
        // Registering to the mod event bus ensures the config events are captured
        modEventBus.addListener(CruelConfig::onLoad);
    }

    // This method is called from your main CruelMod constructor
    public static void registerConfigs(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, client);
        container.registerConfig(ModConfig.Type.COMMON, common);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        ModConfig config = event.getConfig();
        if (config.getModId().equals(CruelMod.MOD_ID)) {
            if (config.getType() == ModConfig.Type.CLIENT) {
                // Registering client-side gore events to the NeoForge bus
                if (CruelConfig.clientMelee.get() >= 0) {
                    NeoForge.EVENT_BUS.addListener(ClientGoreEvent::onPlayerAttackTarget);
                }
                if (CruelConfig.clientTrident.get() >= 0) {
                    NeoForge.EVENT_BUS.addListener(ClientGoreEvent::onProjectileImpact);
                }
            } else if (config.getType() == ModConfig.Type.COMMON) {
                // Registering server/common events
                if (CruelConfig.serverEnable.get()) {
                    NeoForge.EVENT_BUS.register(GoreEvent.class);
                }
            }
        }
    }
}
