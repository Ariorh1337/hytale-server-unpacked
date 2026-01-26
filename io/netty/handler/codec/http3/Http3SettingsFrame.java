/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3ControlStreamFrame;
import io.netty.handler.codec.http3.Http3SettingIdentifier;
import io.netty.handler.codec.http3.Http3Settings;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface Http3SettingsFrame
extends Http3ControlStreamFrame,
Iterable<Map.Entry<Long, Long>> {
    @Deprecated
    public static final long HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY = Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY.id();
    @Deprecated
    public static final long HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS = Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS.id();
    @Deprecated
    public static final long HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL = Http3SettingIdentifier.HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL.id();
    @Deprecated
    public static final long HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE = Http3SettingIdentifier.HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE.id();

    default public Http3Settings settings() {
        throw new UnsupportedOperationException("Http3SettingsFrame.settings() not implemented in this version");
    }

    @Override
    default public long type() {
        return 4L;
    }

    @Deprecated
    @Nullable
    default public Long get(long key) {
        return this.settings().get(key);
    }

    @Deprecated
    default public Long getOrDefault(long key, long defaultValue) {
        Long val = this.get(key);
        return val == null ? defaultValue : val;
    }

    @Nullable
    default public Long put(long key, Long value) {
        return this.settings().put(key, value);
    }
}

