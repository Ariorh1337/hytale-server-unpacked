/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.animations;

import com.hypixel.hytale.protocol.AnimationSlot;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

@Deprecated
public enum NPCAnimationSlot implements Supplier<String>
{
    Status(AnimationSlot.Status),
    Action(AnimationSlot.Action),
    Face(AnimationSlot.Face);

    public static final NPCAnimationSlot[] VALUES;
    private final AnimationSlot mappedSlot;

    private NPCAnimationSlot(AnimationSlot mappedSlot) {
        this.mappedSlot = mappedSlot;
    }

    @Override
    @Nonnull
    public String get() {
        return this.name();
    }

    public AnimationSlot getMappedSlot() {
        return this.mappedSlot;
    }

    static {
        VALUES = NPCAnimationSlot.values();
    }
}

