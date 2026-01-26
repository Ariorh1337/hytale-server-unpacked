/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicEvent;

public final class QuicStreamLimitChangedEvent
implements QuicEvent {
    static final QuicStreamLimitChangedEvent INSTANCE = new QuicStreamLimitChangedEvent();

    private QuicStreamLimitChangedEvent() {
    }
}

