/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum RelativeDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN;

    public static final SingleArgumentType<RelativeDirection> ARGUMENT_TYPE;

    @Nonnull
    public static Vector3i toDirectionVector(@Nullable RelativeDirection direction, @Nonnull HeadRotation headRotation) {
        if (direction == null) {
            return headRotation.getAxisDirection();
        }
        return switch (direction.ordinal()) {
            default -> throw new MatchException(null, null);
            case 4 -> new Vector3i(0, 1, 0);
            case 5 -> new Vector3i(0, -1, 0);
            case 0 -> headRotation.getHorizontalAxisDirection();
            case 1 -> headRotation.getHorizontalAxisDirection().clone().scale(-1);
            case 2 -> RelativeDirection.rotateLeft(headRotation.getHorizontalAxisDirection());
            case 3 -> RelativeDirection.rotateRight(headRotation.getHorizontalAxisDirection());
        };
    }

    @Nonnull
    public static Axis toAxis(@Nonnull RelativeDirection direction, @Nonnull HeadRotation headRotation) {
        return switch (direction.ordinal()) {
            default -> throw new MatchException(null, null);
            case 4, 5 -> Axis.Y;
            case 0, 1 -> RelativeDirection.getHorizontalAxis(headRotation);
            case 2, 3 -> RelativeDirection.getPerpendicularHorizontalAxis(headRotation);
        };
    }

    @Nonnull
    private static Axis getHorizontalAxis(@Nonnull HeadRotation headRotation) {
        Vector3i horizontalDir = headRotation.getHorizontalAxisDirection();
        return horizontalDir.getX() != 0 ? Axis.X : Axis.Z;
    }

    @Nonnull
    private static Axis getPerpendicularHorizontalAxis(@Nonnull HeadRotation headRotation) {
        Vector3i horizontalDir = headRotation.getHorizontalAxisDirection();
        return horizontalDir.getX() != 0 ? Axis.Z : Axis.X;
    }

    @Nonnull
    private static Vector3i rotateLeft(@Nonnull Vector3i dir) {
        return new Vector3i(dir.z, 0, -dir.x);
    }

    @Nonnull
    private static Vector3i rotateRight(@Nonnull Vector3i dir) {
        return new Vector3i(-dir.z, 0, dir.x);
    }

    static {
        ARGUMENT_TYPE = new SingleArgumentType<RelativeDirection>("Relative Direction", "A direction relative to the player (forward, backward, left, right, up, down)", new String[]{"forward", "backward", "left", "right", "up", "down"}){

            @Override
            @Nullable
            public RelativeDirection parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
                return switch (input.toLowerCase()) {
                    case "forward", "f" -> FORWARD;
                    case "backward", "back", "b" -> BACKWARD;
                    case "left", "l" -> LEFT;
                    case "right", "r" -> RIGHT;
                    case "up", "u" -> UP;
                    case "down", "d" -> DOWN;
                    default -> {
                        parseResult.fail(Message.raw("Invalid direction: " + input + ". Use: forward, backward, left, right, up, down"));
                        yield null;
                    }
                };
            }
        };
    }
}

