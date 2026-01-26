/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap$EntrySet.EntrySetSpliterator
 *  it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap$KeySet.KeySetSpliterator
 *  it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap$ValuesCollection.ValuesSpliterator
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2ObjectMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
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
import java.util.function.DoubleConsumer;

public class Double2ObjectArrayMap<V>
extends AbstractDouble2ObjectMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    protected transient double[] key;
    protected transient Object[] value;
    protected int size;
    protected transient Double2ObjectMap.FastEntrySet<V> entries;
    protected transient DoubleSet keys;
    protected transient ObjectCollection<V> values;

    public Double2ObjectArrayMap(double[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Double2ObjectArrayMap() {
        this.key = DoubleArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Double2ObjectArrayMap(int capacity) {
        this.key = new double[capacity];
        this.value = new Object[capacity];
    }

    public Double2ObjectArrayMap(Double2ObjectMap<V> m) {
        this(m.size());
        int i = 0;
        for (Double2ObjectMap.Entry entry : m.double2ObjectEntrySet()) {
            this.key[i] = entry.getDoubleKey();
            this.value[i] = entry.getValue();
            ++i;
        }
        this.size = i;
    }

    public Double2ObjectArrayMap(Map<? extends Double, ? extends V> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<Double, V> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue();
            ++i;
        }
        this.size = i;
    }

    public Double2ObjectArrayMap(double[] key, Object[] value, int size) {
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

    public Double2ObjectMap.FastEntrySet<V> double2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(double k) {
        double[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) != Double.doubleToLongBits(k)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public V get(double k) {
        double[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) != Double.doubleToLongBits(k)) continue;
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
    public boolean containsKey(double k) {
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
    public V put(double k, V v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            double[] newKey = new double[this.size == 0 ? 2 : this.size * 2];
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
    public V remove(double k) {
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
    public DoubleSet keySet() {
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

    public Double2ObjectArrayMap<V> clone() {
        Double2ObjectArrayMap c;
        try {
            c = (Double2ObjectArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (double[])this.key.clone();
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
            s.writeDouble(this.key[i]);
            s.writeObject(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new double[this.size];
        this.value = new Object[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readDouble();
            this.value[i] = s.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Double2ObjectMap.Entry<V>>
    implements Double2ObjectMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Double2ObjectMap.Entry<V>> iterator() {
            return new ObjectIterator<Double2ObjectMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Double2ObjectArrayMap.this.size;
                }

                @Override
                public Double2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractDouble2ObjectMap.BasicEntry<Object>(Double2ObjectArrayMap.this.key[this.curr], Double2ObjectArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Double2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Double2ObjectArrayMap.this.key, this.next + 1, Double2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Double2ObjectArrayMap.this.value, this.next + 1, Double2ObjectArrayMap.this.value, this.next, tail);
                    Double2ObjectArrayMap.this.value[Double2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Double2ObjectMap.Entry<V>> action) {
                    int max = Double2ObjectArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractDouble2ObjectMap.BasicEntry<Object>(Double2ObjectArrayMap.this.key[this.curr], Double2ObjectArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Double2ObjectMap.Entry<V>> fastIterator() {
            return new ObjectIterator<Double2ObjectMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractDouble2ObjectMap.BasicEntry<V> entry = new AbstractDouble2ObjectMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Double2ObjectArrayMap.this.size;
                }

                @Override
                public Double2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Double2ObjectArrayMap.this.key[this.curr];
                    this.entry.value = Double2ObjectArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Double2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Double2ObjectArrayMap.this.key, this.next + 1, Double2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Double2ObjectArrayMap.this.value, this.next + 1, Double2ObjectArrayMap.this.value, this.next, tail);
                    Double2ObjectArrayMap.this.value[Double2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Double2ObjectMap.Entry<V>> action) {
                    int max = Double2ObjectArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Double2ObjectArrayMap.this.key[this.curr];
                        this.entry.value = Double2ObjectArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Double2ObjectMap.Entry<V>> spliterator() {
            return new EntrySetSpliterator(0, Double2ObjectArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Double2ObjectMap.Entry<V>> action) {
            int max = Double2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractDouble2ObjectMap.BasicEntry<Object>(Double2ObjectArrayMap.this.key[i], Double2ObjectArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Double2ObjectMap.Entry<V>> action) {
            AbstractDouble2ObjectMap.BasicEntry entry = new AbstractDouble2ObjectMap.BasicEntry();
            int max = Double2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Double2ObjectArrayMap.this.key[i];
                entry.value = Double2ObjectArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Double2ObjectArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            double k = (Double)e.getKey();
            return Double2ObjectArrayMap.this.containsKey(k) && Objects.equals(Double2ObjectArrayMap.this.get(k), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            double k = (Double)e.getKey();
            Object v = e.getValue();
            int oldPos = Double2ObjectArrayMap.this.findKey(k);
            if (oldPos == -1 || !Objects.equals(v, Double2ObjectArrayMap.this.value[oldPos])) {
                return false;
            }
            int tail = Double2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Double2ObjectArrayMap.this.key, oldPos + 1, Double2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Double2ObjectArrayMap.this.value, oldPos + 1, Double2ObjectArrayMap.this.value, oldPos, tail);
            --Double2ObjectArrayMap.this.size;
            Double2ObjectArrayMap.this.value[Double2ObjectArrayMap.this.size] = null;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Double2ObjectMap.Entry<V>>
        implements ObjectSpliterator<Double2ObjectMap.Entry<V>> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Double2ObjectMap.Entry<V> get(int location) {
                return new AbstractDouble2ObjectMap.BasicEntry<Object>(Double2ObjectArrayMap.this.key[location], Double2ObjectArrayMap.this.value[location]);
            }

            protected final it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap$EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractDoubleSet {
        private KeySet() {
        }

        @Override
        public boolean contains(double k) {
            return Double2ObjectArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(double k) {
            int oldPos = Double2ObjectArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Double2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Double2ObjectArrayMap.this.key, oldPos + 1, Double2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Double2ObjectArrayMap.this.value, oldPos + 1, Double2ObjectArrayMap.this.value, oldPos, tail);
            --Double2ObjectArrayMap.this.size;
            Double2ObjectArrayMap.this.value[Double2ObjectArrayMap.this.size] = null;
            return true;
        }

        @Override
        public DoubleIterator iterator() {
            return new DoubleIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Double2ObjectArrayMap.this.size;
                }

                @Override
                public double nextDouble() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Double2ObjectArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Double2ObjectArrayMap.this.size - this.pos;
                    System.arraycopy(Double2ObjectArrayMap.this.key, this.pos, Double2ObjectArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Double2ObjectArrayMap.this.value, this.pos, Double2ObjectArrayMap.this.value, this.pos - 1, tail);
                    --Double2ObjectArrayMap.this.size;
                    --this.pos;
                    Double2ObjectArrayMap.this.value[Double2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(DoubleConsumer action) {
                    int max = Double2ObjectArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Double2ObjectArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public DoubleSpliterator spliterator() {
            return new KeySetSpliterator(0, Double2ObjectArrayMap.this.size);
        }

        @Override
        public void forEach(DoubleConsumer action) {
            int max = Double2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Double2ObjectArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Double2ObjectArrayMap.this.size;
        }

        @Override
        public void clear() {
            Double2ObjectArrayMap.this.clear();
        }

        final class KeySetSpliterator
        extends DoubleSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements DoubleSpliterator {
            KeySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16721;
            }

            @Override
            protected final double get(int location) {
                return Double2ObjectArrayMap.this.key[location];
            }

            protected final it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap$KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                int max = Double2ObjectArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Double2ObjectArrayMap.this.key[this.pos++]);
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
            return Double2ObjectArrayMap.this.containsValue(v);
        }

        @Override
        public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Double2ObjectArrayMap.this.size;
                }

                @Override
                public V next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Double2ObjectArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Double2ObjectArrayMap.this.size - this.pos;
                    System.arraycopy(Double2ObjectArrayMap.this.key, this.pos, Double2ObjectArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Double2ObjectArrayMap.this.value, this.pos, Double2ObjectArrayMap.this.value, this.pos - 1, tail);
                    --Double2ObjectArrayMap.this.size;
                    --this.pos;
                    Double2ObjectArrayMap.this.value[Double2ObjectArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super V> action) {
                    int max = Double2ObjectArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Double2ObjectArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<V> spliterator() {
            return new ValuesSpliterator(0, Double2ObjectArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            int max = Double2ObjectArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Double2ObjectArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Double2ObjectArrayMap.this.size;
        }

        @Override
        public void clear() {
            Double2ObjectArrayMap.this.clear();
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
                return Double2ObjectArrayMap.this.value[location];
            }

            protected final it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap$ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
                int max = Double2ObjectArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Double2ObjectArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

