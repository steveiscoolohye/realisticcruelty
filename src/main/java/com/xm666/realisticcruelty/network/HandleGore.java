package com.moskowitz.realisticcruelty.network; // Updated package

import com.moskowitz.realisticcruelty.CruelConfig;
import com.moskowitz.realisticcruelty.particle.ModParticle;
import com.moskowitz.realisticcruelty.math.ModUtil; // Ensure this exists
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HandleGore {
    public static void handle(GorePacket msg) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        
        // Null check the level to prevent crashes on world exit
        if (level == null) return;

        Entity entity = level.getEntity(msg.entityId());
        if (entity == null) return;

        AABB aabb = entity.getBoundingBox();
        Entity direct = level.getEntity(msg.directId());
        Vec3 position, rotation;
        HitType hitType = msg.hitType();

        if (direct == null) {
            position = new Vec3(msg.x(), msg.y(), msg.z());
            rotation = new Vec3(msg.xd(), msg.yd(), msg.zd());
        } else {
            position = hitType.getPosition(direct);
            rotation = hitType.getRotation(direct);
        }

        HitArg hitArg = hitType.getHitArg(aabb, new HitArg(position, rotation));
        if (hitArg == null) return;

        position = hitArg.position();
        rotation = hitArg.rotation();
        
        // Final call to spawn the effects
        HandleGore.gore(level, position, rotation, msg.amount());
    }

    public static void gore(ClientLevel level, Vec3 position, Vec3 rotation, float amount) {
        double sqrt = Math.sqrt(amount);
        
        // 1.21.1 Tip: Instead of static variables, we'll eventually move 'size' 
        // into the ParticleOptions for better realism.
        float fogSize = (float) sqrt * CruelConfig.fogSize.get().floatValue();
        
        // Spawn the central blood fog
        level.addParticle(ModParticle.BLOOD_FOG.get(), 
            position.x, position.y, position.z, 
            rotation.x, rotation.y, rotation.z);

        double speed = -sqrt * CruelConfig.bloodSpeed.get();
        float bloodAmount = amount * CruelConfig.bloodAmount.get().floatValue();

        // Spawn individual realistic blood droplets
        while (bloodAmount-- > 0) {
            ModUtil.addParticle(level, position, ModParticle.BLOOD.get(), rotation, speed);
        }
    }
}
