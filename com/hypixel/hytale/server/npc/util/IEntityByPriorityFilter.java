/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.predicate.TriPredicate;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nullable;

public interface IEntityByPriorityFilter
extends TriPredicate<Ref<EntityStore>, Ref<EntityStore>, ComponentAccessor<EntityStore>> {
    public void init(Role var1);

    @Nullable
    public Ref<EntityStore> getHighestPriorityTarget();

    public void cleanup();
}

