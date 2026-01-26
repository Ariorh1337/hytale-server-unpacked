/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2BooleanMap;
import it.unimi.dsi.fastutil.doubles.Double2BooleanMaps;
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

public abstract class AbstractDouble2BooleanMap
extends AbstractDouble2BooleanFunction
implements Double2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2BooleanMap() {
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2BooleanMap.Entry)i.next()).getDoubleKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(boolean v) {
        Iterator i = this.double2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2BooleanMap.Entry)i.next()).getBooleanValue() != v) continue;
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
                return AbstractDouble2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2BooleanMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2BooleanMap.Entry> i;
                    {
                        this.i = Double2BooleanMaps.fastIterator(AbstractDouble2BooleanMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return ((Double2BooleanMap.Entry)this.i.next()).getDoubleKey();
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
                return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2BooleanMap.this), 321);
            }
        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractDouble2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Double2BooleanMap.Entry> i;
                    {
                        this.i = Double2BooleanMaps.fastIterator(AbstractDouble2BooleanMap.this);
                    }

                    @Override
                    public boolean nextBoolean() {
                        return ((Double2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
                return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractDouble2BooleanMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Boolean> m) {
        if (m instanceof Double2BooleanMap) {
            ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator((Double2BooleanMap)m);
            while (i.hasNext()) {
                Double2BooleanMap.Entry e = (Double2BooleanMap.Entry)i.next();
                this.put(e.getDoubleKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Double2BooleanMap.Entry)i.next()).hashCode();
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
        return this.double2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2BooleanMap.Entry e = (Double2BooleanMap.Entry)i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Double2BooleanMap.Entry> {
        protected final Double2BooleanMap map;

        public BasicEntrySet(Double2BooleanMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Double2BooleanMap.Entry) {
                Double2BooleanMap.Entry e = (Double2BooleanMap.Entry)o;
                double k = e.getDoubleKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Double)) {
                return false;
            }
            double k = (Double)key;
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
            if (o instanceof Double2BooleanMap.Entry) {
                Double2BooleanMap.Entry e = (Double2BooleanMap.Entry)o;
                return this.map.remove(e.getDoubleKey(), e.getBooleanValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Double)) {
                return false;
            }
            double k = (Double)key;
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
        public ObjectSpliterator<Double2BooleanMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Double2BooleanMap.Entry {
        protected double key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Boolean value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, boolean value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public double getDoubleKey() {
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
            if (o instanceof Double2BooleanMap.Entry) {
                Double2BooleanMap.Entry e = (Double2BooleanMap.Entry)o;
                return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getDoubleKey()) && this.value == e.getBooleanValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Double)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Boolean)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)key) && this.value == (Boolean)value;
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

