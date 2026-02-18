/*
 * Decompiled with CFR 0.152.
 */
package org.joml.sampling;

import java.nio.FloatBuffer;
import org.joml.Math;

public class Convolution {
    public static void gaussianKernel(int rows, int cols, float sigma, FloatBuffer dest) {
        if ((rows & 1) == 0) {
            throw new IllegalArgumentException("rows must be an odd number");
        }
        if ((cols & 1) == 0) {
            throw new IllegalArgumentException("cols must be an odd number");
        }
        if (dest == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
        if (dest.remaining() < rows * cols) {
            throw new IllegalArgumentException("dest must have at least " + rows * cols + " remaining values");
        }
        float sum = 0.0f;
        int pos = dest.position();
        float a = (float)(1.0 / (2.0 * (double)sigma * (double)sigma));
        int i = 0;
        for (int y = -(rows - 1) / 2; y <= (rows - 1) / 2; ++y) {
            int x = -(cols - 1) / 2;
            while (x <= (cols - 1) / 2) {
                float k = (float)Math.exp((float)(-(y * y + x * x)) * a);
                dest.put(pos + i, k);
                sum += k;
                ++x;
                ++i;
            }
        }
        sum = 1.0f / sum;
        for (i = 0; i < rows * cols; ++i) {
            dest.put(pos + i, dest.get(pos + i) * sum);
        }
    }

    public static void gaussianKernel(int rows, int cols, float sigma, float[] dest) {
        if ((rows & 1) == 0) {
            throw new IllegalArgumentException("rows must be an odd number");
        }
        if ((cols & 1) == 0) {
            throw new IllegalArgumentException("cols must be an odd number");
        }
        if (dest == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
        if (dest.length < rows * cols) {
            throw new IllegalArgumentException("dest must have a size of at least " + rows * cols);
        }
        float sum = 0.0f;
        float a = (float)(1.0 / (2.0 * (double)sigma * (double)sigma));
        int i = 0;
        for (int y = -(rows - 1) / 2; y <= (rows - 1) / 2; ++y) {
            int x = -(cols - 1) / 2;
            while (x <= (cols - 1) / 2) {
                float k;
                dest[i] = k = (float)Math.exp((float)(-(y * y + x * x)) * a);
                sum += k;
                ++x;
                ++i;
            }
        }
        sum = 1.0f / sum;
        for (i = 0; i < rows * cols; ++i) {
            dest[i] = dest[i] * sum;
        }
    }
}

