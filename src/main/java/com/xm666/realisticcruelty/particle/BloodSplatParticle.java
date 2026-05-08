package com.moskowitz.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moskowitz.realisticcruelty.math.InverseFunction;
import com.moskowitz.realisticcruelty.math.Process;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodSplatParticle extends TextureSheetParticle {
    // JOML replacement for the old 90-degree X-axis rotation
    public static final Quaternionf QUATERNION_UP = new Quaternionf(0.7071F, 0.0F, 0.0F, 0.7071F);
    
    public static final InverseFunction fadeIn = new InverseFunction(0.9, 0.8, true);
    public static final InverseFunction fadeOut = new InverseFunction(0.9, 0.2, false);
    
    public final Quaternionf quaternion;
    public final byte transparentSide;

    public BloodSplatParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 100;
        this.quadSize = 0.5F;
        this.quaternion = new Quaternionf(QUATERNION_UP);
        this.transparentSide = (byte) (Math.random() * 4);
    }

    @Override
    public float getQuadSize(float tick) {
        float time = (float)this.age + tick;
        Process.f(time, 10, this.lifetime + 1 - 20, 20,
                (f) -> this.alpha = (float) fadeIn.f(f),
                () -> this.alpha = 1.0F,
                (f) -> this.alpha = (float) fadeOut.f(f)
        );
        return this.quadSize * this.alpha;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float tick) {
        Vec3 cameraPos = camera.getPosition();
        float f = (float) (Mth.lerp(tick, this.xo, this.x) - cameraPos.x());
        float f1 = (float) (Mth.lerp(tick, this.yo, this.y) - cameraPos.y());
        float f2 = (float) (Mth.lerp(tick, this.zo, this.z) - cameraPos.z());

        Vector3f[] positions = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        
        float size = this.getQuadSize(tick);

        for (Vector3f pos : positions) {
            pos.rotate(this.quaternion); // JOML rotation
            pos.mul(size);
            pos.add(f, f1, f2);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(tick);

        // Render vertices with the custom transparency logic per corner
        consumer.addVertex(positions[0].x(), positions[0].y(), positions[0].z()).setUv(u1, v1).setColor(this.rCol, this.gCol, this.bCol, this.transparentSide == 0 ? this.alpha / 2 : this.alpha).setLight(light).setNormal(0,1,0);
        consumer.addVertex(positions[1].x(), positions[1].y(), positions[1].z()).setUv(u1, v0).setColor(this.rCol, this.gCol, this.bCol, this.transparentSide == 1 ? this.alpha / 2 : this.alpha).setLight(light).setNormal(0,1,0);
        consumer.addVertex(positions[2].x(), positions[2].y(), positions[2].z()).setUv(u0, v0).setColor(this.rCol, this.gCol, this.bCol, this.transparentSide == 2 ? this.alpha / 2 : this.alpha).setLight(light).setNormal(0,1,0);
        consumer.addVertex(positions[3].x(), positions[3].y(), positions[3].z()).setUv(u0, v1).setColor(this.rCol, this.gCol, this.bCol, this.transparentSide == 3 ? this.alpha / 2 : this.alpha).setLight(light).setNormal(0,1,0);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            BloodSplatParticle particle = new BloodSplatParticle(level, x, y, z);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
