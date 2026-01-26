/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMaps;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractShort2ReferenceMap<V>
extends AbstractShort2ReferenceFunction<V>
implements Short2ReferenceMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2ReferenceMap() {
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2ReferenceEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2ReferenceMap.Entry)i.next()).getShortKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object v) {
        Iterator i = this.short2ReferenceEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2ReferenceMap.Entry)i.next()).getValue() != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ShortSet keySet() {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short k) {
                return AbstractShort2ReferenceMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2ReferenceMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Short2ReferenceMaps.fastIterator(AbstractShort2ReferenceMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return ((Short2ReferenceMap.Entry)this.i.next()).getShortKey();
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
                return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2ReferenceMap.this), 321);
            }
        };
    }

    @Override
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractShort2ReferenceMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2ReferenceMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Short2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Short2ReferenceMaps.fastIterator(AbstractShort2ReferenceMap.this);
                    }

                    @Override
                    public V next() {
                        return ((Short2ReferenceMap.Entry)this.i.next()).getValue();
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
                return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(AbstractShort2ReferenceMap.this), 64);
            }
        };
    }

    @Override
    public void putAll(Map<? extends Short, ? extends V> m) {
        if (m instanceof Short2ReferenceMap) {
            ObjectIterator i = Short2ReferenceMaps.fastIterator((Short2ReferenceMap)m);
            while (i.hasNext()) {
                Short2ReferenceMap.Entry e = (Short2ReferenceMap.Entry)i.next();
                this.put(e.getShortKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Short, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Short, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Short2ReferenceMaps.fastIterator(this);
        while (n-- != 0) {
            h += ((Short2ReferenceMap.Entry)i.next()).hashCode();
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
        return this.short2ReferenceEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Short2ReferenceMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2ReferenceMap.Entry e = (Short2ReferenceMap.Entry)i.next();
            s.append(String.valueOf(e.getShortKey()));
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
    extends AbstractObjectSet<Short2ReferenceMap.Entry<V>> {
        protected final Short2ReferenceMap<V> map;

        public BasicEntrySet(Short2ReferenceMap<V> map) {
            this.map = map;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Short2ReferenceMap.Entry) {
                Short2ReferenceMap.Entry e = (Short2ReferenceMap.Entry)o;
                short k = e.getShortKey();
                return this.map.containsKey(k) && this.map.get(k) == e.getValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Short)) {
                return false;
            }
            short k = (Short)key;
            Object value = e.getValue();
            return this.map.containsKey(k) && this.map.get(k) == value;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            if (o instanceof Short2ReferenceMap.Entry) {
                Short2ReferenceMap.Entry e = (Short2ReferenceMap.Entry)o;
                return this.map.remove(e.getShortKey(), e.getValue());
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Short)) {
                return false;
            }
            short k = (Short)key;
            Object v = e.getValue();
            return this.map.remove(k, v);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public ObjectSpliterator<Short2ReferenceMap.Entry<V>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this.map), 65);
        }
    }

    public static class BasicEntry<V>
    implements Short2ReferenceMap.Entry<V> {
        protected short key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, V value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(short key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public short getShortKey() {
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
            if (o instanceof Short2ReferenceMap.Entry) {
                Short2ReferenceMap.Entry e = (Short2ReferenceMap.Entry)o;
                return this.key == e.getShortKey() && this.value == e.getValue();
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            if (key == null || !(key instanceof Short)) {
                return false;
            }
            Object value = e.getValue();
            return this.key == (Short)key && this.value == value;
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

