package com.moskowitz.realisticcruelty.particle;

import com.moskowitz.realisticcruelty.CruelMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = CruelMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModParticle {
    // 1.21.1 uses Registries.PARTICLE_TYPE and DeferredHolder instead of RegistryObject
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, CruelMod.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD = REGISTER.register("blood", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_SPLAT = REGISTER.register("blood_splat", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_FOG = REGISTER.register("blood_fog", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_SPLASH = REGISTER.register("blood_splash", () -> new SimpleParticleType(true));

    // Helper to register the DeferredRegister to the mod bus
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticle.BLOOD.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(ModParticle.BLOOD_SPLAT.get(), BloodSplatParticle.Provider::new);
        event.registerSpriteSet(ModParticle.BLOOD_FOG.get(), BloodFogParticle.Provider::new);
        event.registerSpriteSet(ModParticle.BLOOD_SPLASH.get(), BloodSplashParticle.Provider::new);
    }
}
