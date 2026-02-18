/*
 * Decompiled with CFR 0.152.
 */
package org.joml.sampling;

import org.joml.Random;
import org.joml.sampling.Callback2d;
import org.joml.sampling.Callback3d;
import org.joml.sampling.Math;

public class UniformSampling {

    public static class Sphere {
        private final Random rnd;

        public Sphere(long seed, int numSamples, Callback3d callback) {
            this.rnd = new Random(seed);
            this.generate(numSamples, callback);
        }

        public void generate(int numSamples, Callback3d callback) {
            int i = 0;
            while (i < numSamples) {
                float x2;
                float x1 = this.rnd.nextFloat() * 2.0f - 1.0f;
                if (x1 * x1 + (x2 = this.rnd.nextFloat() * 2.0f - 1.0f) * x2 >= 1.0f) continue;
                float sqrt = (float)Math.sqrt(1.0 - (double)(x1 * x1) - (double)(x2 * x2));
                float x = 2.0f * x1 * sqrt;
                float y = 2.0f * x2 * sqrt;
                float z = 1.0f - 2.0f * (x1 * x1 + x2 * x2);
                callback.onNewSample(x, y, z);
                ++i;
            }
        }
    }

    public static class Disk {
        private final Random rnd;

        public Disk(long seed, int numSamples, Callback2d callback) {
            this.rnd = new Random(seed);
            this.generate(numSamples, callback);
        }

        private void generate(int numSamples, Callback2d callback) {
            for (int i = 0; i < numSamples; ++i) {
                float r = this.rnd.nextFloat();
                float a = this.rnd.nextFloat() * 2.0f * (float)java.lang.Math.PI;
                float sqrtR = Math.sqrt(r);
                float x = sqrtR * (float)Math.sin_roquen_9((double)a + 1.5707963267948966);
                float y = sqrtR * (float)Math.sin_roquen_9(a);
                callback.onNewSample(x, y);
            }
        }
    }
}

