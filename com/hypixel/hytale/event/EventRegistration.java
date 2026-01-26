/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.event;

import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.registry.Registration;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

public class EventRegistration<KeyType, EventType extends IBaseEvent<KeyType>>
extends Registration {
    @Nonnull
    protected final Class<EventType> eventClass;

    public EventRegistration(@Nonnull Class<EventType> eventClass, @Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.eventClass = eventClass;
    }

    public EventRegistration(@Nonnull EventRegistration<KeyType, EventType> registration, @Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.eventClass = registration.eventClass;
    }

    @Nonnull
    public Class<EventType> getEventClass() {
        return this.eventClass;
    }

    @Override
    @Nonnull
    public String toString() {
        return "EventRegistration{eventClass=" + String.valueOf(this.eventClass) + ", " + super.toString() + "}";
    }

    @Nonnull
    @SafeVarargs
    public static <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> combine(@Nonnull EventRegistration<KeyType, EventType> thisRegistration, EventRegistration<KeyType, EventType> ... containerRegistrations) {
        return new EventRegistration<KeyType, EventType>(thisRegistration.eventClass, () -> {
            if (!thisRegistration.isEnabled.getAsBoolean()) {
                return false;
            }
            for (EventRegistration containerRegistration : containerRegistrations) {
                if (containerRegistration.isEnabled.getAsBoolean()) continue;
                return false;
            }
            return true;
        }, () -> {
            thisRegistration.unregister();
            for (EventRegistration containerRegistration : containerRegistrations) {
                containerRegistration.unregister();
            }
        });
    }
}

