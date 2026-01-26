/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import java.util.EnumSet;
import java.util.function.Supplier;

public enum ComponentContext implements Supplier<String>
{
    SensorSelf("self sensor"),
    SensorTarget("target sensor"),
    SensorEntity("entity sensor");

    private final String description;
    public static final EnumSet<ComponentContext> NotSelfEntitySensor;

    private ComponentContext(String description) {
        this.description = description;
    }

    @Override
    public String get() {
        return this.description;
    }

    static {
        NotSelfEntitySensor = EnumSet.of(SensorTarget, SensorEntity);
    }
}

