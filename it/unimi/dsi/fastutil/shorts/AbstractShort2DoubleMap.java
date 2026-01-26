/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.shorts.AbstractShort2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2DoubleMap;
import it.unimi.dsi.fastutil.shorts.Short2DoubleMaps;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;

public abstract class AbstractShort2DoubleMap
extends AbstractShort2DoubleFunction
implements Short2DoubleMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2DoubleMap() {
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2DoubleMap.Entry)i.next()).getShortKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(double v) {
        Iterator i = this.short2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2DoubleMap.Entry)i.next()).getDoubleValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public final double mergeDouble(short key, double value, it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator remappingFunction) {
        return this.mergeDouble(key, value, (DoubleBinaryOperator)remappingFunction);
    }

    @Override
    public ShortSet keySet() {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short k) {
                return AbstractShort2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2DoubleMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2DoubleMap.Entry> i;
                    {
                        this.i = Short2DoubleMaps.fastIterator(AbstractShort2DoubleMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return ((Short2DoubleMap.Entry)this.i.next()).getShortKey();
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
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getShortKey()));
                    }
                };
            }

            @Override
            public ShortSpliterator spliterator() {
                return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2DoubleMap.this), 321);
            }
        };
    }

    @Override
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractShort2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Short2DoubleMap.Entry> i;
                    {
                        this.i = Short2DoubleMaps.fastIterator(AbstractShort2DoubleMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return ((Short2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getDoubleValue()));
                    }
                };
            }

            @Override
            public DoubleSpliterator spliterator() {
                return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2DoubleMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Double> m) {
        if (m instanceof Short2DoubleMap) {
            ObjectIterator<Short2DoubleMap.Entry> i = Short2DoubleMaps.fastIterator((Short2DoubleMap)m);
            while (i.hasNext()) {
                Short2DoubleMap.Entry e = (Short2DoubleMap.Entry)i.next();
                this.put(e.getShortKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Short, ? extends Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Short, ? extends Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Short2DoubleMap.Entry> i = Short2DoubleMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Short2DoubleMap.Entry)i.next()).hashCode();
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
        return this.short2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Short2DoubleMap.Entry> i = Short2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2DoubleMap.Entry e = (Short2DoubleMap.Entry)i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Short2DoubleMap.Entry> {
        protected final Short2DoubleMap map;

        public BasicEntrySet(Short2DoubleMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Short2DoubleMap.Entry) {
                Short2DoubleMap.Entry e = (Short2DoubleMap.Entry)o;
                short k = e.getShortKey();
                return this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits(e.getDoubleValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Short)) {
                return false;
            }
            short k = (Short)key;
            Object value = e.getValue();
            if (value == null || !(value instanceof Double)) {
                return false;
            }
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.get(k)) == Double.doubleToLongBits((Double)value);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Short2DoubleMap.Entry) {
                Short2DoubleMap.Entry e = (Short2DoubleMap.Entry)o;
                return this.map.remove(e.getShortKey(), e.getDoubleValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Short)) {
                return false;
            }
            short k = (Short)key;
            Object value = e.getValue();
            if (value == null || !(value instanceof Double)) {
                return false;
            }
            double v = (Double)value;
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Short2DoubleMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Short2DoubleMap.Entry {
        protected short key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(short key, double value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public short getShortKey() {
            return this.key;
        }

        @Override
        public double getDoubleValue() {
            return this.value;
        }

        @Override
        public double setValue(double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Short2DoubleMap.Entry) {
                Short2DoubleMap.Entry e = (Short2DoubleMap.Entry)o;
                return this.key == e.getShortKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Short)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Double)) {
                return false;
            }
            return this.key == (Short)key && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value);
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

