/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import java.util.function.Supplier;

public enum RelationalOperator implements Supplier<String>
{
    NotEqual("not equal to"),
    Less("less than"),
    LessEqual("less than or equal to"),
    Greater("greater than"),
    GreaterEqual("greater than or equal to"),
    Equal("equal to");

    private final String asText;

    private RelationalOperator(String text) {
        this.asText = text;
    }

    public String asText() {
        return this.asText;
    }

    @Override
    public String get() {
        return this.asText;
    }
}

