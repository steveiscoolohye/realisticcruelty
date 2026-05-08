package com.moskowitz.realisticcruelty.particle;

import com.moskowitz.realisticcruelty.CruelConfig;
import com.moskowitz.realisticcruelty.math.InverseFunction;
import com.moskowitz.realisticcruelty.math.Process;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class BloodSplashParticle extends TextureSheetParticle {
    public static final InverseFunction fadeIn = new InverseFunction(0.9, 0.8, true);
    public static final InverseFunction fadeOut = new InverseFunction(0.9, 0.2, false);

    public BloodSplashParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 20;
        this.quadSize = 0.05F;
        this.gravity = 1.0F;
        // Random initial spray velocity
        this.xd = (Math.random() * 2 - 1) * 0.15D;
        this.yd = Math.random() * 0.2D + 0.1D;
        this.zd = (Math.random() * 2 - 1) * 0.15D;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            int fadeStartAge = this.lifetime + 1 - 5;
            if (this.age < fadeStartAge) {
                this.age = fadeStartAge;
                
                // Fixed: Respect soundVolume config instead of splashAmount
                float volume = Mth.randomBetween(this.random, 0.3F, 1.0F) * CruelConfig.soundVolume.get().floatValue();
                SoundEvent soundevent = SoundEvents.BEEHIVE_DRIP;
                this.level.playLocalSound(this.x, this.y, this.z, soundevent, SoundSource.BLOCKS, volume, 1.0F, false);
            }
        }
    }

    @Override
    public float getQuadSize(float tick) {
        float time = (float)this.age + tick;
        final float[] sizeArr = {this.quadSize};
        
        Process.f(time, 5, this.lifetime + 1 - 5, 5,
                (f) -> sizeArr[0] *= (float) fadeIn.f(f),
                () -> { },
                (f) -> this.alpha = (float) fadeOut.f(f)
        );
        return sizeArr[0];
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            BloodSplashParticle particle = new BloodSplashParticle(level, x, y, z);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
