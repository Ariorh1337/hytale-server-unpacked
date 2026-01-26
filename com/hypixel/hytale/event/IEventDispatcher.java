/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

import com.hypixel.hytale.event.IBaseEvent;
import javax.annotation.Nullable;

public interface IEventDispatcher<EventType extends IBaseEvent, ReturnType> {
    default public boolean hasListener() {
        return true;
    }

    public ReturnType dispatch(@Nullable EventType var1);
}

