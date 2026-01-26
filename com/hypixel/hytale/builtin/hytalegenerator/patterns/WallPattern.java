/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class WallPattern
extends Pattern {
    @Nonnull
    private final Pattern wallPattern;
    @Nonnull
    private final Pattern originPattern;
    @Nonnull
    private final List<WallDirection> directions;
    private final boolean matchAll;
    private final SpaceSize readSpaceSize;

    public WallPattern(@Nonnull Pattern wallPattern, @Nonnull Pattern originPattern, @Nonnull List<WallDirection> wallDirections, boolean matchAll) {
        this.wallPattern = wallPattern;
        this.originPattern = originPattern;
        this.directions = new ArrayList<WallDirection>(wallDirections);
        this.matchAll = matchAll;
        SpaceSize originSpace = originPattern.readSpace();
        SpaceSize wallSpace = wallPattern.readSpace();
        SpaceSize totalSpace = originSpace;
        for (WallDirection d : this.directions) {
            SpaceSize directionedWallSpace = switch (d.ordinal()) {
                default -> throw new MatchException(null, null);
                case 2 -> wallSpace.clone().moveBy(new Vector3i(1, 0, 0));
                case 3 -> wallSpace.clone().moveBy(new Vector3i(-1, 0, 0));
                case 0 -> wallSpace.clone().moveBy(new Vector3i(0, 0, -1));
                case 1 -> wallSpace.clone().moveBy(new Vector3i(0, 0, 1));
            };
            totalSpace = SpaceSize.merge(totalSpace, directionedWallSpace);
        }
        this.readSpaceSize = totalSpace;
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        for (WallDirection direction : this.directions) {
            boolean matches = this.matches(context, direction);
            if (this.matchAll && !matches) {
                return false;
            }
            if (!matches) continue;
            return true;
        }
        return false;
    }

    private boolean matches(@Nonnull Pattern.Context context, @Nonnull WallDirection direction) {
        Vector3i wallPosition = context.position.clone();
        switch (direction.ordinal()) {
            case 2: {
                ++wallPosition.x;
                break;
            }
            case 3: {
                --wallPosition.x;
                break;
            }
            case 0: {
                --wallPosition.z;
                break;
            }
            case 1: {
                ++wallPosition.z;
            }
        }
        Pattern.Context wallContext = new Pattern.Context(context);
        wallContext.position = wallPosition;
        return this.originPattern.matches(context) && this.wallPattern.matches(wallContext);
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }

    public static enum WallDirection {
        N,
        S,
        E,
        W;

        public static final Codec<WallDirection> CODEC;

        static {
            CODEC = new EnumCodec<WallDirection>(WallDirection.class, EnumCodec.EnumStyle.LEGACY);
        }
    }
}

