/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.instructions.RoleStateChange;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nonnull;

public interface IEntityFilter
extends RoleStateChange,
IAnnotatedComponent {
    public static final IEntityFilter[] EMPTY_ARRAY = new IEntityFilter[0];
    public static final int MINIMAL_COST = 0;
    public static final int LOW_COST = 100;
    public static final int MID_COST = 200;
    public static final int HIGH_COST = 300;
    public static final int EXTREME_COST = 400;

    public boolean matchesEntity(@Nonnull Ref<EntityStore> var1, @Nonnull Ref<EntityStore> var2, @Nonnull Role var3, @Nonnull Store<EntityStore> var4);

    public int cost();

    public static void prioritiseFilters(@Nonnull IEntityFilter[] filters) {
        Arrays.sort(filters, Comparator.comparingInt(IEntityFilter::cost));
    }
}

