/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SeedResource {
    public static final String INFO_SEED_REPORT = "Seed Value: %s for seed %s / %s";
    public static final String INFO_SEED_OVERWRITE_REPORT = "Seed Value: %s for seed %s / %s overwritten by %s";

    @Nonnull
    default public ResultBuffer.Bounds2d localBounds2d() {
        return ResultBuffer.bounds2d;
    }

    @Nonnull
    default public ResultBuffer.ResultBuffer2d localBuffer2d() {
        return ResultBuffer.buffer2d;
    }

    @Nonnull
    default public ResultBuffer.ResultBuffer3d localBuffer3d() {
        return ResultBuffer.buffer3d;
    }

    default public boolean shouldReportSeeds() {
        return false;
    }

    default public void reportSeeds(int seedVal, String original, String seed, @Nullable String overwritten) {
        if (this.shouldReportSeeds()) {
            if (overwritten == null) {
                this.writeSeedReport(String.format(INFO_SEED_REPORT, seedVal, original, seed));
            } else {
                this.writeSeedReport(String.format(INFO_SEED_OVERWRITE_REPORT, seedVal, original, seed, overwritten));
            }
        }
    }

    default public void writeSeedReport(String seedReport) {
        System.out.println(seedReport);
    }
}

