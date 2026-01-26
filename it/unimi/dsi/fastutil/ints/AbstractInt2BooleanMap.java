/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.ints.AbstractInt2BooleanFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMaps;
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

public abstract class AbstractInt2BooleanMap
extends AbstractInt2BooleanFunction
implements Int2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2BooleanMap() {
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2BooleanMap.Entry)i.next()).getIntKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(boolean v) {
        Iterator i = this.int2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2BooleanMap.Entry)i.next()).getBooleanValue() != v) continue;
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
                return AbstractInt2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2BooleanMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2BooleanMap.Entry> i;
                    {
                        this.i = Int2BooleanMaps.fastIterator(AbstractInt2BooleanMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return ((Int2BooleanMap.Entry)this.i.next()).getIntKey();
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
                return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2BooleanMap.this), 321);
            }
        };
    }

    @Override
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractInt2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Int2BooleanMap.Entry> i;
                    {
                        this.i = Int2BooleanMaps.fastIterator(AbstractInt2BooleanMap.this);
                    }

                    @Override
                    public boolean nextBoolean() {
                        return ((Int2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
                return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2BooleanMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Boolean> m) {
        if (m instanceof Int2BooleanMap) {
            ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator((Int2BooleanMap)m);
            while (i.hasNext()) {
                Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)i.next();
                this.put(e.getIntKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Int2BooleanMap.Entry)i.next()).hashCode();
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
        return this.int2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2BooleanMap.Entry> i = Int2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Int2BooleanMap.Entry> {
        protected final Int2BooleanMap map;

        public BasicEntrySet(Int2BooleanMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Int2BooleanMap.Entry) {
                Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)o;
                int k = e.getIntKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getBooleanValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
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
            if (o instanceof Int2BooleanMap.Entry) {
                Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)o;
                return this.map.remove(e.getIntKey(), e.getBooleanValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
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
        public ObjectSpliterator<Int2BooleanMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Int2BooleanMap.Entry {
        protected int key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Boolean value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, boolean value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int getIntKey() {
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
            if (o instanceof Int2BooleanMap.Entry) {
                Int2BooleanMap.Entry e = (Int2BooleanMap.Entry)o;
                return this.key == e.getIntKey() && this.value == e.getBooleanValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Boolean)) {
                return false;
            }
            return this.key == (Integer)key && this.value == (Boolean)value;
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

