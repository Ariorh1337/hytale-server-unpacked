/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2CharFunction;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.Reference2CharMap;
import it.unimi.dsi.fastutil.objects.Reference2CharMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractReference2CharMap<K>
extends AbstractReference2CharFunction<K>
implements Reference2CharMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractReference2CharMap() {
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.reference2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Reference2CharMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(char v) {
        Iterator i = this.reference2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Reference2CharMap.Entry)i.next()).getCharValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ReferenceSet<K> keySet() {
        return new AbstractReferenceSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractReference2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractReference2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2CharMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Reference2CharMap.Entry<K>> i;
                    {
                        this.i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);
                    }

                    @Override
                    public K next() {
                        return ((Reference2CharMap.Entry)this.i.next()).getKey();
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
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2CharMap.this), 65);
            }
        };
    }

    @Override
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractReference2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractReference2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Reference2CharMap.Entry<K>> i;
                    {
                        this.i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);
                    }

                    @Override
                    public char nextChar() {
                        return ((Reference2CharMap.Entry)this.i.next()).getCharValue();
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
                return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractReference2CharMap.this), 320);
            }
        };
    }

    @Override
    public void putAll(Map<? extends K, ? extends Character> m) {
        if (m instanceof Reference2CharMap) {
            ObjectIterator i = Reference2CharMaps.fastIterator((Reference2CharMap)m);
            while (i.hasNext()) {
                Reference2CharMap.Entry e = (Reference2CharMap.Entry)i.next();
                this.put(e.getKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Reference2CharMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Reference2CharMap.Entry)i.next()).hashCode();
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
        return this.reference2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Reference2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Reference2CharMap.Entry e = (Reference2CharMap.Entry)i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<K>
    extends AbstractObjectSet<Reference2CharMap.Entry<K>> {
        protected final Reference2CharMap<K> map;

        public BasicEntrySet(Reference2CharMap<K> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Reference2CharMap.Entry) {
                Reference2CharMap.Entry e = (Reference2CharMap.Entry)o;
                Object k = e.getKey();
                return this.map.containsKey(k) && this.map.getChar(k) == e.getCharValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Character)) {
                return false;
            }
            return this.map.containsKey(k) && this.map.getChar(k) == ((Character)value).charValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Reference2CharMap.Entry) {
                Reference2CharMap.Entry e = (Reference2CharMap.Entry)o;
                return this.map.remove(e.getKey(), e.getCharValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
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
        public ObjectSpliterator<Reference2CharMap.Entry<K>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<K>
    implements Reference2CharMap.Entry<K> {
        protected K key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(K key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
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
            if (o instanceof Reference2CharMap.Entry) {
                Reference2CharMap.Entry e = (Reference2CharMap.Entry)o;
                return this.key == e.getKey() && this.value == e.getCharValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            if (value == null || !(value instanceof Character)) {
                return false;
            }
            return this.key == key && this.value == ((Character)value).charValue();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

