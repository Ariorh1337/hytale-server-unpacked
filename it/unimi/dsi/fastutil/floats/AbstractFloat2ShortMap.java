/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ShortFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ShortMap;
import it.unimi.dsi.fastutil.floats.Float2ShortMaps;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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

public abstract class AbstractFloat2ShortMap
extends AbstractFloat2ShortFunction
implements Float2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2ShortMap() {
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ShortMap.Entry)i.next()).getFloatKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(short v) {
        Iterator i = this.float2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ShortMap.Entry)i.next()).getShortValue() != v) continue;
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
                return AbstractFloat2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ShortMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2ShortMap.Entry> i;
                    {
                        this.i = Float2ShortMaps.fastIterator(AbstractFloat2ShortMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Float2ShortMap.Entry)this.i.next()).getFloatKey();
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
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2ShortMap.this), 321);
            }
        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractFloat2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Float2ShortMap.Entry> i;
                    {
                        this.i = Float2ShortMaps.fastIterator(AbstractFloat2ShortMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return ((Float2ShortMap.Entry)this.i.next()).getShortValue();
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
                return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2ShortMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Short> m) {
        if (m instanceof Float2ShortMap) {
            ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator((Float2ShortMap)m);
            while (i.hasNext()) {
                Float2ShortMap.Entry e = (Float2ShortMap.Entry)i.next();
                this.put(e.getFloatKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Float2ShortMap.Entry)i.next()).hashCode();
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
        return this.float2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2ShortMap.Entry e = (Float2ShortMap.Entry)i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Float2ShortMap.Entry> {
        protected final Float2ShortMap map;

        public BasicEntrySet(Float2ShortMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2ShortMap.Entry) {
                Float2ShortMap.Entry e = (Float2ShortMap.Entry)o;
                float k = e.getFloatKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getShortValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object value = e.getValue();
            if (value == null || !(value instanceof Short)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.get(k) == ((Short)value).shortValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2ShortMap.Entry) {
                Float2ShortMap.Entry e = (Float2ShortMap.Entry)o;
                return this.map.remove(e.getFloatKey(), e.getShortValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
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
        public ObjectSpliterator<Float2ShortMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Float2ShortMap.Entry {
        protected float key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Short value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public float getFloatKey() {
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
            if (o instanceof Float2ShortMap.Entry) {
                Float2ShortMap.Entry e = (Float2ShortMap.Entry)o;
                return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && this.value == e.getShortValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Short)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)key).floatValue()) && this.value == (Short)value;
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

