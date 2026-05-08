package com.moskowitz.realisticcruelty.network; // Updated package to match your new structure

import net.minecraft.world.phys.Vec3;

/**
 * A simple container for the precise hit location and angle.
 * Used by HitType and ModUtil to calculate where blood should spray.
 */
public record HitArg(Vec3 position, Vec3 rotation) {
}
