/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.shaders;

import com.hypixel.hytale.builtin.hytalegenerator.framework.shaders.Shader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class RelationalShader<T>
implements Shader<T> {
    @Nonnull
    private final Map<T, Shader<T>> relations;
    @Nonnull
    private final Shader<T> onMissingKey;

    public RelationalShader(@Nonnull Shader<T> onMissingKey) {
        this.onMissingKey = onMissingKey;
        this.relations = new HashMap<T, Shader<T>>(1);
    }

    @Nonnull
    public RelationalShader<T> addRelation(@Nonnull T key, @Nonnull Shader<T> value) {
        this.relations.put(key, value);
        return this;
    }

    @Override
    public T shade(T current, long seed) {
        if (!this.relations.containsKey(current)) {
            return this.onMissingKey.shade(current, seed);
        }
        return this.relations.get(current).shade(current, seed);
    }

    @Override
    public T shade(T current, long seedA, long seedB) {
        if (!this.relations.containsKey(current)) {
            return this.onMissingKey.shade(current, seedA, seedB);
        }
        return this.relations.get(current).shade(current, seedA, seedB);
    }

    @Override
    public T shade(T current, long seedA, long seedB, long seedC) {
        if (!this.relations.containsKey(current)) {
            return this.onMissingKey.shade(current, seedA, seedB, seedC);
        }
        return this.relations.get(current).shade(current, seedA, seedB, seedC);
    }

    @Nonnull
    public String toString() {
        return "RelationalShader{relations=" + String.valueOf(this.relations) + ", onMissingKey=" + String.valueOf(this.onMissingKey) + "}";
    }
}

