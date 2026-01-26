/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectArrayHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderObjectListHelper<T>
extends BuilderObjectArrayHelper<List<T>, T> {
    public BuilderObjectListHelper(Class<?> classType, BuilderContext owner) {
        super(classType, owner);
    }

    @Override
    @Nullable
    public List<T> build(@Nonnull BuilderSupport builderSupport) {
        if (this.hasNoElements()) {
            return null;
        }
        ObjectArrayList objects = new ObjectArrayList();
        for (BuilderObjectReferenceHelper builder : this.builders) {
            Object obj;
            if (builder.excludeFromRegularBuild() || (obj = builder.build(builderSupport)) == null) continue;
            objects.add(obj);
        }
        return objects;
    }
}

