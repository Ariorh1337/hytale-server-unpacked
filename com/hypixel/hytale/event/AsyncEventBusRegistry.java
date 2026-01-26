/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

import com.hypixel.hytale.event.EventBusRegistry;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IAsyncEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.event.IProcessedEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AsyncEventBusRegistry<KeyType, EventType extends IAsyncEvent<KeyType>>
extends EventBusRegistry<KeyType, EventType, AsyncEventConsumerMap<EventType>> {
    @Nonnull
    public static final IEventDispatcher NO_OP = new IEventDispatcher<IAsyncEvent, CompletableFuture<IAsyncEvent>>(){

        @Override
        public boolean hasListener() {
            return false;
        }

        @Override
        @Nonnull
        public CompletableFuture<IAsyncEvent> dispatch(IAsyncEvent event) {
            return CompletableFuture.completedFuture(event);
        }
    };
    @Nonnull
    private final IEventDispatcher<EventType, CompletableFuture<EventType>> globalDispatcher = event -> {
        CompletableFuture<IAsyncEvent> future = CompletableFuture.completedFuture(event);
        CompletableFuture<IAsyncEvent> before = future;
        if (before == (future = this.dispatchGlobal(future))) {
            future = this.dispatchUnhandled(future);
        }
        return future;
    };

    public AsyncEventBusRegistry(@Nonnull HytaleLogger logger, @Nonnull Class<EventType> eventClass) {
        super(logger, eventClass, new AsyncEventConsumerMap(null), new AsyncEventConsumerMap(null));
        ((AsyncEventConsumerMap)this.global).registry = ((AsyncEventConsumerMap)this.unhandled).registry = this;
    }

    @Nonnull
    public EventRegistration<KeyType, EventType> registerAsync(short priority, @Nonnull KeyType key, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsync0(priority, key, function, function.toString());
    }

    @Nonnull
    private EventRegistration<KeyType, EventType> registerAsync0(short priority, @Nullable KeyType key, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function, @Nonnull String consumerString) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        Object k = key != null ? key : NULL;
        AsyncEventConsumerMap eventMap = this.map.computeIfAbsent(k, o -> new AsyncEventConsumerMap(this));
        AsyncEventConsumer<EventType> eventConsumer = new AsyncEventConsumer<EventType>(priority, consumerString, function);
        eventMap.add(eventConsumer);
        return new EventRegistration(this.eventClass, this::isAlive, () -> this.unregister(key, eventConsumer));
    }

    private void unregister(@Nullable KeyType key, @Nonnull AsyncEventConsumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        Object k = key != null ? key : NULL;
        AsyncEventConsumerMap eventMap = (AsyncEventConsumerMap)this.map.get(k);
        if (eventMap != null && !eventMap.remove(consumer)) {
            throw new IllegalArgumentException(String.valueOf(consumer));
        }
    }

    @Nonnull
    public EventRegistration<KeyType, EventType> registerAsyncGlobal(short priority, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsyncGlobal0(priority, function, function.toString());
    }

    @Nonnull
    private EventRegistration<KeyType, EventType> registerAsyncGlobal0(short priority, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function, @Nonnull String consumerString) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        AsyncEventConsumer<EventType> eventConsumer = new AsyncEventConsumer<EventType>(priority, consumerString, function);
        ((AsyncEventConsumerMap)this.global).add(eventConsumer);
        return new EventRegistration(this.eventClass, this::isAlive, () -> this.unregisterGlobal(eventConsumer));
    }

    private void unregisterGlobal(@Nonnull AsyncEventConsumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        if (!((AsyncEventConsumerMap)this.global).remove(consumer)) {
            throw new IllegalArgumentException(String.valueOf(consumer));
        }
    }

    @Nonnull
    public EventRegistration<KeyType, EventType> registerAsyncUnhandled(short priority, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
        return this.registerAsyncUnhandled0(priority, function, function.toString());
    }

    @Nonnull
    private EventRegistration<KeyType, EventType> registerAsyncUnhandled0(short priority, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function, @Nonnull String consumerString) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        AsyncEventConsumer<EventType> eventConsumer = new AsyncEventConsumer<EventType>(priority, consumerString, function);
        ((AsyncEventConsumerMap)this.unhandled).add(eventConsumer);
        return new EventRegistration(this.eventClass, this::isAlive, () -> this.unregisterUnhandled(eventConsumer));
    }

    private void unregisterUnhandled(@Nonnull AsyncEventConsumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        if (!((AsyncEventConsumerMap)this.unhandled).remove(consumer)) {
            throw new IllegalArgumentException(String.valueOf(consumer));
        }
    }

    private CompletableFuture<EventType> dispatchGlobal(@Nonnull CompletableFuture<EventType> future) {
        return this.dispatchEventMap(future, (AsyncEventConsumerMap)this.global, "Failed to dispatch event (global)");
    }

    private CompletableFuture<EventType> dispatchUnhandled(@Nonnull CompletableFuture<EventType> future) {
        return this.dispatchEventMap(future, (AsyncEventConsumerMap)this.unhandled, "Failed to dispatch event (unhandled)");
    }

    private CompletableFuture<EventType> dispatchEventMap(@Nonnull CompletableFuture<EventType> future, @Nonnull AsyncEventConsumerMap<EventType> eventMap, @Nonnull String s) {
        for (short priority : eventMap.getPriorities()) {
            List consumers = eventMap.get(priority);
            if (consumers == null) continue;
            for (AsyncEventConsumer consumer : consumers) {
                try {
                    Function theConsumer = this.timeEvents ? consumer.getTimedFunction() : consumer.getFunction();
                    future = theConsumer.apply(future).whenComplete((event, throwable) -> {
                        if (event instanceof IProcessedEvent) {
                            IProcessedEvent processedEvent = (IProcessedEvent)((Object)event);
                            processedEvent.processEvent(consumer.getConsumerString());
                        }
                        if (throwable != null) {
                            ((HytaleLogger.Api)this.logger.at(Level.SEVERE).withCause((Throwable)throwable)).log("%s %s to %s", s, event, consumer);
                        }
                    });
                }
                catch (Throwable t) {
                    ((HytaleLogger.Api)this.logger.at(Level.SEVERE).withCause(t)).log("%s %s to %s", s, future, consumer);
                }
            }
        }
        return future;
    }

    @Override
    @Nonnull
    public EventRegistration<KeyType, EventType> register(short priority, KeyType key, @Nonnull Consumer<EventType> consumer) {
        return this.registerAsync0(priority, key, f -> f.thenApply(e -> {
            consumer.accept(e);
            return e;
        }), consumer.toString());
    }

    @Override
    @Nonnull
    public EventRegistration<KeyType, EventType> registerGlobal(short priority, @Nonnull Consumer<EventType> consumer) {
        return this.registerAsyncGlobal0(priority, f -> f.thenApply(e -> {
            consumer.accept(e);
            return e;
        }), consumer.toString());
    }

    @Override
    @Nonnull
    public EventRegistration<KeyType, EventType> registerUnhandled(short priority, @Nonnull Consumer<EventType> consumer) {
        return this.registerAsyncUnhandled0(priority, f -> f.thenApply(e -> {
            consumer.accept(e);
            return e;
        }), consumer.toString());
    }

    @Override
    @Nonnull
    public IEventDispatcher<EventType, CompletableFuture<EventType>> dispatchFor(@Nullable KeyType key) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        Object k = key != null ? key : NULL;
        AsyncEventConsumerMap eventMap = (AsyncEventConsumerMap)this.map.get(k);
        if (eventMap != null && !eventMap.isEmpty()) {
            return eventMap;
        }
        if (((AsyncEventConsumerMap)this.global).isEmpty() && ((AsyncEventConsumerMap)this.unhandled).isEmpty()) {
            return NO_OP;
        }
        return this.globalDispatcher;
    }

    protected static class AsyncEventConsumerMap<EventType extends IAsyncEvent>
    extends EventBusRegistry.EventConsumerMap<EventType, AsyncEventConsumer<EventType>, CompletableFuture<EventType>> {
        protected AsyncEventBusRegistry registry;

        public AsyncEventConsumerMap(AsyncEventBusRegistry registry) {
            this.registry = registry;
        }

        @Override
        @Nonnull
        public CompletableFuture<EventType> dispatch(EventType event) {
            return CompletableFuture.completedFuture(event).thenComposeAsync(this::dispatch0);
        }

        private CompletableFuture<EventType> dispatch0(EventType event) {
            CompletableFuture<EventType> future;
            CompletableFuture<EventType> before = future = CompletableFuture.completedFuture(event);
            CompletableFuture<EventType> beforeGlobal = future = this.registry.dispatchEventMap(future, this, "Failed to dispatch event");
            if (beforeGlobal == (future = this.registry.dispatchGlobal(future)) && before == beforeGlobal) {
                future = this.registry.dispatchUnhandled(future);
            }
            return future;
        }
    }

    protected static class AsyncEventConsumer<EventType extends IAsyncEvent>
    extends EventBusRegistry.EventConsumer {
        @Nonnull
        private final Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function;
        @Nonnull
        private final Function<CompletableFuture<EventType>, CompletableFuture<EventType>> timedFunction;

        public AsyncEventConsumer(short priority, @Nonnull String consumerString, @Nonnull Function<CompletableFuture<EventType>, CompletableFuture<EventType>> function) {
            super(priority, consumerString);
            this.function = function;
            this.timedFunction = f -> {
                long before = System.nanoTime();
                return ((CompletableFuture)function.apply((CompletableFuture<EventType>)f)).whenComplete((eventType, throwable) -> {
                    long after = System.nanoTime();
                    this.timer.add(after - before);
                    if (throwable != null) {
                        throw SneakyThrow.sneakyThrow(throwable);
                    }
                });
            };
        }

        @Nonnull
        public Function<CompletableFuture<EventType>, CompletableFuture<EventType>> getFunction() {
            return this.function;
        }

        @Nonnull
        public Function<CompletableFuture<EventType>, CompletableFuture<EventType>> getTimedFunction() {
            return this.timedFunction;
        }

        @Override
        @Nonnull
        public String toString() {
            return "AsyncEventConsumer{function=" + String.valueOf(this.function) + ", timedFunction=" + String.valueOf(this.timedFunction) + "} " + super.toString();
        }
    }
}

