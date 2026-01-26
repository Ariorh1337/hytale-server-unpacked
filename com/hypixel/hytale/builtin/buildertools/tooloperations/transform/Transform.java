/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.tooloperations.transform;

import com.hypixel.hytale.builtin.buildertools.tooloperations.transform.Composite;
import com.hypixel.hytale.math.vector.Vector3i;

public interface Transform {
    public static final Transform NONE = vec -> {};

    public void apply(Vector3i var1);

    default public Transform then(Transform next) {
        return Composite.of(this, next);
    }
}

