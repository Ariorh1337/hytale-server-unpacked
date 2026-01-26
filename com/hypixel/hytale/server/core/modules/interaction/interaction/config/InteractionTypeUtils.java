/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config;

import com.hypixel.hytale.protocol.InteractionType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class InteractionTypeUtils {
    @Nonnull
    public static final Set<InteractionType> STANDARD_INPUT = EnumSet.of(InteractionType.Primary, new InteractionType[]{InteractionType.Secondary, InteractionType.Ability1, InteractionType.Ability2, InteractionType.Ability3, InteractionType.Use, InteractionType.Pick, InteractionType.SwapFrom, InteractionType.SwapTo});
    @Nonnull
    public static final Map<InteractionType, Set<InteractionType>> DEFAULT_INTERACTION_BLOCKED_BY = Collections.unmodifiableMap(new EnumMap(Map.ofEntries(Map.entry(InteractionType.Primary, STANDARD_INPUT), Map.entry(InteractionType.Secondary, STANDARD_INPUT), Map.entry(InteractionType.Ability1, STANDARD_INPUT), Map.entry(InteractionType.Ability2, STANDARD_INPUT), Map.entry(InteractionType.Ability3, STANDARD_INPUT), Map.entry(InteractionType.Use, STANDARD_INPUT), Map.entry(InteractionType.Pick, STANDARD_INPUT), Map.entry(InteractionType.Pickup, Collections.emptySet()), Map.entry(InteractionType.CollisionEnter, Collections.emptySet()), Map.entry(InteractionType.CollisionLeave, Collections.emptySet()), Map.entry(InteractionType.Collision, Collections.emptySet()), Map.entry(InteractionType.EntityStatEffect, Collections.emptySet()), Map.entry(InteractionType.Death, Collections.emptySet()), Map.entry(InteractionType.Wielding, Collections.emptySet()), Map.entry(InteractionType.SwapTo, EnumSet.of(InteractionType.SwapFrom, InteractionType.SwapTo)), Map.entry(InteractionType.SwapFrom, EnumSet.of(InteractionType.SwapFrom, InteractionType.SwapTo)), Map.entry(InteractionType.ProjectileSpawn, Collections.emptySet()), Map.entry(InteractionType.ProjectileBounce, Collections.emptySet()), Map.entry(InteractionType.ProjectileMiss, Collections.emptySet()), Map.entry(InteractionType.ProjectileHit, Collections.emptySet()), Map.entry(InteractionType.Held, Set.of(InteractionType.Held)), Map.entry(InteractionType.HeldOffhand, Set.of(InteractionType.HeldOffhand)), Map.entry(InteractionType.Equipped, Set.of(InteractionType.Equipped)), Map.entry(InteractionType.Dodge, Set.of(InteractionType.Dodge)), Map.entry(InteractionType.GameModeSwap, Set.of(InteractionType.GameModeSwap)))));
    public static final float DEFAULT_COOLDOWN = 0.35f;

    public static float getDefaultCooldown(@Nonnull InteractionType type) {
        return switch (type) {
            case InteractionType.CollisionEnter, InteractionType.CollisionLeave, InteractionType.ProjectileSpawn, InteractionType.ProjectileHit, InteractionType.ProjectileMiss, InteractionType.ProjectileBounce, InteractionType.GameModeSwap, InteractionType.EntityStatEffect, InteractionType.Pickup -> 0.0f;
            default -> 0.35f;
        };
    }

    public static boolean isCollisionType(@Nonnull InteractionType type) {
        return type == InteractionType.Collision || type == InteractionType.CollisionEnter || type == InteractionType.CollisionLeave;
    }
}

