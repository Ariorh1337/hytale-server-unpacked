/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

import com.hypixel.hytale.event.EventBusRegistry;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.event.IProcessedEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyncEventBusRegistry<KeyType, EventType extends IEvent<KeyType>>
extends EventBusRegistry<KeyType, EventType, SyncEventConsumerMap<EventType>> {
    public static final IEventDispatcher NO_OP = new IEventDispatcher<IBaseEvent, IBaseEvent>(){

        @Override
        public boolean hasListener() {
            return false;
        }

        @Override
        public IBaseEvent dispatch(IBaseEvent event) {
            return event;
        }
    };
    private final IEventDispatcher<EventType, EventType> globalDispatcher = event -> {
        if (!this.dispatchGlobal(event)) {
            this.dispatchUnhandled(event);
        }
        return event;
    };

    public SyncEventBusRegistry(HytaleLogger logger, Class<EventType> eventClass) {
        super(logger, eventClass, new SyncEventConsumerMap(null), new SyncEventConsumerMap(null));
        ((SyncEventConsumerMap)this.global).registry = ((SyncEventConsumerMap)this.unhandled).registry = this;
    }

    @Override
    @Nonnull
    public EventRegistration<KeyType, EventType> register(short priority, @Nullable KeyType key, @Nonnull Consumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        Object k = key != null ? key : NULL;
        SyncEventConsumerMap eventMap = this.map.computeIfAbsent(k, o -> new SyncEventConsumerMap(this));
        SyncEventConsumer<EventType> eventConsumer = new SyncEventConsumer<EventType>(priority, consumer);
        eventMap.add(eventConsumer);
        return new EventRegistration(this.eventClass, this::isAlive, () -> this.unregister(key, eventConsumer));
    }

    private void unregister(@Nullable KeyType key, @Nonnull SyncEventConsumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        Object k = key != null ? key : NULL;
        SyncEventConsumerMap eventMap = (SyncEventConsumerMap)this.map.get(k);
        if (eventMap != null && !eventMap.remove(consumer)) {
            throw new IllegalArgumentException(String.valueOf(consumer));
        }
    }

    @Override
    @Nonnull
    public EventRegistration<KeyType, EventType> registerGlobal(short priority, @Nonnull Consumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        SyncEventConsumer<EventType> eventConsumer = new SyncEventConsumer<EventType>(priority, consumer);
        ((SyncEventConsumerMap)this.global).add(eventConsumer);
        return new EventRegistration(this.eventClass, this::isAlive, () -> this.unregisterGlobal(eventConsumer));
    }

    private void unregisterGlobal(@Nonnull SyncEventConsumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        if (!((SyncEventConsumerMap)this.global).remove(consumer)) {
            throw new IllegalArgumentException(String.valueOf(consumer));
        }
    }

    @Override
    @Nonnull
    public EventRegistration<KeyType, EventType> registerUnhandled(short priority, @Nonnull Consumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        SyncEventConsumer<EventType> eventConsumer = new SyncEventConsumer<EventType>(priority, consumer);
        ((SyncEventConsumerMap)this.unhandled).add(eventConsumer);
        return new EventRegistration(this.eventClass, this::isAlive, () -> this.unregisterUnhandled(eventConsumer));
    }

    private void unregisterUnhandled(@Nonnull SyncEventConsumer<EventType> consumer) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        if (!((SyncEventConsumerMap)this.unhandled).remove(consumer)) {
            throw new IllegalArgumentException(String.valueOf(consumer));
        }
    }

    @Override
    @Nonnull
    public IEventDispatcher<EventType, EventType> dispatchFor(@Nullable KeyType key) {
        if (this.shutdown) {
            throw new IllegalArgumentException("EventRegistry is shutdown!");
        }
        Object k = key != null ? key : NULL;
        SyncEventConsumerMap eventMap = (SyncEventConsumerMap)this.map.get(k);
        if (eventMap != null && !eventMap.isEmpty()) {
            return eventMap;
        }
        if (((SyncEventConsumerMap)this.global).isEmpty() && ((SyncEventConsumerMap)this.unhandled).isEmpty()) {
            return NO_OP;
        }
        return this.globalDispatcher;
    }

    private boolean dispatchGlobal(EventType event) {
        return this.dispatchEventMap(event, (SyncEventConsumerMap)this.global, "Failed to dispatch event (global)");
    }

    private boolean dispatchUnhandled(EventType event) {
        return this.dispatchEventMap(event, (SyncEventConsumerMap)this.unhandled, "Failed to dispatch event (unhandled)");
    }

    private boolean dispatchEventMap(EventType event, @Nonnull SyncEventConsumerMap<EventType> eventMap, String s) {
        boolean handled = false;
        for (short priority : eventMap.getPriorities()) {
            List consumers = eventMap.get(priority);
            if (consumers == null) continue;
            for (SyncEventConsumer consumer : consumers) {
                try {
                    Consumer theConsumer = this.timeEvents ? consumer.getTimedConsumer() : consumer.getConsumer();
                    theConsumer.accept(event);
                    if (event instanceof IProcessedEvent) {
                        IProcessedEvent processedEvent = (IProcessedEvent)event;
                        processedEvent.processEvent(consumer.getConsumerString());
                    }
                    handled = true;
                }
                catch (Throwable t) {
                    ((HytaleLogger.Api)this.logger.at(Level.SEVERE).withCause(t)).log("%s %s to %s", s, event, consumer);
                }
            }
        }
        return handled;
    }

    protected static class SyncEventConsumerMap<EventType extends IEvent>
    extends EventBusRegistry.EventConsumerMap<EventType, SyncEventConsumer<EventType>, EventType> {
        protected SyncEventBusRegistry registry;

        public SyncEventConsumerMap(SyncEventBusRegistry registry) {
            this.registry = registry;
        }

        @Override
        public EventType dispatch(EventType event) {
            boolean handled = this.registry.dispatchEventMap(event, this, "Failed to dispatch event");
            if (!this.registry.dispatchGlobal(event) && !handled) {
                this.registry.dispatchUnhandled(event);
            }
            return event;
        }
    }

    protected static class SyncEventConsumer<EventType extends IEvent>
    extends EventBusRegistry.EventConsumer {
        @Nonnull
        private final Consumer<EventType> consumer;
        @Nonnull
        private final Consumer<EventType> timedConsumer;

        public SyncEventConsumer(short priority, @Nonnull Consumer<EventType> consumer) {
            super(priority, consumer.toString());
            this.consumer = consumer;
            this.timedConsumer = t -> {
                long before = System.nanoTime();
                consumer.accept(t);
                long after = System.nanoTime();
                this.timer.add(after - before);
            };
        }

        @Nonnull
        protected Consumer<EventType> getConsumer() {
            return this.consumer;
        }

        @Nonnull
        public Consumer<EventType> getTimedConsumer() {
            return this.timedConsumer;
        }

        @Override
        @Nonnull
        public String toString() {
            return "SyncEventConsumer{consumer=" + String.valueOf(this.consumer) + ", timedConsumer=" + String.valueOf(this.timedConsumer) + "} " + super.toString();
        }
    }
}

