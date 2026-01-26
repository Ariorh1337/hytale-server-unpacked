/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import javax.annotation.Nullable;

public interface Resource<ECS_TYPE>
extends Cloneable {
    public static final Resource[] EMPTY_ARRAY = new Resource[0];

    @Nullable
    public Resource<ECS_TYPE> clone();
}

