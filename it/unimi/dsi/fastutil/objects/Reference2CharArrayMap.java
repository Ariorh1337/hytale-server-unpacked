/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2CharArrayMap$EntrySet.EntrySetSpliterator
 *  it.unimi.dsi.fastutil.objects.Reference2CharArrayMap$KeySet.KeySetSpliterator
 *  it.unimi.dsi.fastutil.objects.Reference2CharArrayMap$ValuesCollection.ValuesSpliterator
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2CharMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.Reference2CharArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2CharMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Reference2CharArrayMap<K>
extends AbstractReference2CharMap<K>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    protected transient Object[] key;
    protected transient char[] value;
    protected int size;
    protected transient Reference2CharMap.FastEntrySet<K> entries;
    protected transient ReferenceSet<K> keys;
    protected transient CharCollection values;

    public Reference2CharArrayMap(Object[] key, char[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Reference2CharArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = CharArrays.EMPTY_ARRAY;
    }

    public Reference2CharArrayMap(int capacity) {
        this.key = new Object[capacity];
        this.value = new char[capacity];
    }

    public Reference2CharArrayMap(Reference2CharMap<K> m) {
        this(m.size());
        int i = 0;
        for (Reference2CharMap.Entry entry : m.reference2CharEntrySet()) {
            this.key[i] = entry.getKey();
            this.value[i] = entry.getCharValue();
            ++i;
        }
        this.size = i;
    }

    public Reference2CharArrayMap(Map<? extends K, ? extends Character> m) {
        this(m.size());
        int i = 0;
        for (Map.Entry<K, Character> e : m.entrySet()) {
            this.key[i] = e.getKey();
            this.value[i] = e.getValue().charValue();
            ++i;
        }
        this.size = i;
    }

    public Reference2CharArrayMap(Object[] key, char[] value, int size) {
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

    public Reference2CharMap.FastEntrySet<K> reference2CharEntrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private int findKey(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
            return i;
        }
        return -1;
    }

    @Override
    public char getChar(Object k) {
        Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] != k) continue;
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
    public boolean containsValue(char v) {
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
    public char put(K k, char v) {
        int oldKey = this.findKey(k);
        if (oldKey != -1) {
            char oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            char[] newValue = new char[this.size == 0 ? 2 : this.size * 2];
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
    public char removeChar(Object k) {
        int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        char oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.key[this.size] = null;
        return oldValue;
    }

    @Override
    public ReferenceSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public CharCollection values() {
        if (this.values == null) {
            this.values = new ValuesCollection();
        }
        return this.values;
    }

    public Reference2CharArrayMap<K> clone() {
        Reference2CharArrayMap c;
        try {
            c = (Reference2CharArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (Object[])this.key.clone();
        c.value = (char[])this.value.clone();
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
            s.writeChar(this.value[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new char[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readChar();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Reference2CharMap.Entry<K>>
    implements Reference2CharMap.FastEntrySet<K> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Reference2CharMap.Entry<K>> iterator() {
            return new ObjectIterator<Reference2CharMap.Entry<K>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Reference2CharArrayMap.this.size;
                }

                @Override
                public Reference2CharMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractReference2CharMap.BasicEntry<Object>(Reference2CharArrayMap.this.key[this.curr], Reference2CharArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Reference2CharArrayMap.this.size-- - this.next--;
                    System.arraycopy(Reference2CharArrayMap.this.key, this.next + 1, Reference2CharArrayMap.this.key, this.next, tail);
                    System.arraycopy(Reference2CharArrayMap.this.value, this.next + 1, Reference2CharArrayMap.this.value, this.next, tail);
                    Reference2CharArrayMap.this.key[Reference2CharArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Reference2CharMap.Entry<K>> action) {
                    int max = Reference2CharArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        action.accept(new AbstractReference2CharMap.BasicEntry<Object>(Reference2CharArrayMap.this.key[this.curr], Reference2CharArrayMap.this.value[this.next++]));
                    }
                }
            };
        }

        @Override
        public ObjectIterator<Reference2CharMap.Entry<K>> fastIterator() {
            return new ObjectIterator<Reference2CharMap.Entry<K>>(){
                int next = 0;
                int curr = -1;
                final AbstractReference2CharMap.BasicEntry<K> entry = new AbstractReference2CharMap.BasicEntry();

                @Override
                public boolean hasNext() {
                    return this.next < Reference2CharArrayMap.this.size;
                }

                @Override
                public Reference2CharMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Reference2CharArrayMap.this.key[this.curr];
                    this.entry.value = Reference2CharArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Reference2CharArrayMap.this.size-- - this.next--;
                    System.arraycopy(Reference2CharArrayMap.this.key, this.next + 1, Reference2CharArrayMap.this.key, this.next, tail);
                    System.arraycopy(Reference2CharArrayMap.this.value, this.next + 1, Reference2CharArrayMap.this.value, this.next, tail);
                    Reference2CharArrayMap.this.key[Reference2CharArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super Reference2CharMap.Entry<K>> action) {
                    int max = Reference2CharArrayMap.this.size;
                    while (this.next < max) {
                        this.curr = this.next;
                        this.entry.key = Reference2CharArrayMap.this.key[this.curr];
                        this.entry.value = Reference2CharArrayMap.this.value[this.next++];
                        action.accept(this.entry);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<Reference2CharMap.Entry<K>> spliterator() {
            return new EntrySetSpliterator(0, Reference2CharArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super Reference2CharMap.Entry<K>> action) {
            int max = Reference2CharArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(new AbstractReference2CharMap.BasicEntry<Object>(Reference2CharArrayMap.this.key[i], Reference2CharArrayMap.this.value[i]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2CharMap.Entry<K>> action) {
            AbstractReference2CharMap.BasicEntry entry = new AbstractReference2CharMap.BasicEntry();
            int max = Reference2CharArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                entry.key = Reference2CharArrayMap.this.key[i];
                entry.value = Reference2CharArrayMap.this.value[i];
                action.accept(entry);
            }
        }

        @Override
        public int size() {
            return Reference2CharArrayMap.this.size;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            Object k = e.getKey();
            return Reference2CharArrayMap.this.containsKey(k) && Reference2CharArrayMap.this.getChar(k) == ((Character)e.getValue()).charValue();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            Object k = e.getKey();
            char v = ((Character)e.getValue()).charValue();
            int oldPos = Reference2CharArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Reference2CharArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Reference2CharArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2CharArrayMap.this.key, oldPos + 1, Reference2CharArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2CharArrayMap.this.value, oldPos + 1, Reference2CharArrayMap.this.value, oldPos, tail);
            --Reference2CharArrayMap.this.size;
            Reference2CharArrayMap.this.key[Reference2CharArrayMap.this.size] = null;
            return true;
        }

        final class EntrySetSpliterator
        extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Reference2CharMap.Entry<K>>
        implements ObjectSpliterator<Reference2CharMap.Entry<K>> {
            EntrySetSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16465;
            }

            @Override
            protected final Reference2CharMap.Entry<K> get(int location) {
                return new AbstractReference2CharMap.BasicEntry<Object>(Reference2CharArrayMap.this.key[location], Reference2CharArrayMap.this.value[location]);
            }

            protected final it.unimi.dsi.fastutil.objects.Reference2CharArrayMap$EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
                return new EntrySetSpliterator(pos, maxPos);
            }
        }
    }

    private final class KeySet
    extends AbstractReferenceSet<K> {
        private KeySet() {
        }

        @Override
        public boolean contains(Object k) {
            return Reference2CharArrayMap.this.findKey(k) != -1;
        }

        @Override
        public boolean remove(Object k) {
            int oldPos = Reference2CharArrayMap.this.findKey(k);
            if (oldPos == -1) {
                return false;
            }
            int tail = Reference2CharArrayMap.this.size - oldPos - 1;
            System.arraycopy(Reference2CharArrayMap.this.key, oldPos + 1, Reference2CharArrayMap.this.key, oldPos, tail);
            System.arraycopy(Reference2CharArrayMap.this.value, oldPos + 1, Reference2CharArrayMap.this.value, oldPos, tail);
            --Reference2CharArrayMap.this.size;
            Reference2CharArrayMap.this.key[Reference2CharArrayMap.this.size] = null;
            return true;
        }

        @Override
        public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Reference2CharArrayMap.this.size;
                }

                @Override
                public K next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Reference2CharArrayMap.this.key[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Reference2CharArrayMap.this.size - this.pos;
                    System.arraycopy(Reference2CharArrayMap.this.key, this.pos, Reference2CharArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Reference2CharArrayMap.this.value, this.pos, Reference2CharArrayMap.this.value, this.pos - 1, tail);
                    --Reference2CharArrayMap.this.size;
                    --this.pos;
                    Reference2CharArrayMap.this.key[Reference2CharArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(Consumer<? super K> action) {
                    int max = Reference2CharArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Reference2CharArrayMap.this.key[this.pos++]);
                    }
                }
            };
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return new KeySetSpliterator(0, Reference2CharArrayMap.this.size);
        }

        @Override
        public void forEach(Consumer<? super K> action) {
            int max = Reference2CharArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Reference2CharArrayMap.this.key[i]);
            }
        }

        @Override
        public int size() {
            return Reference2CharArrayMap.this.size;
        }

        @Override
        public void clear() {
            Reference2CharArrayMap.this.clear();
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
                return Reference2CharArrayMap.this.key[location];
            }

            protected final it.unimi.dsi.fastutil.objects.Reference2CharArrayMap$KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
                return new KeySetSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                int max = Reference2CharArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Reference2CharArrayMap.this.key[this.pos++]);
                }
            }
        }
    }

    private final class ValuesCollection
    extends AbstractCharCollection {
        private ValuesCollection() {
        }

        @Override
        public boolean contains(char v) {
            return Reference2CharArrayMap.this.containsValue(v);
        }

        @Override
        public CharIterator iterator() {
            return new CharIterator(){
                int pos = 0;

                @Override
                public boolean hasNext() {
                    return this.pos < Reference2CharArrayMap.this.size;
                }

                @Override
                public char nextChar() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Reference2CharArrayMap.this.value[this.pos++];
                }

                @Override
                public void remove() {
                    if (this.pos == 0) {
                        throw new IllegalStateException();
                    }
                    int tail = Reference2CharArrayMap.this.size - this.pos;
                    System.arraycopy(Reference2CharArrayMap.this.key, this.pos, Reference2CharArrayMap.this.key, this.pos - 1, tail);
                    System.arraycopy(Reference2CharArrayMap.this.value, this.pos, Reference2CharArrayMap.this.value, this.pos - 1, tail);
                    --Reference2CharArrayMap.this.size;
                    --this.pos;
                    Reference2CharArrayMap.this.key[Reference2CharArrayMap.this.size] = null;
                }

                @Override
                public void forEachRemaining(CharConsumer action) {
                    int max = Reference2CharArrayMap.this.size;
                    while (this.pos < max) {
                        action.accept(Reference2CharArrayMap.this.value[this.pos++]);
                    }
                }
            };
        }

        @Override
        public CharSpliterator spliterator() {
            return new ValuesSpliterator(0, Reference2CharArrayMap.this.size);
        }

        @Override
        public void forEach(CharConsumer action) {
            int max = Reference2CharArrayMap.this.size;
            for (int i = 0; i < max; ++i) {
                action.accept(Reference2CharArrayMap.this.value[i]);
            }
        }

        @Override
        public int size() {
            return Reference2CharArrayMap.this.size;
        }

        @Override
        public void clear() {
            Reference2CharArrayMap.this.clear();
        }

        final class ValuesSpliterator
        extends CharSpliterators.EarlyBindingSizeIndexBasedSpliterator
        implements CharSpliterator {
            ValuesSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            public int characteristics() {
                return 16720;
            }

            @Override
            protected final char get(int location) {
                return Reference2CharArrayMap.this.value[location];
            }

            protected final it.unimi.dsi.fastutil.objects.Reference2CharArrayMap$ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
                return new ValuesSpliterator(pos, maxPos);
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
                int max = Reference2CharArrayMap.this.size;
                while (this.pos < max) {
                    action.accept(Reference2CharArrayMap.this.value[this.pos++]);
                }
            }
        }
    }
}

