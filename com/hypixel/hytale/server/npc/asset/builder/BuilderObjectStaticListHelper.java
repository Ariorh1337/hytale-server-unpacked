/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectListHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectStaticHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderObjectStaticListHelper<T>
extends BuilderObjectListHelper<T> {
    public BuilderObjectStaticListHelper(Class<?> classType, BuilderContext owner) {
        super(classType, owner);
    }

    @Override
    @Nonnull
    protected BuilderObjectReferenceHelper<T> createReferenceHelper() {
        return new BuilderObjectStaticHelper(this.classType, this);
    }

    @Nullable
    public List<T> staticBuild(@Nonnull BuilderManager manager) {
        if (this.hasNoElements()) {
            return null;
        }
        ObjectArrayList objects = new ObjectArrayList();
        for (BuilderObjectReferenceHelper builder : this.builders) {
            Object obj = ((BuilderObjectStaticHelper)builder).staticBuild(manager);
            if (obj == null) continue;
            objects.add(obj);
        }
        return objects;
    }
}

