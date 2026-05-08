package com.moskowitz.realisticcruelty.network; // Use your package

import com.moskowitz.realisticcruelty.CruelMod;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // The registrar handles versioning and channel setup automatically now
        final PayloadRegistrar registrar = event.registrar(CruelMod.MOD_ID)
                .versioned("1.0.0");

        // Register the GorePacket as a Client-bound payload
        registrar.playToClient(
                GorePacket.TYPE,
                GorePacket.STREAM_CODEC,
                GorePacket::handle
        );
    }
}
