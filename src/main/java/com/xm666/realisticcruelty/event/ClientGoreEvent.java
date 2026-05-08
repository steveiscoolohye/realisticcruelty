package com.moskowitz.realisticcruelty.event;

import com.moskowitz.realisticcruelty.CruelConfig;
import com.moskowitz.realisticcruelty.network.GorePacket;
import com.moskowitz.realisticcruelty.network.HandleGore;
import com.moskowitz.realisticcruelty.network.HitArg;
import com.moskowitz.realisticcruelty.network.HitType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class ClientGoreEvent {

    @SubscribeEvent
    public static void onPlayerAttackTarget(AttackEntityEvent event) {
        Entity target = event.getTarget();
        // Modern field access: .level() instead of .level
        if (target.level().isClientSide) {
            Player attacker = event.getEntity();
            HitType hitType = HitType.MELEE;
            
            // Cast to float for the packet constructor
            float amount = CruelConfig.clientMelee.get().floatValue();
            
            // Create a local packet to trigger particles immediately on the attacker's screen
            GorePacket packet = new GorePacket(target.getId(), attacker.getId(), hitType, 0, 0, 0, 0, 0, 0, amount);
            HandleGore.handle(packet);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        
        if (projectile.level().isClientSide) {
            HitResult hitResult = event.getRayTraceResult();
            
            if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                AABB aabb = entity.getBoundingBox();
                HitType hitType = HitType.INDIRECT;
                
                Vec3 rotation = hitType.getRotation(projectile);
                Vec3 pos = hitType.getPosition(projectile);
                
                // Calculate the exact point where the arrow/trident meets the hitbox
                HitArg hitArg = hitType.getHitArg(aabb, new HitArg(pos, rotation));
                Vec3 location = hitArg.position();
                
                float amount = CruelConfig.clientTrident.get().floatValue();
                
                // Directly trigger the particles in the client world
                HandleGore.gore((ClientLevel) entity.level(), location, rotation, amount);
            }
        }
    }
}
