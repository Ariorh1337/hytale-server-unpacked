/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.ints.AbstractInt2CharFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2CharMap;
import it.unimi.dsi.fastutil.ints.Int2CharMaps;
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

public abstract class AbstractInt2CharMap
extends AbstractInt2CharFunction
implements Int2CharMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2CharMap() {
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2CharMap.Entry)i.next()).getIntKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(char v) {
        Iterator i = this.int2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2CharMap.Entry)i.next()).getCharValue() != v) continue;
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
                return AbstractInt2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2CharMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2CharMap.Entry> i;
                    {
                        this.i = Int2CharMaps.fastIterator(AbstractInt2CharMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return ((Int2CharMap.Entry)this.i.next()).getIntKey();
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
                return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2CharMap.this), 321);
            }
        };
    }

    @Override
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractInt2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Int2CharMap.Entry> i;
                    {
                        this.i = Int2CharMaps.fastIterator(AbstractInt2CharMap.this);
                    }

                    @Override
                    public char nextChar() {
                        return ((Int2CharMap.Entry)this.i.next()).getCharValue();
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
                    public void forEachRemaining(CharConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getCharValue()));
                    }
                };
            }

            @Override
            public CharSpliterator spliterator() {
                return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractInt2CharMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Character> m) {
        if (m instanceof Int2CharMap) {
            ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator((Int2CharMap)m);
            while (i.hasNext()) {
                Int2CharMap.Entry e = (Int2CharMap.Entry)i.next();
                this.put(e.getIntKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Int2CharMap.Entry)i.next()).hashCode();
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
        return this.int2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2CharMap.Entry> i = Int2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2CharMap.Entry e = (Int2CharMap.Entry)i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Int2CharMap.Entry> {
        protected final Int2CharMap map;

        public BasicEntrySet(Int2CharMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Int2CharMap.Entry) {
                Int2CharMap.Entry e = (Int2CharMap.Entry)o;
                int k = e.getIntKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
            Object value = e.getValue();
            if (value == null || !(value instanceof Character)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.get(k) == ((Character)value).charValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Int2CharMap.Entry) {
                Int2CharMap.Entry e = (Int2CharMap.Entry)o;
                return this.map.remove(e.getIntKey(), e.getCharValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            int k = (Integer)key;
            Object value = e.getValue();
            if (value == null || !(value instanceof Character)) {
                return false;
            }
            char v = ((Character)value).charValue();
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Int2CharMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Int2CharMap.Entry {
        protected int key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(int key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int getIntKey() {
            return this.key;
        }

        @Override
        public char getCharValue() {
            return this.value;
        }

        @Override
        public char setValue(char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Int2CharMap.Entry) {
                Int2CharMap.Entry e = (Int2CharMap.Entry)o;
                return this.key == e.getIntKey() && this.value == e.getCharValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Integer)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Character)) {
                return false;
            }
            return this.key == (Integer)key && this.value == ((Character)value).charValue();
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

