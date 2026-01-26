/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractChar2ObjectFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractChar2ObjectMap<V>
extends AbstractChar2ObjectFunction<V>
implements Char2ObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2ObjectMap() {
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2ObjectMap.Entry)i.next()).getCharKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object v) {
        Iterator i = this.char2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2ObjectMap.Entry)i.next()).getValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public CharSet keySet() {
        return new AbstractCharSet(){

            @Override
            public boolean contains(char k) {
                return AbstractChar2ObjectMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ObjectMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2ObjectMap.Entry<V>> i;
                    {
                        this.i = Char2ObjectMaps.fastIterator(AbstractChar2ObjectMap.this);
                    }

                    @Override
                    public char nextChar() {
                        return ((Char2ObjectMap.Entry)this.i.next()).getCharKey();
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
                        this.i.forEachRemaining((? super E entry) -> action.accept(entry.getCharKey()));
                    }
                };
            }

            @Override
            public CharSpliterator spliterator() {
                return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2ObjectMap.this), 321);
            }
        };
    }

    @Override
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractChar2ObjectMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Char2ObjectMap.Entry<V>> i;
                    {
                        this.i = Char2ObjectMaps.fastIterator(AbstractChar2ObjectMap.this);
                    }

                    @Override
                    public V next() {
                        return ((Char2ObjectMap.Entry)this.i.next()).getValue();
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
                    public void forEachRemaining(Consumer<? super V> action) {
                        this.i.forEachRemaining((? super E entry) -> action.accept((Object)entry.getValue()));
                    }
                };
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractChar2ObjectMap.this), 64);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> m) {
        if (m instanceof Char2ObjectMap) {
            ObjectIterator i = Char2ObjectMaps.fastIterator((Char2ObjectMap)m);
            while (i.hasNext()) {
                Char2ObjectMap.Entry e = (Char2ObjectMap.Entry)i.next();
                this.put(e.getCharKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Character, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Character, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Char2ObjectMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Char2ObjectMap.Entry)i.next()).hashCode();
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
        return this.char2ObjectEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Char2ObjectMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2ObjectMap.Entry e = (Char2ObjectMap.Entry)i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            if (this == e.getValue()) {
                s.append("(this map)");
                continue;
            }
            s.append(String.valueOf(e.getValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static abstract class BasicEntrySet<V>
    extends AbstractObjectSet<Char2ObjectMap.Entry<V>> {
        protected final Char2ObjectMap<V> map;

        public BasicEntrySet(Char2ObjectMap<V> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Char2ObjectMap.Entry) {
                Char2ObjectMap.Entry e = (Char2ObjectMap.Entry)o;
                char k = e.getCharKey();
                return this.map.containsKey(k) && Objects.equals(this.map.get(k), e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Character)) {
                return false;
            }
            char k = ((Character)key).charValue();
            Object value = e.getValue();
            return this.map.containsKey(k) && Objects.equals(this.map.get(k), value);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Char2ObjectMap.Entry) {
                Char2ObjectMap.Entry e = (Char2ObjectMap.Entry)o;
                return this.map.remove(e.getCharKey(), e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Character)) {
                return false;
            }
            char k = ((Character)key).charValue();
            Object v = e.getValue();
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Char2ObjectMap.Entry<V>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<V>
    implements Char2ObjectMap.Entry<V> {
        protected char key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, V value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public char getCharKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Char2ObjectMap.Entry) {
                Char2ObjectMap.Entry e = (Char2ObjectMap.Entry)o;
                return this.key == e.getCharKey() && Objects.equals(this.value, e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Character)) {
                return false;
            }
            Object value = e.getValue();
            return this.key == ((Character)key).charValue() && Objects.equals(this.value, value);
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

