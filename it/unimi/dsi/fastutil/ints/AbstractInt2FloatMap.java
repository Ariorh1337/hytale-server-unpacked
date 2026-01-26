/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.ints.AbstractInt2FloatFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.IntConsumer;

public abstract class AbstractInt2FloatMap
extends AbstractInt2FloatFunction
implements Int2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2FloatMap() {
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2FloatMap.Entry)i.next()).getIntKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(float v) {
        Iterator i = this.int2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2FloatMap.Entry)i.next()).getFloatValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public IntSet keySet() {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int k) {
                return AbstractInt2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2FloatMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2FloatMap.Entry> i;
                    {
                        this.i = Int2FloatMaps.fastIterator(AbstractInt2FloatMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return ((Int2FloatMap.Entry)this.i.next()).getIntKey();
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
                    public void forEachRemaining(IntConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getIntKey()));
                    }
                };
            }

            @Override
            public IntSpliterator spliterator() {
                return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2FloatMap.this), 321);
            }
        };
    }

    @Override
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k) {
                return AbstractInt2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Int2FloatMap.Entry> i;
                    {
                        this.i = Int2FloatMaps.fastIterator(AbstractInt2FloatMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Int2FloatMap.Entry)this.i.next()).getFloatValue();
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
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2FloatMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Float> m) {
        if (m instanceof Int2FloatMap) {
            ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator((Int2FloatMap)m);
            while (i.hasNext()) {
                Int2FloatMap.Entry e = (Int2FloatMap.Entry)i.next();
                this.put(e.getIntKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Int2FloatMap.Entry)i.next()).hashCode();
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
        return this.int2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2FloatMap.Entry> i = Int2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2FloatMap.Entry e = (Int2FloatMap.Entry)i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Int2FloatMap.Entry> {
        protected final Int2FloatMap map;

        public BasicEntrySet(Int2FloatMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Int2FloatMap.Entry) {
                Int2FloatMap.Entry e = (Int2FloatMap.Entry)o;
                int k = e.getIntKey();
                return this.map.containsKey(k) && Float.floatToIntBits(this.map.get(k)) == Float.floatToIntBits(e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
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
            if (o instanceof Int2FloatMap.Entry) {
                Int2FloatMap.Entry e = (Int2FloatMap.Entry)o;
                return this.map.remove(e.getIntKey(), e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
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
        public ObjectSpliterator<Int2FloatMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Int2FloatMap.Entry {
        protected int key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Float value) {
            this.key = key;
            this.value = value.floatValue();
        }

        public BasicEntry(int key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int getIntKey() {
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
            if (o instanceof Int2FloatMap.Entry) {
                Int2FloatMap.Entry e = (Int2FloatMap.Entry)o;
                return this.key == e.getIntKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getFloatValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Float)) {
                return false;
            }
            return this.key == (Integer)key && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)value).floatValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

