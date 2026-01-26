/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.DefaultHttp3Headers;
import io.netty.handler.codec.http3.Http3Headers;
import io.netty.handler.codec.http3.Http3PushPromiseFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Objects;

public final class DefaultHttp3PushPromiseFrame
implements Http3PushPromiseFrame {
    private final long id;
    private final Http3Headers headers;

    public DefaultHttp3PushPromiseFrame(long id) {
        this(id, new DefaultHttp3Headers());
    }

    public DefaultHttp3PushPromiseFrame(long id, Http3Headers headers) {
        this.id = ObjectUtil.checkPositiveOrZero(id, "id");
        this.headers = ObjectUtil.checkNotNull(headers, "headers");
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public Http3Headers headers() {
        return this.headers;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultHttp3PushPromiseFrame that = (DefaultHttp3PushPromiseFrame)o;
        return this.id == that.id && Objects.equals(this.headers, that.headers);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.headers);
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(id=" + this.id() + ", headers=" + this.headers() + ')';
    }
}

