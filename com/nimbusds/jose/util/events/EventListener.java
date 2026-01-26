/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util.events;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.events.Event;

public interface EventListener<S, C extends SecurityContext> {
    public void notify(Event<S, C> var1);
}

