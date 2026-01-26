/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.Component;
import javax.annotation.Nonnull;

public class NonSerialized<ECS_TYPE>
implements Component<ECS_TYPE> {
    @Nonnull
    private static final NonSerialized<?> INSTANCE = new NonSerialized();

    public static <ECS_TYPE> NonSerialized<ECS_TYPE> get() {
        return INSTANCE;
    }

    @Override
    public Component<ECS_TYPE> clone() {
        return NonSerialized.get();
    }
}

