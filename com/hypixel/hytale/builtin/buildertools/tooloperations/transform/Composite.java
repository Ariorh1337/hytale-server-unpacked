/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.tooloperations.transform;

import com.hypixel.hytale.builtin.buildertools.tooloperations.transform.Transform;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class Composite
implements Transform {
    private final Transform first;
    private final Transform second;

    private Composite(Transform first, Transform second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void apply(Vector3i vector3i) {
        this.first.apply(vector3i);
        this.second.apply(vector3i);
    }

    @Nonnull
    public String toString() {
        return "Composite{first=" + String.valueOf(this.first) + ", second=" + String.valueOf(this.second) + "}";
    }

    public static Transform of(Transform first, Transform second) {
        if (first == NONE && second == NONE) {
            return NONE;
        }
        if (first == NONE) {
            return second;
        }
        if (second == NONE) {
            return first;
        }
        return new Composite(first, second);
    }
}

