/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.floats.AbstractFloat2BooleanFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2BooleanMap;
import it.unimi.dsi.fastutil.floats.Float2BooleanMaps;
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

public abstract class AbstractFloat2BooleanMap
extends AbstractFloat2BooleanFunction
implements Float2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2BooleanMap() {
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2BooleanMap.Entry)i.next()).getFloatKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(boolean v) {
        Iterator i = this.float2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2BooleanMap.Entry)i.next()).getBooleanValue() != v) continue;
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
                return AbstractFloat2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2BooleanMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2BooleanMap.Entry> i;
                    {
                        this.i = Float2BooleanMaps.fastIterator(AbstractFloat2BooleanMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return ((Float2BooleanMap.Entry)this.i.next()).getFloatKey();
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
                return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2BooleanMap.this), 321);
            }
        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractFloat2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Float2BooleanMap.Entry> i;
                    {
                        this.i = Float2BooleanMaps.fastIterator(AbstractFloat2BooleanMap.this);
                    }

                    @Override
                    public boolean nextBoolean() {
                        return ((Float2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
                    public void forEachRemaining(BooleanConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getBooleanValue()));
                    }
                };
            }

            @Override
            public BooleanSpliterator spliterator() {
                return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractFloat2BooleanMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Boolean> m) {
        if (m instanceof Float2BooleanMap) {
            ObjectIterator<Float2BooleanMap.Entry> i = Float2BooleanMaps.fastIterator((Float2BooleanMap)m);
            while (i.hasNext()) {
                Float2BooleanMap.Entry e = (Float2BooleanMap.Entry)i.next();
                this.put(e.getFloatKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2BooleanMap.Entry> i = Float2BooleanMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Float2BooleanMap.Entry)i.next()).hashCode();
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
        return this.float2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2BooleanMap.Entry> i = Float2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2BooleanMap.Entry e = (Float2BooleanMap.Entry)i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Float2BooleanMap.Entry> {
        protected final Float2BooleanMap map;

        public BasicEntrySet(Float2BooleanMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2BooleanMap.Entry) {
                Float2BooleanMap.Entry e = (Float2BooleanMap.Entry)o;
                float k = e.getFloatKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object value = e.getValue();
            if (value == null || !(value instanceof Boolean)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.get(k) == ((Boolean)value).booleanValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2BooleanMap.Entry) {
                Float2BooleanMap.Entry e = (Float2BooleanMap.Entry)o;
                return this.map.remove(e.getFloatKey(), e.getBooleanValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            float k = ((Float)key).floatValue();
            Object value = e.getValue();
            if (value == null || !(value instanceof Boolean)) {
                return false;
            }
            boolean v = (Boolean)value;
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Float2BooleanMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Float2BooleanMap.Entry {
        protected float key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Boolean value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, boolean value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public float getFloatKey() {
            return this.key;
        }

        @Override
        public boolean getBooleanValue() {
            return this.value;
        }

        @Override
        public boolean setValue(boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Float2BooleanMap.Entry) {
                Float2BooleanMap.Entry e = (Float2BooleanMap.Entry)o;
                return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getFloatKey()) && this.value == e.getBooleanValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Float)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Boolean)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)key).floatValue()) && this.value == (Boolean)value;
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

