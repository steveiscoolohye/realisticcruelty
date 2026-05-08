package com.moskowitz.realisticcruelty.event;

import com.moskowitz.realisticcruelty.network.GorePacket;
import com.moskowitz.realisticcruelty.network.HitType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class GoreEvent {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level(); // .getLevel() changed to .level()

        // Only handle this on the server side
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        
        // If there's no direct projectile/attacker, check the indirect cause (like a shooter)
        if (direct == null) {
            direct = source.getEntity();
            if (direct == null) return;
        }

        int entityId = entity.getId();
        int directId = direct.getId();
        
        // Determine the "style" of the hit based on the damage source
        HitType hitType = HitType.getHitType(source);
        Vec3 position = hitType.getPosition(direct);
        Vec3 rotation = hitType.getRotation(direct);
        
        float amount = event.getNewDamage(); // 1.21.1 uses getNewDamage() in Post event

        GorePacket packet = new GorePacket(
            entityId, 
            directId, 
            hitType, 
            position.x, position.y, position.z, 
            rotation.x, rotation.y, rotation.z, 
            amount
        );

        // Calculate broadcast distance (View Distance in blocks)
        double range = serverLevel.getServer().getPlayerList().getViewDistance() * 16.0;

        // Modern NeoForge Packet Distribution
        PacketDistributor.sendToPlayersNear(
            serverLevel, 
            null, // Exclude no one
            entity.getX(), entity.getY(), entity.getZ(), 
            range, 
            packet
        );
    }
}
