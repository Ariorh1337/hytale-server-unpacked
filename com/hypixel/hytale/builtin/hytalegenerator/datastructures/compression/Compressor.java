/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.compression;

import javax.annotation.Nonnull;

public class Compressor {
    private final int MIN_RUN = 7;

    @Nonnull
    public <T> CompressedArray<T> compressOnReference(@Nonnull T[] in) {
        int currentRun = 0;
        int resultIndex = 0;
        Object runObj = null;
        Object[] result = new Object[in.length];
        for (int i = 0; i < result.length; ++i) {
            if (in[i] != runObj && currentRun >= 7) {
                result[resultIndex] = new Run(runObj, currentRun);
                currentRun = 0;
                ++resultIndex;
                runObj = in[i];
                continue;
            }
            if (in[i] != runObj && currentRun < 7) {
                while (currentRun > 0) {
                    result[resultIndex] = runObj;
                    ++resultIndex;
                    --currentRun;
                }
                currentRun = 0;
                runObj = in[i];
                continue;
            }
            ++currentRun;
        }
        if (currentRun >= 7) {
            result[resultIndex] = new Run(runObj, currentRun);
        } else {
            while (currentRun > 0) {
                result[resultIndex] = runObj;
                ++resultIndex;
                --currentRun;
            }
        }
        Object[] trimmedResult = new Object[resultIndex];
        System.arraycopy(result, 0, trimmedResult, 0, trimmedResult.length);
        return new CompressedArray(trimmedResult, in.length);
    }

    @Nonnull
    public <T> T[] decompress(@Nonnull CompressedArray<T> compressedArray) {
        int runIndex = 0;
        int outIndex = 0;
        Object[] ca = compressedArray.data;
        Object[] out = new Object[compressedArray.initialLength];
        for (int caIndex = 0; caIndex < ca.length; ++caIndex) {
            if (ca[caIndex] instanceof Run) {
                Run run = (Run)ca[caIndex];
                for (runIndex = 0; runIndex < run.length; ++runIndex) {
                    out[outIndex++] = run.obj;
                }
                continue;
            }
            out[outIndex++] = ca[caIndex];
        }
        return out;
    }

    public static class Run {
        Object obj;
        int length;

        private Run(Object obj, int length) {
            this.obj = obj;
            this.length = length;
        }
    }

    public static class CompressedArray<T> {
        private final Object[] data;
        private final int initialLength;

        private CompressedArray(Object[] data, int initialLength) {
            this.data = data;
            this.initialLength = initialLength;
        }
    }
}

