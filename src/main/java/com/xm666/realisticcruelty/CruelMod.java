package com.moskowitz.realisticcruelty; // Updated to your name

import com.moskowitz.realisticcruelty.network.ModNetwork;
import com.moskowitz.realisticcruelty.particle.ModParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CruelMod.MOD_ID)
public class CruelMod {
    public static final String MOD_ID = "realistic_cruelty";

    // NeoForge 1.21.1 passes the event bus directly into the constructor
    public CruelMod(IEventBus modEventBus) {
        // 1. Register Config (Note: CruelConfig will need internal updates too)
        CruelConfig.register(modEventBus);

        // 2. Register Particles
        // In 1.21.1, we register the types to the bus
        ModParticle.REGISTER.register(modEventBus);

        // 3. Handle Client-Side Logic (Particles/Rendering)
        if (FMLEnvironment.dist == Dist.CLIENT) {
            // This is where you'll register particle providers later
            modEventBus.addListener(ModParticle::registerProviders);
        }

        // 4. Initialize Network
        // ModNetwork will need a total rewrite for PayloadRegistrar
        ModNetwork.register(modEventBus);

        // 5. Register the mod for game events (like hitting mobs)
        NeoForge.EVENT_BUS.register(this);
    }
}
