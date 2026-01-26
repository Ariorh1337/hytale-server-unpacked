/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class AreaScanner
extends Scanner {
    @Nonnull
    private final ScanShape scanShape;
    private final int range;
    private final int resultCap;
    @Nonnull
    private final Scanner childScanner;
    @Nonnull
    private final List<Vector2i> scanOrder;
    @Nonnull
    private final SpaceSize scanSpaceSize;

    public AreaScanner(int resultCap, @Nonnull ScanShape scanShape, int range, @Nonnull Scanner childScanner) {
        if (resultCap < 0 || range < 0) {
            throw new IllegalArgumentException();
        }
        this.resultCap = resultCap;
        this.childScanner = childScanner;
        this.scanShape = scanShape;
        this.range = range;
        ArrayList<Vector2i> scanOrder = new ArrayList<Vector2i>();
        for (int x = -range; x <= range; ++x) {
            for (int z = -range; z <= range; ++z) {
                if (scanShape == ScanShape.CIRCLE && Calculator.distance(x, z, 0.0, 0.0) > (double)range) continue;
                scanOrder.add(new Vector2i(x, z));
            }
        }
        this.scanOrder = VectorUtil.orderByDistanceFrom(new Vector2i(), scanOrder);
        this.scanSpaceSize = new SpaceSize(new Vector3i(-range, 0, -range), new Vector3i(1 + range, 0, 1 + range));
    }

    @Override
    @Nonnull
    public List<Vector3i> scan(@Nonnull Scanner.Context context) {
        if (this.resultCap == 0) {
            return Collections.emptyList();
        }
        ArrayList<Vector3i> validPositions = new ArrayList<Vector3i>(this.resultCap);
        for (Vector2i column : this.scanOrder) {
            Vector3i columnOrigin = new Vector3i(context.position.x + column.x, context.position.y, context.position.z + column.y);
            Scanner.Context childContext = new Scanner.Context(columnOrigin, context.pattern, context.materialSpace, context.workerId);
            List<Vector3i> results = this.childScanner.scan(childContext);
            for (Vector3i result : results) {
                validPositions.add(result);
                if (validPositions.size() != this.resultCap) continue;
                return validPositions;
            }
        }
        return validPositions;
    }

    @Override
    @Nonnull
    public SpaceSize scanSpace() {
        return this.scanSpaceSize.clone();
    }

    public static enum ScanShape {
        CIRCLE,
        SQUARE;

        public static final Codec<ScanShape> CODEC;

        static {
            CODEC = new EnumCodec<ScanShape>(ScanShape.class, EnumCodec.EnumStyle.LEGACY);
        }
    }

    public static enum Verticality {
        GLOBAL,
        LOCAL;

    }
}

