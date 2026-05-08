package com.moskowitz.realisticcruelty.particle;

import com.moskowitz.realisticcruelty.CruelConfig;
import com.moskowitz.realisticcruelty.math.InverseFunction;
import com.moskowitz.realisticcruelty.math.Process; // Ensure this is updated to your package
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class BloodParticle extends TextureSheetParticle {
    public static final InverseFunction fadeIn = new InverseFunction(0.9, 0.8, true);
    public static final InverseFunction fadeOut = new InverseFunction(0.9, 0.2, false);

    public BloodParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 60;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.1F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            int fadeStartAge = this.lifetime + 1 - 10;
            if (this.age < fadeStartAge) {
                this.age = fadeStartAge;
                
                // Trigger the Splat effect on the ground
                if (CruelConfig.splatEnable.get()) {
                    this.level.addParticle(ModParticle.BLOOD_SPLAT.get(), this.x, this.y + 0.05D, this.z, 0.0D, 0.0D, 0.0D);
                }
                
                // Trigger small splashes
                int splashCount = CruelConfig.splashAmount.get();
                for (int j = 0; j < splashCount; j++) {
                    this.level.addParticle(ModParticle.BLOOD_SPLASH.get(), this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
                }
                
                // Play the "drip" sound realistically
                float volume = Mth.randomBetween(this.random, 0.3F, 1.0F) * CruelConfig.soundVolume.get().floatValue();
                SoundEvent dripSound = SoundEvents.BEEHIVE_DRIP;
                this.level.playLocalSound(this.x, this.y, this.z, dripSound, SoundSource.BLOCKS, volume, 1.0F, false);
            }
        }
    }

    @Override
    public float getQuadSize(float tick) {
        float time = (float)this.age + tick;
        final float[] currentSize = {this.quadSize};
        
        // This helper handles the smooth transition from spray to splat
        Process.f(time, 5, this.lifetime + 1 - 10, 10,
                (f) -> this.alpha = (float) fadeIn.f(f),
                () -> this.alpha = 1.0F,
                (f) -> currentSize[0] *= (float) fadeOut.f(f)
        );
        return currentSize[0];
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            BloodParticle particle = new BloodParticle(level, x, y, z);
            particle.pickSprite(this.sprites);
            particle.setParticleSpeed(xd, yd, zd);
            return particle;
        }
    }
}
