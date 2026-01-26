/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PositionScanResult
implements ScanResult {
    private Vector3i position;

    public PositionScanResult(@Nullable Vector3i position) {
        if (position == null) {
            return;
        }
        this.position = position.clone();
    }

    @Nullable
    public Vector3i getPosition() {
        if (this.position == null) {
            return null;
        }
        return this.position.clone();
    }

    @Nonnull
    public static PositionScanResult cast(ScanResult scanResult) {
        if (!(scanResult instanceof PositionScanResult)) {
            throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
        }
        return (PositionScanResult)scanResult;
    }

    @Override
    public boolean isNegative() {
        return this.position == null;
    }
}

