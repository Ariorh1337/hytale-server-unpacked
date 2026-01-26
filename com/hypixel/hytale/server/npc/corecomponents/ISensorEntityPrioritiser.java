/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.instructions.RoleStateChange;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.util.IEntityByPriorityFilter;
import java.util.List;

public interface ISensorEntityPrioritiser
extends RoleStateChange {
    public IEntityByPriorityFilter getNPCPrioritiser();

    public IEntityByPriorityFilter getPlayerPrioritiser();

    public Ref<EntityStore> pickTarget(Ref<EntityStore> var1, Role var2, Vector3d var3, Ref<EntityStore> var4, Ref<EntityStore> var5, boolean var6, Store<EntityStore> var7);

    public boolean providesFilters();

    public void buildProvidedFilters(List<IEntityFilter> var1);
}

