/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.lookup;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.builder.StringTreeMap;
import com.hypixel.hytale.codec.lookup.ACodecMapCodec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import java.util.concurrent.locks.StampedLock;
import javax.annotation.Nonnull;

public abstract class StringCodecMapCodec<T, C extends Codec<? extends T>>
extends ACodecMapCodec<String, T, C> {
    protected final StampedLock stampedLock = new StampedLock();
    protected final StringTreeMap<C> stringTreeMap = new StringTreeMap();

    public StringCodecMapCodec() {
        super(Codec.STRING);
    }

    public StringCodecMapCodec(boolean allowDefault) {
        super(Codec.STRING, allowDefault);
    }

    public StringCodecMapCodec(String id) {
        super(id, Codec.STRING);
    }

    public StringCodecMapCodec(String key, boolean allowDefault) {
        super(key, Codec.STRING, allowDefault);
    }

    public StringCodecMapCodec(String key, boolean allowDefault, boolean encodeDefaultKey) {
        super(key, Codec.STRING, allowDefault, encodeDefaultKey);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StringCodecMapCodec<T, C> register(@Nonnull Priority priority, @Nonnull String id, Class<? extends T> aClass, C codec) {
        long lock = this.stampedLock.readLock();
        try {
            this.stringTreeMap.put(id, codec);
        }
        finally {
            this.stampedLock.unlockRead(lock);
        }
        return (StringCodecMapCodec)super.register(priority, id, aClass, codec);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(Class<? extends T> aClass) {
        String id = (String)this.classToId.get(aClass);
        if (id == null) {
            return;
        }
        long lock = this.stampedLock.readLock();
        try {
            this.stringTreeMap.remove(id);
        }
        finally {
            this.stampedLock.unlockRead(lock);
        }
        super.remove(aClass);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.mark();
        Codec codec = null;
        int distance = 0;
        if (RawJsonReader.seekToKey(reader, this.key)) {
            distance = reader.getMarkDistance();
            long lock = this.stampedLock.readLock();
            try {
                StringTreeMap<C> entry = this.stringTreeMap.findEntry(reader);
                codec = entry == null ? null : (Codec)entry.getValue();
            }
            finally {
                this.stampedLock.unlockRead(lock);
            }
        }
        extraInfo.ignoreUnusedKey(this.key);
        try {
            if (codec == null) {
                Object defaultCodec = this.getDefaultCodec();
                if (defaultCodec == null) {
                    if (distance == 0) {
                        throw new ACodecMapCodec.UnknownIdException("No codec registered with for '" + this.key + "': null");
                    }
                    reader.skip(distance - reader.getMarkDistance());
                    String id = reader.readString();
                    throw new ACodecMapCodec.UnknownIdException("No codec registered with for '" + this.key + "': " + id);
                }
                reader.reset();
                Object t = defaultCodec.decodeJson(reader, extraInfo);
                return t;
            }
            reader.reset();
            Object t = codec.decodeJson(reader, extraInfo);
            return t;
        }
        finally {
            extraInfo.popIgnoredUnusedKey();
        }
    }
}

