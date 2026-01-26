/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.RelationalOperator;
import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;
import javax.annotation.Nonnull;

public abstract class IntValidator
extends Validator {
    public abstract boolean test(int var1);

    public static boolean compare(int value, @Nonnull RelationalOperator op, int c) {
        return switch (op) {
            default -> throw new MatchException(null, null);
            case RelationalOperator.NotEqual -> {
                if (value != c) {
                    yield true;
                }
                yield false;
            }
            case RelationalOperator.Less -> {
                if (value < c) {
                    yield true;
                }
                yield false;
            }
            case RelationalOperator.LessEqual -> {
                if (value <= c) {
                    yield true;
                }
                yield false;
            }
            case RelationalOperator.Greater -> {
                if (value > c) {
                    yield true;
                }
                yield false;
            }
            case RelationalOperator.GreaterEqual -> {
                if (value >= c) {
                    yield true;
                }
                yield false;
            }
            case RelationalOperator.Equal -> value == c;
        };
    }

    public abstract String errorMessage(int var1);

    public abstract String errorMessage(int var1, String var2);
}

