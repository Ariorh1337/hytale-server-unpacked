/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

import com.hypixel.hytale.event.IAsyncEvent;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.event.IEventRegistry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IEventBus
extends IEventRegistry {
    default public <KeyType, EventType extends IEvent<KeyType>> EventType dispatch(@Nonnull Class<EventType> eventClass) {
        return (EventType)((IEvent)this.dispatchFor(eventClass, null).dispatch(null));
    }

    default public <EventType extends IAsyncEvent<Void>> CompletableFuture<EventType> dispatchAsync(@Nonnull Class<EventType> eventClass) {
        return this.dispatchForAsync(eventClass).dispatch(null);
    }

    default public <KeyType, EventType extends IEvent<KeyType>> IEventDispatcher<EventType, EventType> dispatchFor(@Nonnull Class<? super EventType> eventClass) {
        return this.dispatchFor(eventClass, null);
    }

    public <KeyType, EventType extends IEvent<KeyType>> IEventDispatcher<EventType, EventType> dispatchFor(@Nonnull Class<? super EventType> var1, @Nullable KeyType var2);

    default public <KeyType, EventType extends IAsyncEvent<KeyType>> IEventDispatcher<EventType, CompletableFuture<EventType>> dispatchForAsync(@Nonnull Class<? super EventType> eventClass) {
        return this.dispatchForAsync(eventClass, null);
    }

    public <KeyType, EventType extends IAsyncEvent<KeyType>> IEventDispatcher<EventType, CompletableFuture<EventType>> dispatchForAsync(@Nonnull Class<? super EventType> var1, @Nullable KeyType var2);
}

