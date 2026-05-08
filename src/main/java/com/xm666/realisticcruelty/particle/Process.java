package com.moskowitz.realisticcruelty.math; // Moved to the math package

import java.util.function.Consumer;

/**
 * A utility to handle the three-stage animation lifecycle of a particle:
 * 1. Initial (Fade-in/Scaling)
 * 2. Middle (Sustained state)
 * 3. End (Fade-out/Cleanup)
 */
public class Process {
    
    /**
     * @param x            The current age/time of the particle.
     * @param init         Duration of the fade-in/initial phase.
     * @param mid          The timestamp where the middle phase ends.
     * @param end          Duration of the fade-out/final phase.
     * @param initConsumer Logic to run during fade-in (receives 0.0 to 1.0).
     * @param midRunnable  Logic to run during the sustained middle phase.
     * @param endConsumer  Logic to run during fade-out (receives 0.0 to 1.0).
     */
    public static void f(float x, float init, float mid, float end, 
                        Consumer<Float> initConsumer, 
                        Runnable midRunnable, 
                        Consumer<Float> endConsumer) {
        if (x < init) {
            // Initial phase (e.g., blood puffing out)
            initConsumer.accept(x / init);
        } else if ((x -= mid) > 0) {
            // End phase (e.g., blood drying/fading)
            endConsumer.accept(x / end);
        } else {
            // Sustained phase (flying through the air)
            midRunnable.run();
        }
    }
}
