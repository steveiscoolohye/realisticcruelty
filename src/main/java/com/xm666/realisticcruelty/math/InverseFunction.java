package com.moskowitz.realisticcruelty.math;

/**
 * Handles the easing curves for particle size and transparency.
 * This creates non-linear movement (hyperbolic functions) to make 
 * blood spray and dissipation look cinematic rather than robotic.
 */
public class InverseFunction {
    public double offsetX, width, offsetY;

    public InverseFunction(double x, double y, boolean reversed) {
        if (reversed) {
            x = 1 - x;
        }
        this.offsetX = y / (1 - x);
        this.width = (1 - this.offsetX) / x;
        this.offsetY = -1 / (this.offsetX + this.width);
        
        double shrink = 1 / this.offsetX + this.offsetY;
        this.shrink(shrink);
        
        if (reversed) {
            offsetX += width;
            width = -width;
        }
    }

    public void shrink(double shrink) {
        this.offsetX *= shrink;
        this.width *= shrink;
        this.offsetY /= shrink;
    }

    /**
     * The core curve function used by particles in getQuadSize.
     * @param x The current progress (usually 0.0 to 1.0)
     * @return The eased value
     */
    public double f(double x) {
        return 1 / (this.offsetX + this.width * x) + this.offsetY;
    }
}
