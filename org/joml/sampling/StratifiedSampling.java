/*
 * Decompiled with CFR 0.152.
 */
package org.joml.sampling;

import org.joml.Random;
import org.joml.sampling.Callback2d;

public class StratifiedSampling {
    private final Random rnd;

    public StratifiedSampling(long seed) {
        this.rnd = new Random(seed);
    }

    public void generateRandom(int n, Callback2d callback) {
        float invN = 1.0f / (float)n;
        for (int y = 0; y < n; ++y) {
            for (int x = 0; x < n; ++x) {
                float sampleX = (this.rnd.nextFloat() * invN + (float)x * invN) * 2.0f - 1.0f;
                float sampleY = (this.rnd.nextFloat() * invN + (float)y * invN) * 2.0f - 1.0f;
                callback.onNewSample(sampleX, sampleY);
            }
        }
    }

    public void generateCentered(int n, float centering, Callback2d callback) {
        float start = centering * 0.5f;
        float end = 1.0f - centering;
        float invN = 1.0f / (float)n;
        for (int y = 0; y < n; ++y) {
            for (int x = 0; x < n; ++x) {
                float sampleX = ((start + this.rnd.nextFloat() * end) * invN + (float)x * invN) * 2.0f - 1.0f;
                float sampleY = ((start + this.rnd.nextFloat() * end) * invN + (float)y * invN) * 2.0f - 1.0f;
                callback.onNewSample(sampleX, sampleY);
            }
        }
    }
}

