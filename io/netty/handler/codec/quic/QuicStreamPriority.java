/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;
import java.util.Objects;

public final class QuicStreamPriority {
    private final int urgency;
    private final boolean incremental;

    public QuicStreamPriority(int urgency, boolean incremental) {
        this.urgency = ObjectUtil.checkInRange(urgency, 0, 127, "urgency");
        this.incremental = incremental;
    }

    public int urgency() {
        return this.urgency;
    }

    public boolean isIncremental() {
        return this.incremental;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QuicStreamPriority that = (QuicStreamPriority)o;
        return this.urgency == that.urgency && this.incremental == that.incremental;
    }

    public int hashCode() {
        return Objects.hash(this.urgency, this.incremental);
    }

    public String toString() {
        return "QuicStreamPriority{urgency=" + this.urgency + ", incremental=" + this.incremental + '}';
    }
}

