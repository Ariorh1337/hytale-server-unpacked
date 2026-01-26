/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.longs.AbstractLong2CharFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2CharMap;
import it.unimi.dsi.fastutil.longs.Long2CharMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.LongConsumer;

public abstract class AbstractLong2CharMap
extends AbstractLong2CharFunction
implements Long2CharMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2CharMap() {
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2CharMap.Entry)i.next()).getLongKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(char v) {
        Iterator i = this.long2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2CharMap.Entry)i.next()).getCharValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public LongSet keySet() {
        return new AbstractLongSet(){

            @Override
            public boolean contains(long k) {
                return AbstractLong2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2CharMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2CharMap.Entry> i;
                    {
                        this.i = Long2CharMaps.fastIterator(AbstractLong2CharMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return ((Long2CharMap.Entry)this.i.next()).getLongKey();
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
                    public void forEachRemaining(LongConsumer action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getLongKey()));
                    }
                };
            }

            @Override
            public LongSpliterator spliterator() {
                return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2CharMap.this), 321);
            }
        };
    }

    @Override
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractLong2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Long2CharMap.Entry> i;
                    {
                        this.i = Long2CharMaps.fastIterator(AbstractLong2CharMap.this);
                    }

                    @Override
                    public char nextChar() {
                        return ((Long2CharMap.Entry)this.i.next()).getCharValue();
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
                return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractLong2CharMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Character> m) {
        if (m instanceof Long2CharMap) {
            ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator((Long2CharMap)m);
            while (i.hasNext()) {
                Long2CharMap.Entry e = (Long2CharMap.Entry)i.next();
                this.put(e.getLongKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Long2CharMap.Entry)i.next()).hashCode();
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
        return this.long2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2CharMap.Entry e = (Long2CharMap.Entry)i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet
    extends AbstractObjectSet<Long2CharMap.Entry> {
        protected final Long2CharMap map;

        public BasicEntrySet(Long2CharMap map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Long2CharMap.Entry) {
                Long2CharMap.Entry e = (Long2CharMap.Entry)o;
                long k = e.getLongKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getCharValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Long)) {
                return false;
            }
            long k = (Long)key;
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
            if (o instanceof Long2CharMap.Entry) {
                Long2CharMap.Entry e = (Long2CharMap.Entry)o;
                return this.map.remove(e.getLongKey(), e.getCharValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Long)) {
                return false;
            }
            long k = (Long)key;
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
        public ObjectSpliterator<Long2CharMap.Entry> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry
    implements Long2CharMap.Entry {
        protected long key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(long key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public long getLongKey() {
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
            if (o instanceof Long2CharMap.Entry) {
                Long2CharMap.Entry e = (Long2CharMap.Entry)o;
                return this.key == e.getLongKey() && this.value == e.getCharValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Long)) {
                return false;
            }
            Object value = e.getValue();
            if (value == null || !(value instanceof Character)) {
                return false;
            }
            return this.key == (Long)key && this.value == ((Character)value).charValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

