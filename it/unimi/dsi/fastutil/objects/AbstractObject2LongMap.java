/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObject2LongFunction;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;

public abstract class AbstractObject2LongMap<K>
extends AbstractObject2LongFunction<K>
implements Object2LongMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractObject2LongMap() {
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.object2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2LongMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(long v) {
        Iterator i = this.object2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2LongMap.Entry)i.next()).getLongValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public final long mergeLong(K key, long value, it.unimi.dsi.fastutil.longs.LongBinaryOperator remappingFunction) {
        return this.mergeLong(key, value, (LongBinaryOperator)remappingFunction);
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractObject2LongMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractObject2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2LongMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Object2LongMap.Entry<K>> i;
                    {
                        this.i = Object2LongMaps.fastIterator(AbstractObject2LongMap.this);
                    }

                    @Override
                    public K next() {
                        return ((Object2LongMap.Entry)this.i.next()).getKey();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super K> action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept((Object)entry.getKey()));
                    }
                };
            }

            @Override
            public ObjectSpliterator<K> spliterator() {
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2LongMap.this), 65);
            }
        };
    }

    @Override
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long k) {
                return AbstractObject2LongMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractObject2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Object2LongMap.Entry<K>> i;
                    {
                        this.i = Object2LongMaps.fastIterator(AbstractObject2LongMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return ((Object2LongMap.Entry)this.i.next()).getLongValue();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }

                    @Override
                    public void forEachRemaining(LongConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getLongValue()));
                    }
                };
            }

            @Override
            public LongSpliterator spliterator() {
                return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2LongMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> m) {
        if (m instanceof Object2LongMap) {
            ObjectIterator i = Object2LongMaps.fastIterator((Object2LongMap)m);
            while (i.hasNext()) {
                Object2LongMap.Entry e = (Object2LongMap.Entry)i.next();
                this.put(e.getKey(), e.getLongValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Long>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Long> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Object2LongMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Object2LongMap.Entry)i.next()).hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map m = (Map)o;
        if (m.size() != this.size()) {
            return false;
        }
        return this.object2LongEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Object2LongMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object2LongMap.Entry e = (Object2LongMap.Entry)i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<K>
    extends AbstractObjectSet<Object2LongMap.Entry<K>> {
        protected final Object2LongMap<K> map;

        public BasicEntrySet(Object2LongMap<K> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2LongMap.Entry) {
                Object2LongMap.Entry e = (Object2LongMap.Entry)o;
                Object k = e.getKey();
                return this.map.containsKey(k) && this.map.getLong(k) == e.getLongValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Long)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.getLong(k) == ((Long)value).longValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2LongMap.Entry) {
                Object2LongMap.Entry e = (Object2LongMap.Entry)o;
                return this.map.remove(e.getKey(), e.getLongValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Long)) {
                return false;
            }
            long v = (Long)value;
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Object2LongMap.Entry<K>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<K>
    implements Object2LongMap.Entry<K> {
        protected K key;
        protected long value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Long value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(K key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public long getLongValue() {
            return this.value;
        }

        @Override
        public long setValue(long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2LongMap.Entry) {
                Object2LongMap.Entry e = (Object2LongMap.Entry)o;
                return Objects.equals(this.key, e.getKey()) && this.value == e.getLongValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Long)) {
                return false;
            }
            return Objects.equals(this.key, key) && this.value == (Long)value;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.long2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

