/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicConnectionCloseEvent;
import java.nio.channels.ClosedChannelException;
import org.jetbrains.annotations.Nullable;

public final class QuicClosedChannelException
extends ClosedChannelException {
    private final QuicConnectionCloseEvent event;

    QuicClosedChannelException(@Nullable QuicConnectionCloseEvent event) {
        this.event = event;
    }

    @Nullable
    public QuicConnectionCloseEvent event() {
        return this.event;
    }
}

