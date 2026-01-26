/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.validators.RelationalOperator;
import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;
import javax.annotation.Nonnull;

public abstract class DoubleValidator
extends Validator {
    public abstract boolean test(double var1);

    public static boolean compare(double value, @Nonnull RelationalOperator predicate, double c) {
        return switch (predicate) {
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

    public abstract String errorMessage(double var1);

    public abstract String errorMessage(double var1, String var3);
}

