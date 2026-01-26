/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

import com.hypixel.hytale.event.AsyncEventBusRegistry;
import com.hypixel.hytale.event.EventBusRegistry;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IAsyncEvent;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventBus;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.event.SyncEventBusRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventBus
implements IEventBus {
    @Nonnull
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    private final Map<Class<? extends IBaseEvent<?>>, EventBusRegistry<?, ?, ?>> registryMap = new ConcurrentHashMap();
    private final boolean timeEvents;

    public EventBus(boolean timeEvents) {
        this.timeEvents = timeEvents;
    }

    public void shutdown() {
        this.registryMap.values().forEach(EventBusRegistry::shutdown);
    }

    @Nonnull
    public Set<Class<? extends IBaseEvent<?>>> getRegisteredEventClasses() {
        return new HashSet(this.registryMap.keySet());
    }

    @Nonnull
    public Set<String> getRegisteredEventClassNames() {
        HashSet<String> classNames = new HashSet<String>();
        for (Class<IBaseEvent<?>> aClass : this.registryMap.keySet()) {
            classNames.add(aClass.getSimpleName());
        }
        return classNames;
    }

    @Nullable
    public EventBusRegistry<?, ?, ?> getRegistry(@Nonnull String eventName) {
        Class<IBaseEvent<?>> eventClass = null;
        for (Class<IBaseEvent<?>> aClass : this.registryMap.keySet()) {
            if (!aClass.getSimpleName().equalsIgnoreCase(eventName) && !aClass.getName().equalsIgnoreCase(eventName)) continue;
            eventClass = aClass;
        }
        return eventClass == null ? null : this.getRegistry(eventClass);
    }

    @Nonnull
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventBusRegistry<KeyType, EventType, ?> getRegistry(@Nonnull Class<? super EventType> eventClass) {
        if (IAsyncEvent.class.isAssignableFrom(eventClass)) {
            return this.getAsyncRegistry(eventClass);
        }
        return this.getSyncRegistry(eventClass);
    }

    @Nonnull
    public <KeyType, EventType extends IEvent<KeyType>> EventBusRegistry<KeyType, EventType, ?> getSyncRegistry(@Nonnull Class<? super EventType> eventClass) {
        EventBusRegistry registry = this.registryMap.computeIfAbsent(eventClass, aClass -> new SyncEventBusRegistry(LOGGER, aClass));
        if (this.timeEvents) {
            registry.setTimeEvents(true);
        }
        return registry;
    }

    @Nonnull
    private <KeyType, EventType extends IAsyncEvent<KeyType>> AsyncEventBusRegistry<KeyType, EventType> getAsyncRegistry(@Nonnull Class<? super EventType> eventClass) {
        EventBusRegistry registry = this.registryMap.computeIfAbsent(eventClass, aClass -> new AsyncEventBusRegistry(LOGGER, aClass));
        if (this.timeEvents) {
            registry.setTimeEvents(true);
        }
        return (AsyncEventBusRegistry)registry;
    }

    @Override
    public <EventType extends IBaseEvent<Void>> EventRegistration<Void, EventType> register(@Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.register((short)0, eventClass, null, consumer);
    }

    @Override
    public <EventType extends IBaseEvent<Void>> EventRegistration<Void, EventType> register(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.register(priority.getValue(), eventClass, null, consumer);
    }

    @Override
    public <EventType extends IBaseEvent<Void>> EventRegistration<Void, EventType> register(short priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.register(priority, eventClass, null, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> register(@Nonnull Class<? super EventType> eventClass, @Nonnull KeyType key, @Nonnull Consumer<EventType> consumer) {
        return this.register((short)0, eventClass, key, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> register(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull KeyType key, @Nonnull Consumer<EventType> consumer) {
        return this.register(priority.getValue(), eventClass, key, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> register(short priority, @Nonnull Class<? super EventType> eventClass, @Nullable KeyType key, @Nonnull Consumer<EventType> consumer) {
        return this.getRegistry(eventClass).register(priority, key, consumer);
    }

    @Override
    public <EventType extends IAsyncEvent<Void>> EventRegistration<Void, EventType> registerAsync(@Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsync((short)0, eventClass, null, function);
    }

    @Override
    public <EventType extends IAsyncEvent<Void>> EventRegistration<Void, EventType> registerAsync(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsync(priority.getValue(), eventClass, null, function);
    }

    @Override
    public <EventType extends IAsyncEvent<Void>> EventRegistration<Void, EventType> registerAsync(short priority, Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsync(priority, eventClass, null, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsync(@Nonnull Class<? super EventType> eventClass, @Nonnull KeyType key, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsync((short)0, eventClass, key, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsync(@Nonnull EventPriority priority, Class<? super EventType> eventClass, @Nonnull KeyType key, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsync(priority.getValue(), eventClass, key, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsync(short priority, @Nonnull Class<? super EventType> eventClass, @Nullable KeyType key, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.getAsyncRegistry(eventClass).registerAsync(priority, key, function);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerGlobal(@Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.registerGlobal((short)0, eventClass, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerGlobal(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.registerGlobal(priority.getValue(), eventClass, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerGlobal(short priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.getRegistry(eventClass).registerGlobal(priority, consumer);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsyncGlobal(@Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsyncGlobal((short)0, eventClass, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsyncGlobal(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsyncGlobal(priority.getValue(), eventClass, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsyncGlobal(short priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.getAsyncRegistry(eventClass).registerAsyncGlobal(priority, function);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerUnhandled(@Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.registerUnhandled((short)0, eventClass, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerUnhandled(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.registerUnhandled(priority.getValue(), eventClass, consumer);
    }

    @Override
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerUnhandled(short priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Consumer<EventType> consumer) {
        return this.getRegistry(eventClass).registerUnhandled(priority, consumer);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsyncUnhandled(@Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsyncUnhandled((short)0, eventClass, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsyncUnhandled(@Nonnull EventPriority priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsyncUnhandled(priority.getValue(), eventClass, function);
    }

    @Override
    public <KeyType, EventType extends IAsyncEvent<KeyType>> EventRegistration<KeyType, EventType> registerAsyncUnhandled(short priority, @Nonnull Class<? super EventType> eventClass, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.getAsyncRegistry(eventClass).registerAsyncUnhandled(priority, function);
    }

    @Override
    @Nonnull
    public <KeyType, EventType extends IEvent<KeyType>> IEventDispatcher<EventType, EventType> dispatchFor(@Nonnull Class<? super EventType> eventClass, KeyType key) {
        SyncEventBusRegistry registry = (SyncEventBusRegistry)this.registryMap.get(eventClass);
        if (registry == null) {
            return SyncEventBusRegistry.NO_OP;
        }
        return registry.dispatchFor(key);
    }

    @Override
    @Nonnull
    public <KeyType, EventType extends IAsyncEvent<KeyType>> IEventDispatcher<EventType, CompletableFuture<EventType>> dispatchForAsync(@Nonnull Class<? super EventType> eventClass, KeyType key) {
        AsyncEventBusRegistry registry = (AsyncEventBusRegistry)this.registryMap.get(eventClass);
        if (registry == null) {
            return AsyncEventBusRegistry.NO_OP;
        }
        return registry.dispatchFor(key);
    }
}

