package org.joml;

public class Random {
   private final Random.Xorshiro128 rnd;
   private static long seedHalf = 8020463840L;

   public static long newSeed() {
      synchronized (Random.class) {
         long oldSeedHalf = seedHalf;
         long newSeedHalf = oldSeedHalf * 3512401965023503517L;
         seedHalf = newSeedHalf;
         return newSeedHalf;
      }
   }

   public Random() {
      this(newSeed() ^ System.nanoTime());
   }

   public Random(long seed) {
      this.rnd = new Random.Xorshiro128(seed);
   }

   public float nextFloat() {
      return this.rnd.nextFloat();
   }

   public int nextInt(int n) {
      return this.rnd.nextInt(n);
   }

   private static final class Xorshiro128 {
      private static final float INT_TO_FLOAT = Float.intBitsToFloat(864026624);
      private long _s0;
      private long _s1;
      private long state;

      Xorshiro128(long seed) {
         this.state = seed;
         this._s0 = this.nextSplitMix64();
         this._s1 = this.nextSplitMix64();
      }

      private long nextSplitMix64() {
         long z = this.state += -7046029254386353131L;
         z = (z ^ z >>> 30) * -4658895280553007687L;
         z = (z ^ z >>> 27) * -7723592293110705685L;
         return z ^ z >>> 31;
      }

      final float nextFloat() {
         return (this.nextInt() >>> 8) * INT_TO_FLOAT;
      }

      private int nextInt() {
         long s0 = this._s0;
         long s1 = this._s1;
         long result = s0 + s1;
         s1 ^= s0;
         this.rotateLeft(s0, s1);
         return (int)(result & -1L);
      }

      private static long rotl_JDK4(long x, int k) {
         return x << k | x >>> 64 - k;
      }

      private static long rotl_JDK5(long x, int k) {
         return Long.rotateLeft(x, k);
      }

      private static long rotl(long x, int k) {
         return Runtime.HAS_Long_rotateLeft ? rotl_JDK5(x, k) : rotl_JDK4(x, k);
      }

      private void rotateLeft(long s0, long s1) {
         this._s0 = rotl(s0, 55) ^ s1 ^ s1 << 14;
         this._s1 = rotl(s1, 36);
      }

      final int nextInt(int n) {
         long r = this.nextInt() >>> 1;
         r = r * n >> 31;
         return (int)r;
      }
   }
}
