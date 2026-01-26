/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util.events;

import com.nimbusds.jose.proc.SecurityContext;

public interface Event<S, C extends SecurityContext> {
    public S getSource();

    public C getContext();
}

