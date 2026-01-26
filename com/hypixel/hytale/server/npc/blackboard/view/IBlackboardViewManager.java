/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.IBlackboardView;
import java.util.function.Consumer;

public interface IBlackboardViewManager<View extends IBlackboardView<View>> {
    public View get(Ref<EntityStore> var1, Blackboard var2, ComponentAccessor<EntityStore> var3);

    public View get(Vector3d var1, Blackboard var2);

    public View get(int var1, int var2, Blackboard var3);

    public View get(long var1, Blackboard var3);

    public View getIfExists(long var1);

    public void cleanup();

    public void onWorldRemoved();

    public void forEachView(Consumer<View> var1);

    public void clear();
}

