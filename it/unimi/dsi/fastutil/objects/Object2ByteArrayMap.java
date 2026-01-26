/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ByteArrayMap$EntrySet.EntrySetSpliterator
 *  it.unimi.dsi.fastutil.objects.Object2ByteArrayMap$KeySet.KeySetSpliterator
 *  it.unimi.dsi.fastutil.objects.Object2ByteArrayMap$ValuesCollection.ValuesSpliterator
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObject2ByteMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2ByteArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class Object2ByteArrayMap<K>
extends AbstractObject2ByteMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    protected transient Object[] key;
    protected transient byte[] value;
    protected int size;
    protected transient Object2ByteMap.FastEntrySet<K> entries;
    protected transient ObjectSet<K> keys;
    protected transient ByteCollection values;

    public Object2ByteArrayMap(Object[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Object2ByteArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = ByteArrays.EMPTY_ARRAY;
    }

    public Object2ByteArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new byte[capacity];
    }

    public Object2ByteArrayMap(Object2ByteMap<K> m) {
        this(m.size());
        int i = 0;
        for (Object2ByteMap.Entry entry : m.object2ByteEntrySet()) {
            this.key[i] = entry.getKey();
            this.value[i] = entry.getByteValue();
            ++i;
        }
        this.size = i;
    }

    public Object2ByteArrayMap(Map<? extends K, ? extends Byte> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<K, Byte> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Object2ByteArrayMap(Object[] key, byte[] value, int size) {
        this.key = key;
        this.value = value;
        this.size = size;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
        if (size > key.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
        }
    }

    public Object2ByteMap.FastEntrySet<K> object2ByteEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (!Objects.equals(key[i], k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public byte getByte(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (!Objects.equals(key[i], k)) continue;
            return this.value[i];
        }
        return this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        int i = this.size;
        while (i-- != 0) {
            this.key[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(byte v) {
        int i = this.size;
        while (i-- != 0) {
            if (this.value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public byte put(K k, byte v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            byte oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            byte[] newValue = new byte[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                newKey[i] = this.key[i];
                newValue[i] = this.value[i];
            }
            this.key = newKey;
            this.value = newValue;
        }
        this.key[this.size] = k;
        this.value[this.size] = v;
        ++this.size;
        return this.defRetValue;
    }

    @Override
    public byte removeByte(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        byte oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.key[this.size] = null;
        return oldValue;
    }

    @Override
    public ObjectSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ByteCollection values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Object2ByteArrayMap<K> clone() {
        Object2ByteArrayMap c;
        try {
            c = (Object2ByteArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (byte[])this.value.clone();
        c.entries = null;
        c.keys = null;
        c.values = null;
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int max = this.size;
        for (int i = 0; i < max; ++i) {
            s.writeObject(this.key[i]);
            s.writeByte(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new byte[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readByte();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Object2ByteMap.Entry<K>>
    implements Object2ByteMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Object2ByteMap.Entry<K>> iterator() {
            return new ObjectIterator<Object2ByteMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Object2ByteArrayMap.this.size;
                }

                @Override
                public Object2ByteMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractObject2ByteMap.BasicEntry<Object>(Object2ByteArrayMap.this.key[this.curr], Object2ByteArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2ByteArrayMap.this.key, this.next + 1, Object2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2ByteArrayMap.this.value, this.next + 1, Object2ByteArrayMap.this.value, this.next, tail);
                    Object2ByteArrayMap.this.key[Object2ByteArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Object2ByteMap.Entry<K>> action) {
                    int max = Object2ByteArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractObject2ByteMap.BasicEntry<Object>(Object2ByteArrayMap.this.key[this.curr], Object2ByteArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Object2ByteMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Object2ByteMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractObject2ByteMap.BasicEntry<K> entry = new AbstractObject2ByteMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Object2ByteArrayMap.this.size;
                }

                @Override
                public Object2ByteMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Object2ByteArrayMap.this.key[this.curr];
                    this.entry.value = Object2ByteArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Object2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2ByteArrayMap.this.key, this.next + 1, Object2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2ByteArrayMap.this.value, this.next + 1, Object2ByteArrayMap.this.value, this.next, tail);
                    Object2ByteArrayMap.this.key[Object2ByteArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Object2ByteMap.Entry<K>> action) {
                    int max = Object2ByteArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Object2ByteArrayMap.this.key[this.curr];
                        this.entry.value = Object2ByteArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Object2ByteMap.Entry<K>> spliterator() {
            return new EntrySetSpliterator(0, Object2ByteArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Object2ByteMap.Entry<K>> action) {
            int max = Object2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractObject2ByteMap.BasicEntry<Object>(Object2ByteArrayMap.this.key[i], Object2ByteArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2ByteMap.Entry<K>> action) {
            AbstractObject2ByteMap.BasicEntry entry = new AbstractObject2ByteMap.BasicEntry();
            int max = Object2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Object2ByteArrayMap.this.key[i];
                entry.value = Object2ByteArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Object2ByteArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            Object k = e.getKey();
            return Object2ByteArrayMap.this.containsKey(k) && Object2ByteArrayMap.this.getByte(k) == ((Byte)e.getValue()).byteValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            Object k = e.getKey();
            byte v = (Byte)e.getValue();
            int oldPos = Object2ByteArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Object2ByteArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Object2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2ByteArrayMap.this.key, oldPos + 1, Object2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2ByteArrayMap.this.value, oldPos + 1, Object2ByteArrayMap.this.value, oldPos, tail);
            --Object2ByteArrayMap.this.size;
            Object2ByteArrayMap.this.key[Object2ByteArrayMap.this.size] = null;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Object2ByteMap.Entry<K>>
        implements ObjectSpliterator<Object2ByteMap.Entry<K>> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Object2ByteMap.Entry<K> get(int location) {
                return new AbstractObject2ByteMap.BasicEntry<Object>(Object2ByteArrayMap.this.key[location], Object2ByteArrayMap.this.value[location]);
            }

            protected final it.unimi.dsi.fastutil.objects.Object2ByteArrayMap$EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractObjectSet<K> {
        private KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return Object2ByteArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(Object k) {
            int oldPos = Object2ByteArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Object2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2ByteArrayMap.this.key, oldPos + 1, Object2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2ByteArrayMap.this.value, oldPos + 1, Object2ByteArrayMap.this.value, oldPos, tail);
            --Object2ByteArrayMap.this.size;
            Object2ByteArrayMap.this.key[Object2ByteArrayMap.this.size] = null;
            return true;
        }

        @Override
        public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Object2ByteArrayMap.this.size;
                }

                @Override
                public K next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Object2ByteArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Object2ByteArrayMap.this.size - this.pos;
                    System.arraycopy(Object2ByteArrayMap.this.key, this.pos, Object2ByteArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Object2ByteArrayMap.this.value, this.pos, Object2ByteArrayMap.this.value, this.pos - 1, tail);
                    --Object2ByteArrayMap.this.size;
                    --this.pos;
                    Object2ByteArrayMap.this.key[Object2ByteArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super K> action) {
                    int max = Object2ByteArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Object2ByteArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return new KeySetSpliterator(0, Object2ByteArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super K> action) {
            int max = Object2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Object2ByteArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Object2ByteArrayMap.this.size;
        }

        @Override
        public void clear() {
            Object2ByteArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<K>
        implements ObjectSpliterator<K> {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final K get(int location) {
                return Object2ByteArrayMap.this.key[location];
            }

            protected final it.unimi.dsi.fastutil.objects.Object2ByteArrayMap$KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                int max = Object2ByteArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Object2ByteArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractByteCollection {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(byte v) {
            return Object2ByteArrayMap.this.containsValue(v);
        }

        @Override
        public ByteIterator iterator() {
            return new ByteIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Object2ByteArrayMap.this.size;
                }

                @Override
                public byte nextByte() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Object2ByteArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Object2ByteArrayMap.this.size - this.pos;
                    System.arraycopy(Object2ByteArrayMap.this.key, this.pos, Object2ByteArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Object2ByteArrayMap.this.value, this.pos, Object2ByteArrayMap.this.value, this.pos - 1, tail);
                    --Object2ByteArrayMap.this.size;
                    --this.pos;
                    Object2ByteArrayMap.this.key[Object2ByteArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(ByteConsumer action) {
                    int max = Object2ByteArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Object2ByteArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ByteSpliterator spliterator() {
            return new ValuesSpliterator(0, Object2ByteArrayMap.this.size);
        }

        @Override
        public void forEach(ByteConsumer action) {
            int max = Object2ByteArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Object2ByteArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Object2ByteArrayMap.this.size;
        }

        @Override
        public void clear() {
            Object2ByteArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends ByteSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements ByteSpliterator {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16720;
            }

            @Override
            protected final byte get(int location) {
                return Object2ByteArrayMap.this.value[location];
            }

            protected final it.unimi.dsi.fastutil.objects.Object2ByteArrayMap$ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(ByteConsumer action) {
                int max = Object2ByteArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Object2ByteArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

