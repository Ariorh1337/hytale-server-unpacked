/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.referencebundle;

import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.Reference;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReferenceBundle {
    @Nonnull
    private final Map<String, Reference> dataLayerMap = new HashMap<String, Reference>();
    @Nonnull
    private final Map<String, String> layerTypeMap = new HashMap<String, String>();

    public <T extends Reference> void put(@Nonnull String name, @Nonnull Reference reference, @Nonnull Class<T> type) {
        this.dataLayerMap.put(name, reference);
        this.layerTypeMap.put(name, type.getName());
    }

    @Nullable
    public Reference getLayerWithName(@Nonnull String name) {
        return this.dataLayerMap.get(name);
    }

    @Nullable
    public <T extends Reference> T getLayerWithName(@Nonnull String name, @Nonnull Class<T> type) {
        String storedType = this.layerTypeMap.get(name);
        if (storedType == null) {
            return null;
        }
        if (!storedType.equals(type.getName())) {
            return null;
        }
        return (T)this.dataLayerMap.get(name);
    }
}

