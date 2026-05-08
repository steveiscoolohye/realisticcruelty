package com.moskowitz.realisticcruelty.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public enum HitType {
    MELEE {
        public Vec3 getPosition(Entity entity) {
            return entity.getEyePosition(HitType.getPartialTick());
        }

        public Vec3 getRotation(Entity entity) {
            return entity.getViewVector(HitType.getPartialTick());
        }

        public HitArg getHitArg(AABB aabb, HitArg hitArg) {
            return HitType.getHitArgRay(aabb, hitArg);
        }
    },
    INDIRECT {
        public Vec3 getPosition(Entity entity) {
            // Updated to use the correct position method
            return entity.position();
        }

        public Vec3 getRotation(Entity entity) {
            return entity.getDeltaMovement().normalize();
        }

        public HitArg getHitArg(AABB aabb, HitArg hitArg) {
            return HitType.getHitArgRay(aabb, hitArg);
        }
    },
    EXPLODE {
        public Vec3 getPosition(Entity entity) {
            return entity.position();
        }

        public Vec3 getRotation(Entity entity) {
            return Vec3.ZERO;
        }

        public HitArg getHitArg(AABB aabb, HitArg hitArg) {
            return HitType.getHitArgOrb(aabb, hitArg);
        }
    };

    public static HitArg getHitArgRay(AABB aabb, HitArg hitArg) {
        Vec3 position = hitArg.position();
        Vec3 rotation = hitArg.rotation();
        // Ensure ModUtil is also updated to your new package!
        double[] doubles = ModUtil.rayClip(aabb, position, rotation.x, rotation.y, rotation.z);
        if (doubles == null) {
            return null;
        }
        position = new Vec3(doubles[1], doubles[2], doubles[3]);
        return new HitArg(position, rotation);
    }

    public static HitArg getHitArgOrb(AABB aabb, HitArg hitArg) {
        Vec3 position = hitArg.position();
        Vec3 vec3 = ModUtil.orbClip(aabb, position);
        Vec3 rotation = position.subtract(vec3).normalize();
        return new HitArg(vec3, rotation);
    }

    public static float getPartialTick() {
        // Updated to NeoForge check
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        }
        return 1.0F;
    }

    public static HitType getHitType(DamageSource damageSource) {
        // In 1.21.1, we check tags or the direct source entity
        if (damageSource.is(net.minecraft.world.damagesource.DamageTypeTags.IS_EXPLOSION)) {
            return HitType.EXPLODE;
        } else if (damageSource.getDirectEntity() != damageSource.getEntity() && damageSource.getDirectEntity() != null) {
            // If the thing that hit you isn't the person who "caused" it (like an arrow), it's indirect
            return HitType.INDIRECT;
        }
        return HitType.MELEE;
    }

    public abstract Vec3 getPosition(Entity entity);

    public abstract Vec3 getRotation(Entity entity);

    public abstract HitArg getHitArg(AABB aabb, HitArg hitArg);
}
