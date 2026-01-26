/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ByteFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2ByteMap;
import it.unimi.dsi.fastutil.doubles.Double2ByteMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.DoubleConsumer;

public abstract class AbstractDouble2ByteMap
extends AbstractDouble2ByteFunction
implements Double2ByteMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2ByteMap() {
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2ByteMap.Entry)i.next()).getDoubleKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(byte v) {
        Iterator i = this.double2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2ByteMap.Entry)i.next()).getByteValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public DoubleSet keySet() {
        return new AbstractDoubleSet(){

            @Override
            public boolean contains(double k) {
                return AbstractDouble2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ByteMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2ByteMap.Entry> i;
                    {
                        this.i = Double2ByteMaps.fastIterator(AbstractDouble2ByteMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return ((Double2ByteMap.Entry)this.i.next()).getDoubleKey();
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
                    public void forEachRemaining(DoubleConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getDoubleKey()));
                    }
                };
            }

            @Override
            public DoubleSpliterator spliterator() {
                return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ByteMap.this), 321);
            }
        };
    }

    @Override
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte k) {
                return AbstractDouble2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Double2ByteMap.Entry> i;
                    {
                        this.i = Double2ByteMaps.fastIterator(AbstractDouble2ByteMap.this);
                    }

                    @Override
                    public byte nextByte() {
                        return ((Double2ByteMap.Entry)this.i.next()).getByteValue();
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
                    public void forEachRemaining(ByteConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getByteValue()));
                    }
                };
            }

            @Override
            public ByteSpliterator spliterator() {
                return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2ByteMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Byte> m) {
        if (m instanceof Double2ByteMap) {
            ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator((Double2ByteMap)m);
            while (i.hasNext()) {
                Double2ByteMap.Entry e = (Double2ByteMap.Entry)i.next();
                this.put(e.getDoubleKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Double2ByteMap.Entry)i.next()).hashCode();
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
        return this.double2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2ByteMap.Entry> i = Double2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2ByteMap.Entry e = (Double2ByteMap.Entry)i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Double2ByteMap.Entry> {
        protected final Double2ByteMap map;

        public BasicEntrySet(Double2ByteMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Double2ByteMap.Entry) {
                Double2ByteMap.Entry e = (Double2ByteMap.Entry)o;
                double k = e.getDoubleKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getByteValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Double)) {
                return false;
            }
            double k = (Double)key;
            Object value = e.getValue();
            if (value == null || !(value instanceof Byte)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.get(k) == ((Byte)value).byteValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Double2ByteMap.Entry) {
                Double2ByteMap.Entry e = (Double2ByteMap.Entry)o;
                return this.map.remove(e.getDoubleKey(), e.getByteValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Double)) {
                return false;
            }
            double k = (Double)key;
            Object value = e.getValue();
            if (value == null || !(value instanceof Byte)) {
                return false;
            }
            byte v = (Byte)value;
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Double2ByteMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Double2ByteMap.Entry {
        protected double key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Byte value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public double getDoubleKey() {
            return this.key;
        }

        @Override
        public byte getByteValue() {
            return this.value;
        }

        @Override
        public byte setValue(byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Double2ByteMap.Entry) {
                Double2ByteMap.Entry e = (Double2ByteMap.Entry)o;
                return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getByteValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Double)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Byte)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == (Byte)value;
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

