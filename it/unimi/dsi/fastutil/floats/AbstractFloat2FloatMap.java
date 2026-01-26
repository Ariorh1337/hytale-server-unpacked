/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloat2FloatFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2FloatMap;
import it.unimi.dsi.fastutil.floats.Float2FloatMaps;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFloat2FloatMap
extends AbstractFloat2FloatFunction
implements Float2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2FloatMap() {
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2FloatMap.Entry)i.next()).getFloatKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(float v) {
        Iterator i = this.float2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2FloatMap.Entry)i.next()).getFloatValue() != v) continue;
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
                return AbstractFloat2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2FloatMap.Entry> i;
                    {
                        this.i = Float2FloatMaps.fastIterator(AbstractFloat2FloatMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Float2FloatMap.Entry)this.i.next()).getFloatKey();
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
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2FloatMap.this), 321);
            }
        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k) {
                return AbstractFloat2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2FloatMap.Entry> i;
                    {
                        this.i = Float2FloatMaps.fastIterator(AbstractFloat2FloatMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Float2FloatMap.Entry)this.i.next()).getFloatValue();
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
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getFloatValue()));
                    }
                };
            }

            @Override
            public FloatSpliterator spliterator() {
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2FloatMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Float> m) {
        if (m instanceof Float2FloatMap) {
            ObjectIterator<Float2FloatMap.Entry> i = Float2FloatMaps.fastIterator((Float2FloatMap)m);
            while (i.hasNext()) {
                Float2FloatMap.Entry e = (Float2FloatMap.Entry)i.next();
                this.put(e.getFloatKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2FloatMap.Entry> i = Float2FloatMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Float2FloatMap.Entry)i.next()).hashCode();
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
        return this.float2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2FloatMap.Entry> i = Float2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2FloatMap.Entry e = (Float2FloatMap.Entry)i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Float2FloatMap.Entry> {
        protected final Float2FloatMap map;

        public BasicEntrySet(Float2FloatMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2FloatMap.Entry) {
                Float2FloatMap.Entry e = (Float2FloatMap.Entry)o;
                float k = e.getFloatKey();
                return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(((Float)value).floatValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2FloatMap.Entry) {
                Float2FloatMap.Entry e = (Float2FloatMap.Entry)o;
                return this.map.remove(e.getFloatKey(), e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            float v = ((Float)value).floatValue();
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Float2FloatMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Float2FloatMap.Entry {
        protected float key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Float value) {
            this.key = key.floatValue();
            this.value = value.floatValue();
        }

        public BasicEntry(float key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public float getFloatKey() {
            return this.key;
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }

        @Override
        public float setValue(float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2FloatMap.Entry) {
                Float2FloatMap.Entry e = (Float2FloatMap.Entry)o;
                return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)key).floatValue()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)value).floatValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

