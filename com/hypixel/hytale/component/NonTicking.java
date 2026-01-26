/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.Component;

public class NonTicking<ECS_TYPE>
implements Component<ECS_TYPE> {
    private static final NonTicking<?> INSTANCE = new NonTicking();

    public static <ECS_TYPE> NonTicking<ECS_TYPE> get() {
        return INSTANCE;
    }

    @Override
    public Component<ECS_TYPE> clone() {
        return NonTicking.get();
    }
}

