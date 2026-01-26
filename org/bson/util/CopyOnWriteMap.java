/*
 * Decompiled with CFR 0.152.
 */
package org.bson.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bson.util.AbstractCopyOnWriteMap;

abstract class CopyOnWriteMap<K, V>
extends AbstractCopyOnWriteMap<K, V, Map<K, V>> {
    private static final long serialVersionUID = 7935514534647505917L;

    public static <K, V> Builder<K, V> builder() {
        return new Builder();
    }

    public static <K, V> CopyOnWriteMap<K, V> newHashMap() {
        Builder<K, V> builder = CopyOnWriteMap.builder();
        return builder.newHashMap();
    }

    public static <K, V> CopyOnWriteMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        Builder<? extends K, ? extends V> builder = CopyOnWriteMap.builder();
        return builder.addAll(map).newHashMap();
    }

    public static <K, V> CopyOnWriteMap<K, V> newLinkedMap() {
        Builder<K, V> builder = CopyOnWriteMap.builder();
        return builder.newLinkedMap();
    }

    public static <K, V> CopyOnWriteMap<K, V> newLinkedMap(Map<? extends K, ? extends V> map) {
        Builder<? extends K, ? extends V> builder = CopyOnWriteMap.builder();
        return builder.addAll(map).newLinkedMap();
    }

    protected CopyOnWriteMap(Map<? extends K, ? extends V> map) {
        this(map, AbstractCopyOnWriteMap.View.Type.LIVE);
    }

    protected CopyOnWriteMap() {
        this(Collections.emptyMap(), AbstractCopyOnWriteMap.View.Type.LIVE);
    }

    protected CopyOnWriteMap(Map<? extends K, ? extends V> map, AbstractCopyOnWriteMap.View.Type viewType) {
        super(map, viewType);
    }

    protected CopyOnWriteMap(AbstractCopyOnWriteMap.View.Type viewType) {
        super(Collections.emptyMap(), viewType);
    }

    @Override
    protected abstract <N extends Map<? extends K, ? extends V>> Map<K, V> copy(N var1);

    static class Linked<K, V>
    extends CopyOnWriteMap<K, V> {
        private static final long serialVersionUID = -8659999465009072124L;

        Linked(Map<? extends K, ? extends V> map, AbstractCopyOnWriteMap.View.Type viewType) {
            super(map, viewType);
        }

        @Override
        public <N extends Map<? extends K, ? extends V>> Map<K, V> copy(N map) {
            return new LinkedHashMap(map);
        }
    }

    static class Hash<K, V>
    extends CopyOnWriteMap<K, V> {
        private static final long serialVersionUID = 5221824943734164497L;

        Hash(Map<? extends K, ? extends V> map, AbstractCopyOnWriteMap.View.Type viewType) {
            super(map, viewType);
        }

        @Override
        public <N extends Map<? extends K, ? extends V>> Map<K, V> copy(N map) {
            return new HashMap(map);
        }
    }

    public static class Builder<K, V> {
        private AbstractCopyOnWriteMap.View.Type viewType = AbstractCopyOnWriteMap.View.Type.STABLE;
        private final Map<K, V> initialValues = new HashMap();

        Builder() {
        }

        public Builder<K, V> stableViews() {
            this.viewType = AbstractCopyOnWriteMap.View.Type.STABLE;
            return this;
        }

        public Builder<K, V> addAll(Map<? extends K, ? extends V> values) {
            this.initialValues.putAll(values);
            return this;
        }

        public Builder<K, V> liveViews() {
            this.viewType = AbstractCopyOnWriteMap.View.Type.LIVE;
            return this;
        }

        public CopyOnWriteMap<K, V> newHashMap() {
            return new Hash<K, V>(this.initialValues, this.viewType);
        }

        public CopyOnWriteMap<K, V> newLinkedMap() {
            return new Linked<K, V>(this.initialValues, this.viewType);
        }
    }
}

