/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props;

import javax.annotation.Nonnull;

public interface ScanResult {
    public static final ScanResult NONE = new ScanResult(){

        @Override
        public boolean isNegative() {
            return true;
        }
    };

    public boolean isNegative();

    @Nonnull
    public static ScanResult noScanResult() {
        return NONE;
    }
}

