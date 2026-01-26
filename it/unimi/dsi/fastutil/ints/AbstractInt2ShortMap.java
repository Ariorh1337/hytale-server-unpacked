/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.AbstractInt2ShortFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
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
import java.util.function.IntConsumer;

public abstract class AbstractInt2ShortMap
extends AbstractInt2ShortFunction
implements Int2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2ShortMap() {
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2ShortMap.Entry)i.next()).getIntKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(short v) {
        Iterator i = this.int2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2ShortMap.Entry)i.next()).getShortValue() != v) continue;
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
                return AbstractInt2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ShortMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2ShortMap.Entry> i;
                    {
                        this.i = Int2ShortMaps.fastIterator(AbstractInt2ShortMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return ((Int2ShortMap.Entry)this.i.next()).getIntKey();
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
                return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2ShortMap.this), 321);
            }
        };
    }

    @Override
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractInt2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Int2ShortMap.Entry> i;
                    {
                        this.i = Int2ShortMaps.fastIterator(AbstractInt2ShortMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return ((Int2ShortMap.Entry)this.i.next()).getShortValue();
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
                return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2ShortMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Short> m) {
        if (m instanceof Int2ShortMap) {
            ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator((Int2ShortMap)m);
            while (i.hasNext()) {
                Int2ShortMap.Entry e = (Int2ShortMap.Entry)i.next();
                this.put(e.getIntKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Int2ShortMap.Entry)i.next()).hashCode();
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
        return this.int2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2ShortMap.Entry e = (Int2ShortMap.Entry)i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Int2ShortMap.Entry> {
        protected final Int2ShortMap map;

        public BasicEntrySet(Int2ShortMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Int2ShortMap.Entry) {
                Int2ShortMap.Entry e = (Int2ShortMap.Entry)o;
                int k = e.getIntKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getShortValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
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
            if (o instanceof Int2ShortMap.Entry) {
                Int2ShortMap.Entry e = (Int2ShortMap.Entry)o;
                return this.map.remove(e.getIntKey(), e.getShortValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
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
        public ObjectSpliterator<Int2ShortMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Int2ShortMap.Entry {
        protected int key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Short value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int getIntKey() {
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
            if (o instanceof Int2ShortMap.Entry) {
                Int2ShortMap.Entry e = (Int2ShortMap.Entry)o;
                return this.key == e.getIntKey() && this.value == e.getShortValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Short)) {
                return false;
            }
            return this.key == (Integer)key && this.value == (Short)value;
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

