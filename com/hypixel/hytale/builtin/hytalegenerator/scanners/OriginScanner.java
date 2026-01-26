/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class OriginScanner
extends Scanner {
    private static final OriginScanner instance = new OriginScanner();
    private static final SpaceSize SCAN_SPACE_SIZE = new SpaceSize(new Vector3i(0, 0, 0), new Vector3i(1, 0, 1));

    private OriginScanner() {
    }

    @Override
    @Nonnull
    public List<Vector3i> scan(@Nonnull Scanner.Context context) {
        Pattern.Context patternContext = new Pattern.Context(context.position, context.materialSpace, context.workerId);
        if (context.pattern.matches(patternContext)) {
            return Collections.singletonList(context.position.clone());
        }
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public SpaceSize scanSpace() {
        return SCAN_SPACE_SIZE.clone();
    }

    public static OriginScanner getInstance() {
        return instance;
    }
}

