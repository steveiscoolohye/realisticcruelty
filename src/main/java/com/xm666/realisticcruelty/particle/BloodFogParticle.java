package com.moskowitz.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moskowitz.realisticcruelty.math.InverseFunction;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodFogParticle extends TextureSheetParticle {
    public static final InverseFunction fadeIn = new InverseFunction(0.2, 0.8, true);
    private final Quaternionf quaternion;

    public BloodFogParticle(ClientLevel level, double x, double y, double z, double size) {
        super(level, x, y, z);
        this.lifetime = 10;
        this.quadSize = (float) size;
        
        // Use JOML for rotation
        this.quaternion = new Quaternionf().rotationZ(Mth.TWO_PI * (float) Math.random());
    }

    @Override
    public float getQuadSize(float tick) {
        float time = (float)this.age + tick;
        float sizeFactor = (float) BloodFogParticle.fadeIn.f(time / (this.lifetime + 1));
        this.alpha = sizeFactor < 0.8f ? 1.0f : (1.0f - sizeFactor) / 0.2f;
        return this.quadSize * sizeFactor;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float tick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(tick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(tick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(tick, this.zo, this.z) - vec3.z());

        // JOML rotation math
        Quaternionf cameraRotation = new Quaternionf(camera.rotation());
        cameraRotation.mul(this.quaternion);

        Vector3f[] positions = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F),
            new Vector3f(-1.0F, 1.0F, 0.0F),
            new Vector3f(1.0F, 1.0F, 0.0F),
            new Vector3f(1.0F, -1.0F, 0.0F)
        };
        
        float size = this.getQuadSize(tick);

        for (Vector3f pos : positions) {
            pos.rotate(cameraRotation); // JOML method
            pos.mul(size);
            pos.add(f, f1, f2);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(tick);

        consumer.addVertex(positions[0].x(), positions[0].y(), positions[0].z()).setUv(u1, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light).setNormal(0,1,0);
        consumer.addVertex(positions[1].x(), positions[1].y(), positions[1].z()).setUv(u1, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light).setNormal(0,1,0);
        consumer.addVertex(positions[2].x(), positions[2].y(), positions[2].z()).setUv(u0, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light).setNormal(0,1,0);
        consumer.addVertex(positions[3].x(), positions[3].y(), positions[3].z()).setUv(u0, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light).setNormal(0,1,0);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            // We use xSpeed to pass the 'size' from HandleGore
            BloodFogParticle particle = new BloodFogParticle(level, x, y, z, xSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
