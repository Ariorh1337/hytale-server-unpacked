/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObject2DoubleFunction;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;

public abstract class AbstractObject2DoubleMap<K>
extends AbstractObject2DoubleFunction<K>
implements Object2DoubleMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractObject2DoubleMap() {
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.object2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2DoubleMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(double v) {
        Iterator i = this.object2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2DoubleMap.Entry)i.next()).getDoubleValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public final double mergeDouble(K key, double value, it.unimi.dsi.fastutil.doubles.DoubleBinaryOperator remappingFunction) {
        return this.mergeDouble(key, value, (DoubleBinaryOperator)remappingFunction);
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractObject2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractObject2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2DoubleMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Object2DoubleMap.Entry<K>> i;
                    {
                        this.i = Object2DoubleMaps.fastIterator(AbstractObject2DoubleMap.this);
                    }

                    @Override
                    public K next() {
                        return ((Object2DoubleMap.Entry)this.i.next()).getKey();
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
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2DoubleMap.this), 65);
            }
        };
    }

    @Override
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractObject2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractObject2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Object2DoubleMap.Entry<K>> i;
                    {
                        this.i = Object2DoubleMaps.fastIterator(AbstractObject2DoubleMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return ((Object2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
                return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractObject2DoubleMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends K, ? extends Double> m) {
        if (m instanceof Object2DoubleMap) {
            ObjectIterator i = Object2DoubleMaps.fastIterator((Object2DoubleMap)m);
            while (i.hasNext()) {
                Object2DoubleMap.Entry e = (Object2DoubleMap.Entry)i.next();
                this.put(e.getKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Object2DoubleMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Object2DoubleMap.Entry)i.next()).hashCode();
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
        return this.object2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Object2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object2DoubleMap.Entry e = (Object2DoubleMap.Entry)i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<K>
    extends AbstractObjectSet<Object2DoubleMap.Entry<K>> {
        protected final Object2DoubleMap<K> map;

        public BasicEntrySet(Object2DoubleMap<K> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2DoubleMap.Entry) {
                Object2DoubleMap.Entry e = (Object2DoubleMap.Entry)o;
                Object k = e.getKey();
                return this.map.containsKey(k) && Double.doubleToLongBits(this.map.getDouble(k)) == Double.doubleToLongBits(e.getDoubleValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Double)) {
                return false;
            }
            return this.map.containsKey(k) && Double.doubleToLongBits(this.map.getDouble(k)) == Double.doubleToLongBits((Double)value);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Object2DoubleMap.Entry) {
                Object2DoubleMap.Entry e = (Object2DoubleMap.Entry)o;
                return this.map.remove(e.getKey(), e.getDoubleValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
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
        public ObjectSpliterator<Object2DoubleMap.Entry<K>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<K>
    implements Object2DoubleMap.Entry<K> {
        protected K key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(K key, double value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
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
            if (o instanceof Object2DoubleMap.Entry) {
                Object2DoubleMap.Entry e = (Object2DoubleMap.Entry)o;
                return Objects.equals(this.key, e.getKey()) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getDoubleValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Double)) {
                return false;
            }
            return Objects.equals(this.key, key) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)value);
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

