/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class SurfacePattern
extends Pattern {
    @Nonnull
    private final Pattern wallPattern;
    @Nonnull
    private final Pattern originPattern;
    @Nonnull
    private final SpaceSize readSpaceSize;
    @Nonnull
    private final List<Vector3i> surfacePositions;
    @Nonnull
    private final List<Vector3i> originPositions;

    public SurfacePattern(@Nonnull Pattern surfacePattern, @Nonnull Pattern originPattern, double surfaceRadius, double originRadius, @Nonnull Facing facing, int surfaceGap, int originGap) {
        this.wallPattern = surfacePattern;
        this.originPattern = originPattern;
        int surfaceY = -1 - surfaceGap;
        this.surfacePositions = new ArrayList<Vector3i>(1);
        for (int x = -((int)surfaceRadius) - 1; x <= (int)surfaceRadius + 1; ++x) {
            for (int z = -((int)surfaceRadius) - 1; z <= (int)surfaceRadius + 1; ++z) {
                if (Calculator.distance(x, z, 0.0, 0.0) > surfaceRadius) continue;
                Vector3i position = new Vector3i(x, surfaceY, z);
                this.surfacePositions.add(position);
            }
        }
        int originY = originGap;
        this.originPositions = new ArrayList<Vector3i>(1);
        for (int x = -((int)originRadius) - 1; x <= (int)originRadius + 1; ++x) {
            for (int z = -((int)originRadius) - 1; z <= (int)originRadius + 1; ++z) {
                if (Calculator.distance(x, z, 0.0, 0.0) > originRadius) continue;
                Vector3i position = new Vector3i(x, originY, z);
                this.originPositions.add(position);
            }
        }
        for (Vector3i pos : this.surfacePositions) {
            this.applyFacing(pos, facing);
        }
        for (Vector3i pos : this.originPositions) {
            this.applyFacing(pos, facing);
        }
        SpaceSize floorSpace = surfacePattern.readSpace();
        for (Vector3i pos : this.surfacePositions) {
            floorSpace = SpaceSize.merge(floorSpace, new SpaceSize(pos));
        }
        floorSpace = SpaceSize.stack(floorSpace, surfacePattern.readSpace());
        SpaceSize originSpace = originPattern.readSpace();
        for (Vector3i pos : this.originPositions) {
            originSpace = SpaceSize.merge(originSpace, new SpaceSize(pos));
        }
        originSpace = SpaceSize.stack(originSpace, originPattern.readSpace());
        this.readSpaceSize = SpaceSize.merge(floorSpace, originSpace);
    }

    private void applyFacing(@Nonnull Vector3i pos, @Nonnull Facing facing) {
        switch (facing.ordinal()) {
            case 1: {
                this.toD(pos);
                break;
            }
            case 2: {
                this.toE(pos);
                break;
            }
            case 3: {
                this.toW(pos);
                break;
            }
            case 5: {
                this.toN(pos);
                break;
            }
            case 4: {
                this.toS(pos);
            }
        }
    }

    private void toD(@Nonnull Vector3i pos) {
        pos.y = -pos.y;
    }

    private void toN(@Nonnull Vector3i pos) {
        int y = pos.y;
        pos.y = pos.z;
        pos.z = y;
    }

    private void toS(@Nonnull Vector3i pos) {
        this.toN(pos);
        pos.z = -pos.z;
    }

    private void toW(@Nonnull Vector3i pos) {
        int y = pos.y;
        pos.y = -pos.x;
        pos.x = y;
    }

    private void toE(@Nonnull Vector3i pos) {
        this.toW(pos);
        pos.x = -pos.x;
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        Vector3i childPosition = context.position.clone();
        Pattern.Context childContext = new Pattern.Context(context);
        childContext.position = childPosition;
        for (Vector3i pos : this.originPositions) {
            childPosition.assign(pos).add(context.position);
            if (this.originPattern.matches(childContext)) continue;
            return false;
        }
        for (Vector3i pos : this.surfacePositions) {
            childPosition.assign(pos).add(context.position);
            if (this.wallPattern.matches(childContext)) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }

    public static enum Facing {
        U,
        D,
        E,
        W,
        S,
        N;

        @Nonnull
        public static Codec<Facing> CODEC;

        static {
            CODEC = new EnumCodec<Facing>(Facing.class, EnumCodec.EnumStyle.LEGACY);
        }
    }
}

