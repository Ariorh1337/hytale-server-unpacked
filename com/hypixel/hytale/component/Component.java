/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Component<ECS_TYPE>
extends Cloneable {
    @Nonnull
    public static final Component[] EMPTY_ARRAY = new Component[0];

    @Nullable
    public Component<ECS_TYPE> clone();

    @Nullable
    default public Component<ECS_TYPE> cloneSerializable() {
        return this.clone();
    }
}

