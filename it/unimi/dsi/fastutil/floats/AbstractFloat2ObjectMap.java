/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ObjectFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMaps;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractFloat2ObjectMap<V>
extends AbstractFloat2ObjectFunction<V>
implements Float2ObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2ObjectMap() {
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ObjectMap.Entry)i.next()).getFloatKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object v) {
        Iterator i = this.float2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ObjectMap.Entry)i.next()).getValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public FloatSet keySet() {
        return new AbstractFloatSet(){

            @Override
            public boolean contains(float k) {
                return AbstractFloat2ObjectMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ObjectMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2ObjectMap.Entry<V>> i;
                    {
                        this.i = Float2ObjectMaps.fastIterator(AbstractFloat2ObjectMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Float2ObjectMap.Entry)this.i.next()).getFloatKey();
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
                    public void forEachRemaining(FloatConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getFloatKey()));
                    }
                };
            }

            @Override
            public FloatSpliterator spliterator() {
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2ObjectMap.this), 321);
            }
        };
    }

    @Override
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractFloat2ObjectMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Float2ObjectMap.Entry<V>> i;
                    {
                        this.i = Float2ObjectMaps.fastIterator(AbstractFloat2ObjectMap.this);
                    }

                    @Override
                    public V next() {
                        return ((Float2ObjectMap.Entry)this.i.next()).getValue();
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
                    public void forEachRemaining(Consumer<? super V> action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept((Object)entry.getValue()));
                    }
                };
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2ObjectMap.this), 64);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Float, ? extends V> m) {
        if (m instanceof Float2ObjectMap) {
            ObjectIterator i = Float2ObjectMaps.fastIterator((Float2ObjectMap)m);
            while (i.hasNext()) {
                Float2ObjectMap.Entry e = (Float2ObjectMap.Entry)i.next();
                this.put(e.getFloatKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Float, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Float, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Float2ObjectMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Float2ObjectMap.Entry)i.next()).hashCode();
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
        return this.float2ObjectEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Float2ObjectMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2ObjectMap.Entry e = (Float2ObjectMap.Entry)i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            if (this == e.getValue()) {
                s.append("(this map)");
                continue;
            }
            s.append(String.valueOf(e.getValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<V>
    extends AbstractObjectSet<Float2ObjectMap.Entry<V>> {
        protected final Float2ObjectMap<V> map;

        public BasicEntrySet(Float2ObjectMap<V> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2ObjectMap.Entry) {
                Float2ObjectMap.Entry e = (Float2ObjectMap.Entry)o;
                float k = e.getFloatKey();
                return this.map.containsKey(k) && Objects.equals(this.map.get(k), e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object value = e.getValue();
            return this.map.containsKey(k) && Objects.equals(this.map.get(k), value);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2ObjectMap.Entry) {
                Float2ObjectMap.Entry e = (Float2ObjectMap.Entry)o;
                return this.map.remove(e.getFloatKey(), e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object v = e.getValue();
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Float2ObjectMap.Entry<V>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<V>
    implements Float2ObjectMap.Entry<V> {
        protected float key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, V value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public float getFloatKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2ObjectMap.Entry) {
                Float2ObjectMap.Entry e = (Float2ObjectMap.Entry)o;
                return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && Objects.equals(this.value, e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            Object value = e.getValue();
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)key).floatValue()) && Objects.equals(this.value, value);
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

