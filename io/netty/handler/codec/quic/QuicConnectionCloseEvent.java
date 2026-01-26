/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicEvent;
import java.util.Arrays;

public final class QuicConnectionCloseEvent
implements QuicEvent {
    final boolean applicationClose;
    final int error;
    final byte[] reason;

    QuicConnectionCloseEvent(boolean applicationClose, int error, byte[] reason) {
        this.applicationClose = applicationClose;
        this.error = error;
        this.reason = reason;
    }

    public boolean isApplicationClose() {
        return this.applicationClose;
    }

    public int error() {
        return this.error;
    }

    public boolean isTlsError() {
        return !this.applicationClose && this.error >= 256;
    }

    public byte[] reason() {
        return (byte[])this.reason.clone();
    }

    public String toString() {
        return "QuicConnectionCloseEvent{applicationClose=" + this.applicationClose + ", error=" + this.error + ", reason=" + Arrays.toString(this.reason) + '}';
    }

    public static int extractTlsError(int error) {
        int tlsError = error - 256;
        if (tlsError < 0) {
            return -1;
        }
        return tlsError;
    }
}

