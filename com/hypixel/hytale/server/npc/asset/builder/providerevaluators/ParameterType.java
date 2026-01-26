/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.providerevaluators;

import java.util.function.Supplier;

public enum ParameterType implements Supplier<String>
{
    DOUBLE("double"),
    STRING("string"),
    INTEGER("int");

    private final String description;

    private ParameterType(String description) {
        this.description = description;
    }

    @Override
    public String get() {
        return this.description;
    }
}

