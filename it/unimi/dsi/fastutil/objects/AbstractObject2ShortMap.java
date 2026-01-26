/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObject2ShortFunction;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractObject2ShortMap<K>
extends AbstractObject2ShortFunction<K>
implements Object2ShortMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractObject2ShortMap() {
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.object2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2ShortMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(short v) {
        Iterator i = this.object2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2ShortMap.Entry)i.next()).getShortValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractObject2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractObject2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2ShortMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Object2ShortMap.Entry<K>> i;
                    {
                        this.i = Object2ShortMaps.fastIterator(AbstractObject2ShortMap.this);
                    }

                    @Override
                    public K next() {
                        return ((Object2ShortMap.Entry)this.i.next()).getKey();
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
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2ShortMap.this), 65);
            }
        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractObject2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractObject2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Object2ShortMap.Entry<K>> i;
                    {
                        this.i = Object2ShortMaps.fastIterator(AbstractObject2ShortMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return ((Object2ShortMap.Entry)this.i.next()).getShortValue();
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
                    public void forEachRemaining(ShortConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getShortValue()));
                    }
                };
            }

            @Override
            public ShortSpliterator spliterator() {
                return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2ShortMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends K, ? extends Short> m) {
        if (m instanceof Object2ShortMap) {
            ObjectIterator i = Object2ShortMaps.fastIterator((Object2ShortMap)m);
            while (i.hasNext()) {
                Object2ShortMap.Entry e = (Object2ShortMap.Entry)i.next();
                this.put(e.getKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Object2ShortMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Object2ShortMap.Entry)i.next()).hashCode();
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
        return this.object2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Object2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object2ShortMap.Entry e = (Object2ShortMap.Entry)i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<K>
    extends AbstractObjectSet<Object2ShortMap.Entry<K>> {
        protected final Object2ShortMap<K> map;

        public BasicEntrySet(Object2ShortMap<K> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2ShortMap.Entry) {
                Object2ShortMap.Entry e = (Object2ShortMap.Entry)o;
                Object k = e.getKey();
                return this.map.containsKey(k) && this.map.getShort(k) == e.getShortValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Short)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.getShort(k) == ((Short)value).shortValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2ShortMap.Entry) {
                Object2ShortMap.Entry e = (Object2ShortMap.Entry)o;
                return this.map.remove(e.getKey(), e.getShortValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Short)) {
                return false;
            }
            short v = (Short)value;
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Object2ShortMap.Entry<K>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<K>
    implements Object2ShortMap.Entry<K> {
        protected K key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Short value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(K key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public short getShortValue() {
            return this.value;
        }

        @Override
        public short setValue(short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2ShortMap.Entry) {
                Object2ShortMap.Entry e = (Object2ShortMap.Entry)o;
                return Objects.equals(this.key, e.getKey()) && this.value == e.getShortValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Short)) {
                return false;
            }
            return Objects.equals(this.key, key) && this.value == (Short)value;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

