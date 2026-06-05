package com.hypixel.hytale.builtin.hytalegenerator.rng;

import javax.annotation.Nonnull;
import org.joml.Vector3dc;
import org.joml.Vector3ic;

public class RngField {
   private final int seed;

   public RngField(int seed) {
      this.seed = seed;
   }

   public int get(@Nonnull Vector3ic position) {
      return this.get(position.x(), position.y(), position.z());
   }

   public int get(int x, int y, int z) {
      return Rng.mix(this.seed, x, y, z);
   }

   public int get(int x, int y) {
      return Rng.mix(this.seed, x, y);
   }

   public int get(@Nonnull Vector3dc position) {
      return this.get(position.x(), position.y(), position.z());
   }

   public int get(double x, double y, double z) {
      int bitsX = Float.floatToRawIntBits((float)x);
      int bitsY = Float.floatToRawIntBits((float)y);
      int bitsZ = Float.floatToRawIntBits((float)z);
      bitsX = Integer.rotateLeft(bitsX, 1);
      bitsY = Integer.rotateLeft(bitsY, 1);
      bitsZ = Integer.rotateLeft(bitsZ, 1);
      return Rng.mix(this.seed, bitsX, bitsY, bitsZ);
   }

   public int get(double x, double y) {
      int bitsX = Float.floatToRawIntBits((float)x);
      int bitsY = Float.floatToRawIntBits((float)y);
      return Rng.mix(this.seed, bitsX, bitsY);
   }
}
