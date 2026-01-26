/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.tintproviders;

import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.ConstantTintProvider;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import javax.annotation.Nonnull;

public abstract class TintProvider {
    public static final int DEFAULT_TINT = ColorParseUtil.colorToARGBInt(new Color(91, -98, 40));

    public abstract Result getValue(@Nonnull Context var1);

    @Nonnull
    public static TintProvider noTintProvider() {
        return new ConstantTintProvider(DEFAULT_TINT);
    }

    public static class Context {
        public Vector3i position;
        public WorkerIndexer.Id workerId;

        public Context(@Nonnull Vector3i position, WorkerIndexer.Id workerId) {
            this.position = position;
            this.workerId = workerId;
        }

        public Context(@Nonnull Context other) {
            this.position = other.position;
            this.workerId = other.workerId;
        }
    }

    public static class Result {
        public static final Result WITHOUT_VALUE = new Result();
        public final int tint;
        public final boolean hasValue;

        public Result(int tint) {
            this.tint = tint;
            this.hasValue = true;
        }

        public Result() {
            this.tint = DEFAULT_TINT;
            this.hasValue = false;
        }
    }
}

