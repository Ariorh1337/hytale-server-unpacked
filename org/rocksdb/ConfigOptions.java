/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Env;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;
import org.rocksdb.SanityLevel;

public class ConfigOptions
extends RocksObject {
    public ConfigOptions() {
        super(ConfigOptions.newConfigOptionsInstance());
    }

    public ConfigOptions setDelimiter(String string) {
        ConfigOptions.setDelimiter(this.nativeHandle_, string);
        return this;
    }

    public ConfigOptions setIgnoreUnknownOptions(boolean bl) {
        ConfigOptions.setIgnoreUnknownOptions(this.nativeHandle_, bl);
        return this;
    }

    public ConfigOptions setEnv(Env env) {
        ConfigOptions.setEnv(this.nativeHandle_, env.nativeHandle_);
        return this;
    }

    public ConfigOptions setInputStringsEscaped(boolean bl) {
        ConfigOptions.setInputStringsEscaped(this.nativeHandle_, bl);
        return this;
    }

    public ConfigOptions setSanityLevel(SanityLevel sanityLevel) {
        ConfigOptions.setSanityLevel(this.nativeHandle_, sanityLevel.getValue());
        return this;
    }

    @Override
    protected final void disposeInternal(long l) {
        ConfigOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static long newConfigOptionsInstance() {
        RocksDB.loadLibrary();
        return ConfigOptions.newConfigOptions();
    }

    private static native long newConfigOptions();

    private static native void setEnv(long var0, long var2);

    private static native void setDelimiter(long var0, String var2);

    private static native void setIgnoreUnknownOptions(long var0, boolean var2);

    private static native void setInputStringsEscaped(long var0, boolean var2);

    private static native void setSanityLevel(long var0, byte var2);
}

