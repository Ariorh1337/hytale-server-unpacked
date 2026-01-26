/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3Settings;
import io.netty.handler.codec.http3.Http3SettingsFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class DefaultHttp3SettingsFrame
implements Http3SettingsFrame {
    private final Http3Settings settings;

    public DefaultHttp3SettingsFrame(Http3Settings settings) {
        this.settings = ObjectUtil.checkNotNull(settings, "settings");
    }

    public DefaultHttp3SettingsFrame() {
        this.settings = new Http3Settings();
    }

    @Override
    public Http3Settings settings() {
        return this.settings;
    }

    @Override
    @Deprecated
    @Nullable
    public Long get(long key) {
        return this.settings.get(key);
    }

    @Override
    @Deprecated
    @Nullable
    public Long put(long key, Long value) {
        return this.settings.put(key, value);
    }

    @Override
    public Iterator<Map.Entry<Long, Long>> iterator() {
        return this.settings.iterator();
    }

    public int hashCode() {
        return this.settings.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultHttp3SettingsFrame that = (DefaultHttp3SettingsFrame)o;
        return that.settings.equals(this.settings);
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(settings=" + this.settings + ')';
    }

    public static DefaultHttp3SettingsFrame copyOf(Http3SettingsFrame settingsFrame) {
        DefaultHttp3SettingsFrame copy = new DefaultHttp3SettingsFrame();
        if (settingsFrame instanceof DefaultHttp3SettingsFrame) {
            copy.settings.putAll(((DefaultHttp3SettingsFrame)settingsFrame).settings);
        } else {
            for (Map.Entry entry : settingsFrame) {
                copy.put((Long)entry.getKey(), (Long)entry.getValue());
            }
        }
        return copy;
    }
}

