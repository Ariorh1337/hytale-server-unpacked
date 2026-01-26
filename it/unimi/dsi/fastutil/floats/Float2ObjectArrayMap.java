/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap$EntrySet.EntrySetSpliterator
 *  it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap$KeySet.KeySetSpliterator
 *  it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap$ValuesCollection.ValuesSpliterator
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2ObjectMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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

public class Float2ObjectArrayMap<V>
extends AbstractFloat2ObjectMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    protected transient float[] key;
    protected transient Object[] value;
    protected int size;
    protected transient Float2ObjectMap.FastEntrySet<V> entries;
    protected transient FloatSet keys;
    protected transient ObjectCollection<V> values;

    public Float2ObjectArrayMap(float[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Float2ObjectArrayMap() {
        this.key = FloatArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Float2ObjectArrayMap(int capacity) {
        this.key = new float[capacity];
        this.value = new Object[capacity];
    }

    public Float2ObjectArrayMap(Float2ObjectMap<V> m) {
        this(m.size());
        int i = 0;
        for (Float2ObjectMap.Entry entry : m.float2ObjectEntrySet()) {
            this.key[i] = entry.getFloatKey();
            this.value[i] = entry.getValue();
            ++i;
        }
        this.size = i;
    }

    public Float2ObjectArrayMap(Map<? extends Float, ? extends V> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<Float, V> e : m.entrySet()) {
            this.key[i] = e.getKey().floatValue();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Float2ObjectArrayMap(float[] key, Object[] value, int size) {
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

    public Float2ObjectMap.FastEntrySet<V> float2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(float k) {
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != Float.floatToIntBits(k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public V get(float k) {
        float[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != Float.floatToIntBits(k)) continue;
            return (V)this.value[i];
        }
        return (V)this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        int i = this.size;
        while (i-- != 0) {
            this.value[i] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsKey(float k) {
        return this.findKey(k) != -1;
    }

    @Override
    public boolean containsValue(Object v) {
        int i = this.size;
        while (i-- != 0) {
            if (!Objects.equals(this.value[i], v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public V put(float k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            float[] newKey = new float[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];
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
        return (V)this.defRetValue;
    }

    @Override
    public V remove(float k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return (V)this.defRetValue;
        }
        Object oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.value[this.size] = null;
        return (V)oldValue;
    }

    @Override
    public FloatSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Float2ObjectArrayMap<V> clone() {
        Float2ObjectArrayMap c;
        try {
            c = (Float2ObjectArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (float[])this.key.clone();
        c.value = (Object[])this.value.clone();
        c.entries = null;
        c.keys = null;
        c.values = null;
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int max = this.size;
        for (int i = 0; i < max; ++i) {
            s.writeFloat(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new float[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readFloat();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Float2ObjectMap.Entry<V>>
    implements Float2ObjectMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Float2ObjectMap.Entry<V>> iterator() {
            return new ObjectIterator<Float2ObjectMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Float2ObjectArrayMap.this.size;
                }

                @Override
                public Float2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractFloat2ObjectMap.BasicEntry<Object>(Float2ObjectArrayMap.this.key[this.curr], Float2ObjectArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2ObjectArrayMap.this.key, this.next + 1, Float2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2ObjectArrayMap.this.value, this.next + 1, Float2ObjectArrayMap.this.value, this.next, tail);
                    Float2ObjectArrayMap.this.value[Float2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Float2ObjectMap.Entry<V>> action) {
                    int max = Float2ObjectArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractFloat2ObjectMap.BasicEntry<Object>(Float2ObjectArrayMap.this.key[this.curr], Float2ObjectArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Float2ObjectMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Float2ObjectMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractFloat2ObjectMap.BasicEntry<V> entry = new AbstractFloat2ObjectMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Float2ObjectArrayMap.this.size;
                }

                @Override
                public Float2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Float2ObjectArrayMap.this.key[this.curr];
                    this.entry.value = Float2ObjectArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Float2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Float2ObjectArrayMap.this.key, this.next + 1, Float2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Float2ObjectArrayMap.this.value, this.next + 1, Float2ObjectArrayMap.this.value, this.next, tail);
                    Float2ObjectArrayMap.this.value[Float2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Float2ObjectMap.Entry<V>> action) {
                    int max = Float2ObjectArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Float2ObjectArrayMap.this.key[this.curr];
                        this.entry.value = Float2ObjectArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Float2ObjectMap.Entry<V>> spliterator() {
            return new EntrySetSpliterator(0, Float2ObjectArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Float2ObjectMap.Entry<V>> action) {
            int max = Float2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractFloat2ObjectMap.BasicEntry<Object>(Float2ObjectArrayMap.this.key[i], Float2ObjectArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Float2ObjectMap.Entry<V>> action) {
            AbstractFloat2ObjectMap.BasicEntry entry = new AbstractFloat2ObjectMap.BasicEntry();
            int max = Float2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Float2ObjectArrayMap.this.key[i];
                entry.value = Float2ObjectArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Float2ObjectArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            return Float2ObjectArrayMap.this.containsKey(k) && Objects.equals(Float2ObjectArrayMap.this.get(k), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            Object v = e.getValue();
            int oldPos = Float2ObjectArrayMap.this.findKey(k);
            if (oldPos == -1 || !Objects.equals(v, Float2ObjectArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Float2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2ObjectArrayMap.this.key, oldPos + 1, Float2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2ObjectArrayMap.this.value, oldPos + 1, Float2ObjectArrayMap.this.value, oldPos, tail);
            --Float2ObjectArrayMap.this.size;
            Float2ObjectArrayMap.this.value[Float2ObjectArrayMap.this.size] = null;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Float2ObjectMap.Entry<V>>
        implements ObjectSpliterator<Float2ObjectMap.Entry<V>> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Float2ObjectMap.Entry<V> get(int location) {
                return new AbstractFloat2ObjectMap.BasicEntry<Object>(Float2ObjectArrayMap.this.key[location], Float2ObjectArrayMap.this.value[location]);
            }

            protected final it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap$EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractFloatSet {
        private KeySet() {
        }

        @Override
        public boolean contains(float k) {
            return Float2ObjectArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(float k) {
            int oldPos = Float2ObjectArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Float2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Float2ObjectArrayMap.this.key, oldPos + 1, Float2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Float2ObjectArrayMap.this.value, oldPos + 1, Float2ObjectArrayMap.this.value, oldPos, tail);
            --Float2ObjectArrayMap.this.size;
            Float2ObjectArrayMap.this.value[Float2ObjectArrayMap.this.size] = null;
            return true;
        }

        @Override
        public FloatIterator iterator() {
            return new FloatIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Float2ObjectArrayMap.this.size;
                }

                @Override
                public float nextFloat() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Float2ObjectArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Float2ObjectArrayMap.this.size - this.pos;
                    System.arraycopy(Float2ObjectArrayMap.this.key, this.pos, Float2ObjectArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Float2ObjectArrayMap.this.value, this.pos, Float2ObjectArrayMap.this.value, this.pos - 1, tail);
                    --Float2ObjectArrayMap.this.size;
                    --this.pos;
                    Float2ObjectArrayMap.this.value[Float2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(FloatConsumer action) {
                    int max = Float2ObjectArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Float2ObjectArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public FloatSpliterator spliterator() {
            return new KeySetSpliterator(0, Float2ObjectArrayMap.this.size);
        }

        @Override
        public void forEach(FloatConsumer action) {
            int max = Float2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Float2ObjectArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Float2ObjectArrayMap.this.size;
        }

        @Override
        public void clear() {
            Float2ObjectArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends FloatSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements FloatSpliterator {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16721;
            }

            @Override
            protected final float get(int location) {
                return Float2ObjectArrayMap.this.key[location];
            }

            protected final it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap$KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(FloatConsumer action) {
                int max = Float2ObjectArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Float2ObjectArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractObjectCollection<V> {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(Object v) {
            return Float2ObjectArrayMap.this.containsValue(v);
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Float2ObjectArrayMap.this.size;
                }

                @Override
                public V next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Float2ObjectArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Float2ObjectArrayMap.this.size - this.pos;
                    System.arraycopy(Float2ObjectArrayMap.this.key, this.pos, Float2ObjectArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Float2ObjectArrayMap.this.value, this.pos, Float2ObjectArrayMap.this.value, this.pos - 1, tail);
                    --Float2ObjectArrayMap.this.size;
                    --this.pos;
                    Float2ObjectArrayMap.this.value[Float2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super V> action) {
                    int max = Float2ObjectArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Float2ObjectArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<V> spliterator() {
            return new ValuesSpliterator(0, Float2ObjectArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            int max = Float2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Float2ObjectArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Float2ObjectArrayMap.this.size;
        }

        @Override
        public void clear() {
            Float2ObjectArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<V>
        implements ObjectSpliterator<V> {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16464;
            }

            @Override
            protected final V get(int location) {
                return Float2ObjectArrayMap.this.value[location];
            }

            protected final it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap$ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
                int max = Float2ObjectArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Float2ObjectArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

