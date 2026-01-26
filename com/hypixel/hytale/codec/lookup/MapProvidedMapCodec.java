/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.lookup;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.lookup.AMapProvidedMapCodec;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class MapProvidedMapCodec<V, P>
extends AMapProvidedMapCodec<String, V, P, Map<String, V>> {
    private final Supplier<Map<String, V>> supplier;

    public MapProvidedMapCodec(Map<String, P> codecProvider, Function<P, Codec<V>> mapper, Supplier<Map<String, V>> supplier) {
        this(codecProvider, mapper, supplier, true);
    }

    public MapProvidedMapCodec(Map<String, P> codecProvider, Function<P, Codec<V>> mapper, Supplier<Map<String, V>> supplier, boolean unmodifiable) {
        super(codecProvider, mapper, unmodifiable);
        this.supplier = supplier;
    }

    @Override
    public Map<String, V> createMap() {
        return this.supplier.get();
    }

    @Override
    protected String getIdForKey(String key) {
        return key;
    }

    @Override
    protected String getKeyForId(String id) {
        return id;
    }

    @Override
    @Nonnull
    protected Map<String, V> emptyMap() {
        return Collections.emptyMap();
    }

    @Override
    @Nonnull
    protected Map<String, V> unmodifiableMap(@Nonnull Map<String, V> m) {
        return Collections.unmodifiableMap(m);
    }
}

