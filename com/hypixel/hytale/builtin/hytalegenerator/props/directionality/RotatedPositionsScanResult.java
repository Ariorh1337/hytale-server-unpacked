/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props.directionality;

import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPosition;
import java.util.List;
import javax.annotation.Nonnull;

public class RotatedPositionsScanResult
implements ScanResult {
    @Nonnull
    public final List<RotatedPosition> positions;

    public RotatedPositionsScanResult(@Nonnull List<RotatedPosition> positions) {
        this.positions = positions;
    }

    @Nonnull
    public static RotatedPositionsScanResult cast(ScanResult scanResult) {
        if (!(scanResult instanceof RotatedPositionsScanResult)) {
            throw new IllegalArgumentException("The provided ScanResult isn't compatible with this type.");
        }
        return (RotatedPositionsScanResult)scanResult;
    }

    @Override
    public boolean isNegative() {
        return this.positions == null || this.positions.isEmpty();
    }
}

