/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3CancelPushFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Objects;

public final class DefaultHttp3CancelPushFrame
implements Http3CancelPushFrame {
    private final long id;

    public DefaultHttp3CancelPushFrame(long id) {
        this.id = ObjectUtil.checkPositiveOrZero(id, "id");
    }

    @Override
    public long id() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultHttp3CancelPushFrame that = (DefaultHttp3CancelPushFrame)o;
        return this.id == that.id;
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(id=" + this.id() + ')';
    }
}

