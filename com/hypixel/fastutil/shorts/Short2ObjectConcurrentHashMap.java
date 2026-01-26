/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.fastutil.shorts;

import com.hypixel.fastutil.FastCollection;
import com.hypixel.fastutil.shorts.Short2ObjectOperator;
import com.hypixel.fastutil.util.SneakyThrow;
import com.hypixel.fastutil.util.TLRUtil;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import sun.misc.Unsafe;

public class Short2ObjectConcurrentHashMap<V> {
    protected static final long serialVersionUID = 7249069246763182397L;
    protected static final int MAXIMUM_CAPACITY = 0x40000000;
    protected static final int DEFAULT_CAPACITY = 16;
    protected static final int MAX_ARRAY_SIZE = 0x7FFFFFF7;
    protected static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    protected static final float LOAD_FACTOR = 0.75f;
    protected static final int TREEIFY_THRESHOLD = 8;
    protected static final int UNTREEIFY_THRESHOLD = 6;
    protected static final int MIN_TREEIFY_CAPACITY = 64;
    protected static final int MIN_TRANSFER_STRIDE = 16;
    protected static int RESIZE_STAMP_BITS = 16;
    protected static final int MAX_RESIZERS = (1 << 32 - RESIZE_STAMP_BITS) - 1;
    protected static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
    protected static final int MOVED = -1;
    protected static final int TREEBIN = -2;
    protected static final int RESERVED = -3;
    protected static final int HASH_BITS = Integer.MAX_VALUE;
    protected static final int NCPU = Runtime.getRuntime().availableProcessors();
    protected volatile transient Node<V>[] table;
    protected volatile transient Node<V>[] nextTable;
    protected volatile transient long baseCount;
    protected volatile transient int sizeCtl;
    protected volatile transient int transferIndex;
    protected volatile transient int cellsBusy;
    protected volatile transient CounterCell[] counterCells;
    protected transient KeySetView<V> keySet;
    protected transient ValuesView<V> values;
    protected transient EntrySetView<V> entrySet;
    protected final short EMPTY;
    protected static final Unsafe U;
    protected static final long SIZECTL;
    protected static final long TRANSFERINDEX;
    protected static final long BASECOUNT;
    protected static final long CELLSBUSY;
    protected static final long CELLVALUE;
    protected static final long ABASE;
    protected static final int ASHIFT;

    protected static final int spread(int h) {
        return (h ^ h >>> 16) & Integer.MAX_VALUE;
    }

    protected static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        return (n |= n >>> 16) < 0 ? 1 : (n >= 0x40000000 ? 0x40000000 : n + 1);
    }

    protected static final <V> Node<V> tabAt(Node<V>[] tab, int i) {
        return (Node)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }

    protected static final <V> boolean casTabAt(Node<V>[] tab, int i, Node<V> c, Node<V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }

    protected static final <V> void setTabAt(Node<V>[] tab, int i, Node<V> v) {
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }

    public Short2ObjectConcurrentHashMap() {
        this.EMPTY = (short)-1;
    }

    public Short2ObjectConcurrentHashMap(boolean nonce, short emptyValue) {
        this.EMPTY = emptyValue;
    }

    public Short2ObjectConcurrentHashMap(int initialCapacity) {
        this(initialCapacity, true, -1);
    }

    public Short2ObjectConcurrentHashMap(int initialCapacity, boolean nonce, short emptyValue) {
        int cap;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        this.sizeCtl = cap = initialCapacity >= 0x20000000 ? 0x40000000 : Short2ObjectConcurrentHashMap.tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1);
        this.EMPTY = emptyValue;
    }

    public Short2ObjectConcurrentHashMap(Map<? extends Short, ? extends V> m, short emptyValue) {
        this.sizeCtl = 16;
        this.EMPTY = emptyValue;
        this.putAll(m);
    }

    public Short2ObjectConcurrentHashMap(Short2ObjectConcurrentHashMap<? extends V> m) {
        this.sizeCtl = 16;
        this.EMPTY = m.EMPTY;
        this.putAll(m);
    }

    public Short2ObjectConcurrentHashMap(Short2ObjectMap<V> m) {
        this.sizeCtl = 16;
        this.EMPTY = (short)-1;
        this.putAll(m);
    }

    public Short2ObjectConcurrentHashMap(Short2ObjectMap<V> m, short emptyValue) {
        this.sizeCtl = 16;
        this.EMPTY = emptyValue;
        this.putAll(m);
    }

    public Short2ObjectConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1, -1);
    }

    public Short2ObjectConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, short emptyValue) {
        long size;
        int cap;
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (initialCapacity < concurrencyLevel) {
            initialCapacity = concurrencyLevel;
        }
        this.sizeCtl = cap = (size = (long)(1.0 + (double)((float)initialCapacity / loadFactor))) >= 0x40000000L ? 0x40000000 : Short2ObjectConcurrentHashMap.tableSizeFor((int)size);
        this.EMPTY = emptyValue;
    }

    public int size() {
        long n = this.sumCount();
        return n < 0L ? 0 : (n > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)n);
    }

    public boolean isEmpty() {
        return this.sumCount() <= 0L;
    }

    public V get(short key) {
        Node<V> e;
        int n;
        if (key == this.EMPTY) {
            throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
        }
        int h = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
        Node<V>[] tab = this.table;
        if (this.table != null && (n = tab.length) > 0 && (e = Short2ObjectConcurrentHashMap.tabAt(tab, n - 1 & h)) != null) {
            short ek;
            int eh = e.hash;
            if (eh == h) {
                ek = e.key;
                if (ek == key || ek != this.EMPTY && key == ek) {
                    return e.val;
                }
            } else if (eh < 0) {
                Node<V> p = e.find(h, key);
                return p != null ? (V)p.val : null;
            }
            while ((e = e.next) != null) {
                if (e.hash != h || (ek = e.key) != key && (ek == this.EMPTY || key != ek)) continue;
                return e.val;
            }
        }
        return null;
    }

    public boolean containsKey(short key) {
        return this.get(key) != null;
    }

    public boolean containsValue(Object value) {
        block14: {
            Node p;
            Object v;
            if (value == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((v = p.val) != value && (v == null || !value.equals(v)));
            return true;
        }
        return false;
    }

    public V put(short key, V value) {
        return this.putVal(key, value, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final V putVal(short key, V value, boolean onlyIfAbsent) {
        int binCount;
        block20: {
            V oldVal;
            int i;
            if (key == this.EMPTY) {
                throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
            }
            if (value == null) {
                throw new NullPointerException();
            }
            int hash = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
            binCount = 0;
            Node<V>[] tab = this.table;
            while (true) {
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & hash;
                Node<V> f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
                if (f == null) {
                    if (!Short2ObjectConcurrentHashMap.casTabAt(tab, i, null, new Node<V>(this.EMPTY, hash, key, value, null))) continue;
                    break block20;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                oldVal = null;
                Node<V> node = f;
                synchronized (node) {
                    block21: {
                        if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<V> e = f;
                                while (true) {
                                    short ek;
                                    if (e.hash == hash && ((ek = e.key) == key || ek != this.EMPTY && key == ek)) {
                                        oldVal = e.val;
                                        if (!onlyIfAbsent) {
                                            e.val = value;
                                        }
                                        break block21;
                                    }
                                    Node<V> pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        pred.next = new Node<V>(this.EMPTY, hash, key, value, null);
                                        break block21;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                binCount = 2;
                                TreeNode<V> p = ((TreeBin)f).putTreeVal(hash, key, value);
                                if (p != null) {
                                    oldVal = p.val;
                                    if (!onlyIfAbsent) {
                                        p.val = value;
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
            if (oldVal != null) {
                return oldVal;
            }
        }
        this.addCount(1L, binCount);
        return null;
    }

    public void putAll(Map<? extends Short, ? extends V> m) {
        this.tryPresize(m.size());
        for (Map.Entry<Short, V> e : m.entrySet()) {
            this.putVal(e.getKey(), e.getValue(), false);
        }
    }

    public void putAll(Short2ObjectConcurrentHashMap<? extends V> m) {
        this.tryPresize(m.size());
        for (Short2ObjectMap.Entry entry : m.short2ObjectEntrySet()) {
            this.putVal(entry.getShortKey(), entry.getValue(), false);
        }
    }

    public void putAll(Short2ObjectMap<V> m) {
        this.tryPresize(m.size());
        for (Short2ObjectMap.Entry entry : m.short2ObjectEntrySet()) {
            this.putVal(entry.getShortKey(), entry.getValue(), false);
        }
    }

    public V remove(short key) {
        return this.replaceNode(key, null, null);
    }

    @Deprecated
    public V remove(Short key) {
        return this.replaceNode(key, null, null);
    }

    @Deprecated
    public V remove(Object key) {
        return this.remove((Short)key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final V replaceNode(short key, V value, Object cv) {
        int i;
        Node<V> f;
        int n;
        if (key == this.EMPTY) {
            throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
        }
        int hash = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
        Node<V>[] tab = this.table;
        while (tab != null && (n = tab.length) != 0 && (f = Short2ObjectConcurrentHashMap.tabAt(tab, i = n - 1 & hash)) != null) {
            int fh = f.hash;
            if (fh == -1) {
                tab = this.helpTransfer(tab, f);
                continue;
            }
            Object oldVal = null;
            boolean validated = false;
            Node<V> node = f;
            synchronized (node) {
                if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        validated = true;
                        Node<V> e = f;
                        Node<V> pred = null;
                        do {
                            short ek;
                            if (e.hash == hash && ((ek = e.key) == key || ek != this.EMPTY && key == ek)) {
                                Object ev = e.val;
                                if (cv == null || cv == ev || ev != null && cv.equals(ev)) {
                                    oldVal = ev;
                                    if (value != null) {
                                        e.val = value;
                                    } else if (pred != null) {
                                        pred.next = e.next;
                                    } else {
                                        Short2ObjectConcurrentHashMap.setTabAt(tab, i, e.next);
                                    }
                                }
                                break;
                            }
                            pred = e;
                        } while ((e = e.next) != null);
                    } else if (f instanceof TreeBin) {
                        TreeNode p;
                        validated = true;
                        TreeBin t = (TreeBin)f;
                        TreeNode r = t.root;
                        if (r != null && (p = r.findTreeNode(hash, key, null)) != null) {
                            Object pv = p.val;
                            if (cv == null || cv == pv || pv != null && cv.equals(pv)) {
                                oldVal = pv;
                                if (value != null) {
                                    p.val = value;
                                } else if (t.removeTreeNode(p)) {
                                    Short2ObjectConcurrentHashMap.setTabAt(tab, i, this.untreeify(t.first));
                                }
                            }
                        }
                    }
                }
            }
            if (!validated) continue;
            if (oldVal == null) break;
            if (value == null) {
                this.addCount(-1L, -1);
            }
            return (V)oldVal;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        long delta = 0L;
        int i = 0;
        Node<V>[] tab = this.table;
        while (tab != null && i < tab.length) {
            Node<V> f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
            if (f == null) {
                ++i;
                continue;
            }
            int fh = f.hash;
            if (fh == -1) {
                tab = this.helpTransfer(tab, f);
                i = 0;
                continue;
            }
            Node<V> node = f;
            synchronized (node) {
                if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                    Node<V> p;
                    Node<V> node2 = fh >= 0 ? f : (p = f instanceof TreeBin ? ((TreeBin)f).first : null);
                    while (p != null) {
                        --delta;
                        p = p.next;
                    }
                    Short2ObjectConcurrentHashMap.setTabAt(tab, i++, null);
                }
            }
        }
        if (delta != 0L) {
            this.addCount(delta, -1);
        }
    }

    public KeySetView<V> keySet() {
        KeySetView<V> ks = this.keySet;
        return ks != null ? ks : (this.keySet = this.buildKeySetView());
    }

    protected KeySetView<V> buildKeySetView() {
        return new KeySetView<Object>(this, null);
    }

    public FastCollection<V> values() {
        ValuesView<V> vs = this.values;
        return vs != null ? vs : (this.values = this.buildValuesView());
    }

    protected ValuesView<V> buildValuesView() {
        return new ValuesView(this);
    }

    public ObjectSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
        EntrySetView<V> es = this.entrySet;
        return es != null ? es : (this.entrySet = this.buildEntrySetView());
    }

    @Deprecated
    public ObjectSet<Map.Entry<Short, V>> entrySet() {
        return this.short2ObjectEntrySet();
    }

    protected EntrySetView<V> buildEntrySetView() {
        return new EntrySetView(this);
    }

    public int hashCode() {
        int h;
        block13: {
            h = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block13;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block15: {
                        block14: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block14;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block15;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                h += Short.hashCode(p.key) ^ p.val.hashCode();
            }
        }
        return h;
    }

    public String toString() {
        Node<V>[] t = this.table;
        int f = this.table == null ? 0 : t.length;
        Traverser<V> it = new Traverser<V>(t, f, 0, f);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Node<V> p = it.advance();
        if (p != null) {
            while (true) {
                short k = p.key;
                Object v = p.val;
                sb.append(k);
                sb.append('=');
                sb.append((Object)(v == this ? "(this Map)" : v));
                p = it.advance();
                if (p == null) break;
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    public boolean equals(Object o) {
        if (o != this) {
            Node<V> p;
            if (!(o instanceof Short2ObjectConcurrentHashMap)) {
                return false;
            }
            Short2ObjectConcurrentHashMap m = (Short2ObjectConcurrentHashMap)o;
            Node<V>[] t = this.table;
            int f = this.table == null ? 0 : t.length;
            Traverser<V> it = new Traverser<V>(t, f, 0, f);
            while ((p = it.advance()) != null) {
                Object v = p.val;
                V v2 = m.get(p.key);
                if (v2 != null && (v2 == v || v2.equals(v))) continue;
                return false;
            }
            for (Short2ObjectMap.Entry entry : m.short2ObjectEntrySet()) {
                V v;
                Object mv;
                short mk = entry.getShortKey();
                if (mk != m.EMPTY && (mv = entry.getValue()) != null && (v = this.get(mk)) != null && (mv == v || mv.equals(v))) continue;
                return false;
            }
        }
        return true;
    }

    public V putIfAbsent(short key, V value) {
        return this.putVal(key, value, true);
    }

    public boolean remove(short key, Object value) {
        if (key == this.EMPTY) {
            throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
        }
        return value != null && this.replaceNode(key, null, value) != null;
    }

    public boolean replace(short key, V oldValue, V newValue) {
        if (key == this.EMPTY) {
            throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
        }
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        return this.replaceNode(key, newValue, oldValue) != null;
    }

    public V replace(short key, V value) {
        if (key == this.EMPTY) {
            throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
        }
        if (value == null) {
            throw new NullPointerException();
        }
        return this.replaceNode(key, value, null);
    }

    public V getOrDefault(short key, V defaultValue) {
        V v = this.get(key);
        return v == null ? defaultValue : v;
    }

    public int forEach(ShortObjConsumer<? super V> action) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEach(ShortBiObjConsumer<? super V, X> action, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, x);
                ++count;
            }
        }
        return count;
    }

    public <X, Y> int forEach(ShortTriObjConsumer<? super V, X, Y> action, X x, Y y) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, x, y);
                ++count;
            }
        }
        return count;
    }

    public int forEachWithByte(ShortObjByteConsumer<? super V> action, byte ii) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii);
                ++count;
            }
        }
        return count;
    }

    public int forEachWithShort(ShortObjShortConsumer<? super V> action, short ii) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii);
                ++count;
            }
        }
        return count;
    }

    public int forEachWithInt(ShortObjIntConsumer<? super V> action, int ii) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii);
                ++count;
            }
        }
        return count;
    }

    public int forEachWithLong(ShortObjLongConsumer<? super V> action, long ii) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii);
                ++count;
            }
        }
        return count;
    }

    public int forEachWithFloat(ShortObjFloatConsumer<? super V> action, float ii) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii);
                ++count;
            }
        }
        return count;
    }

    public int forEachWithDouble(ShortObjDoubleConsumer<? super V> action, double ii) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEachWithByte(ShortBiObjByteConsumer<? super V, X> action, byte ii, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii, x);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEachWithShort(ShortBiObjShortConsumer<? super V, X> action, short ii, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii, x);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEachWithInt(ShortBiObjIntConsumer<? super V, X> action, int ii, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii, x);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEachWithLong(ShortBiObjLongConsumer<? super V, X> action, long ii, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii, x);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEachWithFloat(ShortBiObjFloatConsumer<? super V, X> action, float ii, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii, x);
                ++count;
            }
        }
        return count;
    }

    public <X> int forEachWithDouble(ShortBiObjDoubleConsumer<? super V, X> action, double ii, X x) {
        int count;
        block14: {
            if (action == null) {
                throw new NullPointerException();
            }
            count = 0;
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                action.accept(p.key, p.val, ii, x);
                ++count;
            }
        }
        return count;
    }

    public void replaceAll(Short2ObjectOperator<V> function) {
        block15: {
            if (function == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block15;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            while (true) {
                V newValue;
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block17: {
                        block16: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block16;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block17;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                Object oldValue = p.val;
                short key = p.key;
                do {
                    if ((newValue = function.apply(key, oldValue)) != null) continue;
                    throw new NullPointerException();
                } while (this.replaceNode(key, newValue, oldValue) == null && (oldValue = this.get(key)) != null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V computeIfAbsent(short key, ShortFunction<? extends V> mappingFunction) {
        int binCount;
        Object val;
        block31: {
            boolean added;
            int i;
            if (key == this.EMPTY) {
                throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
            }
            if (mappingFunction == null) {
                throw new NullPointerException();
            }
            int h = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
            val = null;
            binCount = 0;
            Node<V>[] tab = this.table;
            while (true) {
                Node node;
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & h;
                Node<V> f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
                if (f == null) {
                    ReservationNode r;
                    node = r = new ReservationNode(this.EMPTY);
                    synchronized (node) {
                        if (Short2ObjectConcurrentHashMap.casTabAt(tab, i, null, r)) {
                            binCount = 1;
                            Node<Object> node2 = null;
                            try {
                                V v = mappingFunction.apply(key);
                                val = v;
                                if (v != null) {
                                    node2 = new Node<Object>(this.EMPTY, h, key, val, null);
                                }
                            }
                            finally {
                                Short2ObjectConcurrentHashMap.setTabAt(tab, i, node2);
                            }
                        }
                    }
                    if (binCount == 0) continue;
                    break block31;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                added = false;
                node = f;
                synchronized (node) {
                    block32: {
                        if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<V> e = f;
                                while (true) {
                                    short ek;
                                    if (e.hash == h && ((ek = e.key) == key || ek != this.EMPTY && key == ek)) {
                                        val = e.val;
                                        break block32;
                                    }
                                    Node<V> pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        V v = mappingFunction.apply(key);
                                        val = v;
                                        if (v != null) {
                                            added = true;
                                            pred.next = new Node<Object>(this.EMPTY, h, key, val, null);
                                        }
                                        break block32;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                TreeNode p;
                                binCount = 2;
                                TreeBin t = (TreeBin)f;
                                TreeNode r = t.root;
                                if (r != null && (p = r.findTreeNode(h, key, null)) != null) {
                                    val = p.val;
                                } else {
                                    V v = mappingFunction.apply(key);
                                    val = v;
                                    if (v != null) {
                                        added = true;
                                        t.putTreeVal(h, key, val);
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
            if (!added) {
                return (V)val;
            }
        }
        if (val != null) {
            this.addCount(1L, binCount);
        }
        return (V)val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V computeIfPresent(short key, ShortObjFunction<? super V, ? extends V> remappingFunction) {
        if (key == this.EMPTY) {
            throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
        }
        if (remappingFunction == null) {
            throw new NullPointerException();
        }
        int h = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
        V val = null;
        int delta = 0;
        int binCount = 0;
        Node<V>[] tab = this.table;
        while (true) {
            int n;
            if (tab == null || (n = tab.length) == 0) {
                tab = this.initTable();
                continue;
            }
            int i = n - 1 & h;
            Node<V> f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
            if (f == null) break;
            int fh = f.hash;
            if (fh == -1) {
                tab = this.helpTransfer(tab, f);
                continue;
            }
            Node<V> node = f;
            synchronized (node) {
                if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        binCount = 1;
                        Node<V> e = f;
                        Node<V> pred = null;
                        while (true) {
                            short ek;
                            if (e.hash == h && ((ek = e.key) == key || ek != this.EMPTY && key == ek)) {
                                val = remappingFunction.apply(key, e.val);
                                if (val != null) {
                                    e.val = val;
                                } else {
                                    delta = -1;
                                    Node en = e.next;
                                    if (pred != null) {
                                        pred.next = en;
                                    } else {
                                        Short2ObjectConcurrentHashMap.setTabAt(tab, i, en);
                                    }
                                }
                            } else {
                                pred = e;
                                e = e.next;
                                if (e != null) {
                                    ++binCount;
                                    continue;
                                }
                            }
                            break;
                        }
                    } else if (f instanceof TreeBin) {
                        TreeNode p;
                        binCount = 2;
                        TreeBin t = (TreeBin)f;
                        TreeNode r = t.root;
                        if (r != null && (p = r.findTreeNode(h, key, null)) != null) {
                            val = remappingFunction.apply(key, p.val);
                            if (val != null) {
                                p.val = val;
                            } else {
                                delta = -1;
                                if (t.removeTreeNode(p)) {
                                    Short2ObjectConcurrentHashMap.setTabAt(tab, i, this.untreeify(t.first));
                                }
                            }
                        }
                    }
                }
            }
            if (binCount != 0) break;
        }
        if (delta != 0) {
            this.addCount(delta, binCount);
        }
        return val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V compute(short key, ShortObjFunction<? super V, ? extends V> remappingFunction) {
        int binCount;
        int delta;
        Object val;
        block37: {
            int i;
            if (key == this.EMPTY) {
                throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
            }
            if (remappingFunction == null) {
                throw new NullPointerException();
            }
            int h = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
            val = null;
            delta = 0;
            binCount = 0;
            Node<V>[] tab = this.table;
            while (true) {
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & h;
                Node<V> f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
                if (f == null) {
                    ReservationNode r;
                    ReservationNode reservationNode = r = new ReservationNode(this.EMPTY);
                    synchronized (reservationNode) {
                        if (Short2ObjectConcurrentHashMap.casTabAt(tab, i, null, r)) {
                            binCount = 1;
                            Node<Object> node = null;
                            try {
                                V v = remappingFunction.apply(key, null);
                                val = v;
                                if (v != null) {
                                    delta = 1;
                                    node = new Node<Object>(this.EMPTY, h, key, val, null);
                                }
                            }
                            finally {
                                Short2ObjectConcurrentHashMap.setTabAt(tab, i, node);
                            }
                        }
                    }
                    if (binCount == 0) continue;
                    break block37;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                Node<V> node = f;
                synchronized (node) {
                    block38: {
                        if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<V> e = f;
                                Node<V> pred = null;
                                while (true) {
                                    short ek;
                                    if (e.hash == h && ((ek = e.key) == key || ek != this.EMPTY && key == ek)) {
                                        val = remappingFunction.apply(key, e.val);
                                        if (val != null) {
                                            e.val = val;
                                        } else {
                                            delta = -1;
                                            Node en = e.next;
                                            if (pred != null) {
                                                pred.next = en;
                                            } else {
                                                Short2ObjectConcurrentHashMap.setTabAt(tab, i, en);
                                            }
                                        }
                                        break block38;
                                    }
                                    pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        val = remappingFunction.apply(key, null);
                                        if (val != null) {
                                            delta = 1;
                                            pred.next = new Node<Object>(this.EMPTY, h, key, val, null);
                                        }
                                        break block38;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                binCount = 1;
                                TreeBin t = (TreeBin)f;
                                TreeNode r = t.root;
                                TreeNode p = r != null ? r.findTreeNode(h, key, null) : null;
                                Object pv = p == null ? null : p.val;
                                val = remappingFunction.apply(key, pv);
                                if (val != null) {
                                    if (p != null) {
                                        p.val = val;
                                    } else {
                                        delta = 1;
                                        t.putTreeVal(h, key, val);
                                    }
                                } else if (p != null) {
                                    delta = -1;
                                    if (t.removeTreeNode(p)) {
                                        Short2ObjectConcurrentHashMap.setTabAt(tab, i, this.untreeify(t.first));
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
        }
        if (delta != 0) {
            this.addCount(delta, binCount);
        }
        return val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V merge(short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        int binCount;
        int delta;
        Object val;
        block27: {
            int i;
            if (key == this.EMPTY) {
                throw new IllegalArgumentException("Key is EMPTY: " + this.EMPTY);
            }
            if (value == null || remappingFunction == null) {
                throw new NullPointerException();
            }
            int h = Short2ObjectConcurrentHashMap.spread(Short.hashCode(key));
            val = null;
            delta = 0;
            binCount = 0;
            Node<V>[] tab = this.table;
            while (true) {
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & h;
                Node<V> f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
                if (f == null) {
                    if (!Short2ObjectConcurrentHashMap.casTabAt(tab, i, null, new Node<V>(this.EMPTY, h, key, value, null))) continue;
                    delta = 1;
                    val = value;
                    break block27;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                Node<V> node = f;
                synchronized (node) {
                    block28: {
                        if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<V> e = f;
                                Node<V> pred = null;
                                while (true) {
                                    short ek;
                                    if (e.hash == h && ((ek = e.key) == key || ek != this.EMPTY && key == ek)) {
                                        val = remappingFunction.apply(e.val, value);
                                        if (val != null) {
                                            e.val = val;
                                        } else {
                                            delta = -1;
                                            Node en = e.next;
                                            if (pred != null) {
                                                pred.next = en;
                                            } else {
                                                Short2ObjectConcurrentHashMap.setTabAt(tab, i, en);
                                            }
                                        }
                                        break block28;
                                    }
                                    pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        delta = 1;
                                        val = value;
                                        pred.next = new Node<Object>(this.EMPTY, h, key, val, null);
                                        break block28;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                binCount = 2;
                                TreeBin t = (TreeBin)f;
                                TreeNode r = t.root;
                                TreeNode p = r == null ? null : r.findTreeNode(h, key, null);
                                val = p == null ? value : remappingFunction.apply(p.val, value);
                                if (val != null) {
                                    if (p != null) {
                                        p.val = val;
                                    } else {
                                        delta = 1;
                                        t.putTreeVal(h, key, val);
                                    }
                                } else if (p != null) {
                                    delta = -1;
                                    if (t.removeTreeNode(p)) {
                                        Short2ObjectConcurrentHashMap.setTabAt(tab, i, this.untreeify(t.first));
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
        }
        if (delta != 0) {
            this.addCount(delta, binCount);
        }
        return val;
    }

    public long mappingCount() {
        long n = this.sumCount();
        return n < 0L ? 0L : n;
    }

    public static ShortSet newKeySet() {
        return new KeySetView<Boolean>(new Short2ObjectConcurrentHashMap(), Boolean.TRUE);
    }

    public static KeySetView<Boolean> newKeySet(int initialCapacity) {
        return new KeySetView<Boolean>(new Short2ObjectConcurrentHashMap(initialCapacity), Boolean.TRUE);
    }

    public KeySetView<V> keySet(V mappedValue) {
        if (mappedValue == null) {
            throw new NullPointerException();
        }
        return new KeySetView<V>(this, mappedValue);
    }

    protected static final int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | 1 << RESIZE_STAMP_BITS - 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Node<V>[] initTable() {
        Node<V>[] tab;
        block6: {
            int sc;
            while (true) {
                tab = this.table;
                if (this.table != null && tab.length != 0) break block6;
                sc = this.sizeCtl;
                if (sc < 0) {
                    Thread.yield();
                    continue;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) break;
            }
            try {
                tab = this.table;
                if (this.table == null || tab.length == 0) {
                    int n = sc > 0 ? sc : 16;
                    Node[] nt = new Node[n];
                    tab = nt;
                    this.table = nt;
                    sc = n - (n >>> 2);
                }
            }
            finally {
                this.sizeCtl = sc;
            }
        }
        return tab;
    }

    protected final void addCount(long x, int check) {
        long s;
        long b;
        CounterCell[] as = this.counterCells;
        if (this.counterCells != null || !U.compareAndSwapLong(this, BASECOUNT, b = this.baseCount, s = b + x)) {
            long v;
            CounterCell a;
            int m;
            boolean uncontended = true;
            if (as == null || (m = as.length - 1) < 0 || (a = as[TLRUtil.getProbe() & m]) == null || !(uncontended = U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                this.fullAddCount(x, uncontended);
                return;
            }
            if (check <= 1) {
                return;
            }
            s = this.sumCount();
        }
        if (check >= 0) {
            int sc;
            while (s >= (long)(sc = this.sizeCtl)) {
                int n;
                Node<V>[] tab = this.table;
                if (this.table == null || (n = tab.length) >= 0x40000000) break;
                int rs = Short2ObjectConcurrentHashMap.resizeStamp(n);
                if (sc < 0) {
                    if (sc >>> RESIZE_STAMP_SHIFT != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS) break;
                    Node<V>[] nt = this.nextTable;
                    if (this.nextTable == null || this.transferIndex <= 0) break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                        this.transfer(tab, nt);
                    }
                } else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2)) {
                    this.transfer(tab, null);
                }
                s = this.sumCount();
            }
        }
    }

    protected final Node<V>[] helpTransfer(Node<V>[] tab, Node<V> f) {
        if (tab != null && f instanceof ForwardingNode) {
            Node<V>[] nextTab = ((ForwardingNode)f).nextTable;
            if (((ForwardingNode)f).nextTable != null) {
                int sc;
                int rs = Short2ObjectConcurrentHashMap.resizeStamp(tab.length);
                while (nextTab == this.nextTable && this.table == tab && (sc = this.sizeCtl) < 0 && sc >>> RESIZE_STAMP_SHIFT == rs && sc != rs + 1 && sc != rs + MAX_RESIZERS && this.transferIndex > 0) {
                    if (!U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) continue;
                    this.transfer(tab, nextTab);
                    break;
                }
                return nextTab;
            }
        }
        return this.table;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void tryPresize(int size) {
        int sc;
        int c;
        int n = c = size >= 0x20000000 ? 0x40000000 : Short2ObjectConcurrentHashMap.tableSizeFor(size + (size >>> 1) + 1);
        while ((sc = this.sizeCtl) >= 0) {
            int n2;
            Node<V>[] tab = this.table;
            if (tab == null || (n2 = tab.length) == 0) {
                int n3 = n2 = sc > c ? sc : c;
                if (!U.compareAndSwapInt(this, SIZECTL, sc, -1)) continue;
                try {
                    if (this.table != tab) continue;
                    Node[] nt = new Node[n2];
                    this.table = nt;
                    sc = n2 - (n2 >>> 2);
                    continue;
                }
                finally {
                    this.sizeCtl = sc;
                    continue;
                }
            }
            if (c <= sc || n2 >= 0x40000000) break;
            if (tab != this.table) continue;
            int rs = Short2ObjectConcurrentHashMap.resizeStamp(n2);
            if (sc < 0) {
                if (sc >>> RESIZE_STAMP_SHIFT != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS) break;
                Node<V>[] nt = this.nextTable;
                if (this.nextTable == null || this.transferIndex <= 0) break;
                if (!U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) continue;
                this.transfer(tab, nt);
                continue;
            }
            if (!U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2)) continue;
            this.transfer(tab, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void transfer(Node<V>[] tab, Node<V>[] nextTab) {
        int n = tab.length;
        int stride = NCPU > 1 ? (n >>> 3) / NCPU : n;
        if (stride < 16) {
            stride = 16;
        }
        if (nextTab == null) {
            try {
                Node[] nt = new Node[n << 1];
                nextTab = nt;
            }
            catch (Throwable ex) {
                this.sizeCtl = Integer.MAX_VALUE;
                return;
            }
            this.nextTable = nextTab;
            this.transferIndex = n;
        }
        int nextn = nextTab.length;
        ForwardingNode<V> fwd = new ForwardingNode<V>(this.EMPTY, nextTab);
        boolean advance = true;
        boolean finishing = false;
        int i = 0;
        int bound = 0;
        while (true) {
            if (advance) {
                if (--i >= bound || finishing) {
                    advance = false;
                    continue;
                }
                int nextIndex = this.transferIndex;
                if (nextIndex <= 0) {
                    i = -1;
                    advance = false;
                    continue;
                }
                int nextBound = nextIndex > stride ? nextIndex - stride : 0;
                if (!U.compareAndSwapInt(this, TRANSFERINDEX, nextIndex, nextBound)) continue;
                bound = nextBound;
                i = nextIndex - 1;
                advance = false;
                continue;
            }
            if (i < 0 || i >= n || i + n >= nextn) {
                if (finishing) {
                    this.nextTable = null;
                    this.table = nextTab;
                    this.sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                int sc = this.sizeCtl;
                if (!U.compareAndSwapInt(this, SIZECTL, sc, sc - 1)) continue;
                if (sc - 2 != Short2ObjectConcurrentHashMap.resizeStamp(n) << RESIZE_STAMP_SHIFT) {
                    return;
                }
                advance = true;
                finishing = true;
                i = n;
                continue;
            }
            TreeBin f = Short2ObjectConcurrentHashMap.tabAt(tab, i);
            if (f == null) {
                advance = Short2ObjectConcurrentHashMap.casTabAt(tab, i, null, fwd);
                continue;
            }
            int fh = f.hash;
            if (fh == -1) {
                advance = true;
                continue;
            }
            TreeBin treeBin = f;
            synchronized (treeBin) {
                if (Short2ObjectConcurrentHashMap.tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        Node ln;
                        int runBit = fh & n;
                        TreeBin lastRun = f;
                        Node p = f.next;
                        while (p != null) {
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                            p = p.next;
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        } else {
                            hn = lastRun;
                            ln = null;
                        }
                        p = f;
                        while (p != lastRun) {
                            int ph = p.hash;
                            short pk = p.key;
                            Object pv = p.val;
                            if ((ph & n) == 0) {
                                ln = new Node(this.EMPTY, ph, pk, pv, ln);
                            } else {
                                hn = new Node(this.EMPTY, ph, pk, pv, hn);
                            }
                            p = p.next;
                        }
                        Short2ObjectConcurrentHashMap.setTabAt(nextTab, i, ln);
                        Short2ObjectConcurrentHashMap.setTabAt(nextTab, i + n, hn);
                        Short2ObjectConcurrentHashMap.setTabAt(tab, i, fwd);
                        advance = true;
                    } else if (f instanceof TreeBin) {
                        TreeBin ln;
                        TreeBin t = f;
                        TreeNode lo = null;
                        TreeNode loTail = null;
                        TreeNode hi = null;
                        TreeNode hiTail = null;
                        int lc = 0;
                        int hc = 0;
                        Node e = t.first;
                        while (e != null) {
                            int h = e.hash;
                            TreeNode p = new TreeNode(this.EMPTY, h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                p.prev = loTail;
                                if (p.prev == null) {
                                    lo = p;
                                } else {
                                    loTail.next = p;
                                }
                                loTail = p;
                                ++lc;
                            } else {
                                p.prev = hiTail;
                                if (p.prev == null) {
                                    hi = p;
                                } else {
                                    hiTail.next = p;
                                }
                                hiTail = p;
                                ++hc;
                            }
                            e = e.next;
                        }
                        TreeBin treeBin2 = lc <= 6 ? this.untreeify(lo) : (ln = hc != 0 ? new TreeBin(this.EMPTY, lo) : t);
                        hn = hc <= 6 ? this.untreeify(hi) : (lc != 0 ? new TreeBin(this.EMPTY, hi) : t);
                        Short2ObjectConcurrentHashMap.setTabAt(nextTab, i, ln);
                        Short2ObjectConcurrentHashMap.setTabAt(nextTab, i + n, hn);
                        Short2ObjectConcurrentHashMap.setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }

    protected final long sumCount() {
        CounterCell[] as = this.counterCells;
        long sum = this.baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                CounterCell a = as[i];
                if (a == null) continue;
                sum += a.value;
            }
        }
        return sum;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected final void fullAddCount(long x, boolean wasUncontended) {
        int h = TLRUtil.getProbe();
        if (h == 0) {
            TLRUtil.localInit();
            h = TLRUtil.getProbe();
            wasUncontended = true;
        }
        boolean collide = false;
        while (true) {
            long v;
            int n;
            CounterCell[] as = this.counterCells;
            if (this.counterCells != null && (n = as.length) > 0) {
                CounterCell a = as[n - 1 & h];
                if (a == null) {
                    if (this.cellsBusy == 0) {
                        CounterCell r = new CounterCell(x);
                        if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try {
                                int j;
                                int m;
                                CounterCell[] rs = this.counterCells;
                                if (this.counterCells != null && (m = rs.length) > 0 && rs[j = m - 1 & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            }
                            finally {
                                this.cellsBusy = 0;
                            }
                            if (!created) continue;
                            return;
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else {
                    v = a.value;
                    if (U.compareAndSwapLong(a, CELLVALUE, v, v + x)) return;
                    if (this.counterCells != as || n >= NCPU) {
                        collide = false;
                    } else if (!collide) {
                        collide = true;
                    } else if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                        try {
                            if (this.counterCells == as) {
                                CounterCell[] rs = new CounterCell[n << 1];
                                for (int i = 0; i < n; ++i) {
                                    rs[i] = as[i];
                                }
                                this.counterCells = rs;
                            }
                        }
                        finally {
                            this.cellsBusy = 0;
                        }
                        collide = false;
                        continue;
                    }
                }
                h = TLRUtil.advanceProbe(h);
                continue;
            }
            if (this.cellsBusy == 0 && this.counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try {
                    if (this.counterCells == as) {
                        CounterCell[] rs = new CounterCell[2];
                        rs[h & 1] = new CounterCell(x);
                        this.counterCells = rs;
                        init = true;
                    }
                }
                finally {
                    this.cellsBusy = 0;
                }
                if (!init) continue;
                return;
            }
            v = this.baseCount;
            if (U.compareAndSwapLong(this, BASECOUNT, v, v + x)) return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void treeifyBin(Node<V>[] tab, int index) {
        if (tab != null) {
            int n = tab.length;
            if (n < 64) {
                this.tryPresize(n << 1);
            } else {
                Node<V> b = Short2ObjectConcurrentHashMap.tabAt(tab, index);
                if (b != null && b.hash >= 0) {
                    Node<V> node = b;
                    synchronized (node) {
                        if (Short2ObjectConcurrentHashMap.tabAt(tab, index) == b) {
                            TreeNode hd = null;
                            TreeNode tl = null;
                            Node<V> e = b;
                            while (e != null) {
                                TreeNode p = new TreeNode(this.EMPTY, e.hash, e.key, e.val, null, null);
                                p.prev = tl;
                                if (p.prev == null) {
                                    hd = p;
                                } else {
                                    tl.next = p;
                                }
                                tl = p;
                                e = e.next;
                            }
                            Short2ObjectConcurrentHashMap.setTabAt(tab, index, new TreeBin(this.EMPTY, hd));
                        }
                    }
                }
            }
        }
    }

    protected <V> Node<V> untreeify(Node<V> b) {
        Node hd = null;
        Node tl = null;
        Node<V> q = b;
        while (q != null) {
            Node p = new Node(this.EMPTY, q.hash, q.key, q.val, null);
            if (tl == null) {
                hd = p;
            } else {
                tl.next = p;
            }
            tl = p;
            q = q.next;
        }
        return hd;
    }

    protected final int batchFor(long b) {
        long n;
        if (b == Long.MAX_VALUE || (n = this.sumCount()) <= 1L || n < b) {
            return 0;
        }
        int sp = ForkJoinPool.getCommonPoolParallelism() << 2;
        return b <= 0L || (n /= b) >= (long)sp ? sp : (int)n;
    }

    public void forEach(long parallelismThreshold, ShortObjConsumer<? super V> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachMappingTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEach(long parallelismThreshold, ShortObjFunction<? super V, ? extends U> transformer, Consumer<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedMappingTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U search(long parallelismThreshold, ShortObjFunction<? super V, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchMappingsTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public <U> U search(ShortObjFunction<? super V, ? extends U> searchFunction) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val)) == null);
            return u;
        }
        return null;
    }

    public <U, X> U search(ShortBiObjFunction<? super V, X, ? extends U> searchFunction, X x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U searchWithByte(ShortObjByteFunction<? super V, ? extends U> searchFunction, byte x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U searchWithShort(ShortObjShortFunction<? super V, ? extends U> searchFunction, short x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U searchWithInt(ShortObjIntFunction<? super V, ? extends U> searchFunction, int x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U searchWithLong(ShortObjLongFunction<? super V, ? extends U> searchFunction, long x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U searchWithFloat(ShortObjFloatFunction<? super V, ? extends U> searchFunction, float x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U searchWithDouble(ShortObjDoubleFunction<? super V, ? extends U> searchFunction, double x) {
        block14: {
            Node p;
            U u;
            if (searchFunction == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            do {
                p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break block14;
            } while ((u = searchFunction.apply(p.key, p.val, x)) == null);
            return u;
        }
        return null;
    }

    public <U> U reduce(long parallelismThreshold, ShortObjFunction<? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceMappingsTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public <U> U reduce(ShortObjFunction<? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
        block14: {
            if (transformer == null || reducer == null) {
                throw new NullPointerException();
            }
            Node<V>[] tt = this.table;
            if (this.table == null) break block14;
            Node<V>[] tab = tt;
            Node next = null;
            TableStack stack = null;
            TableStack spare = null;
            int index = 0;
            int baseIndex = 0;
            int baseLimit = tt.length;
            int baseSize = tt.length;
            Object r = null;
            while (true) {
                Node p = null;
                p = next;
                if (p != null) {
                    p = p.next;
                }
                while (true) {
                    TableStack s;
                    int i;
                    int n;
                    Node<V>[] t;
                    block16: {
                        block15: {
                            if (p != null) {
                                next = p;
                                break;
                            }
                            if (baseIndex >= baseLimit) break block15;
                            t = tab;
                            if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                        }
                        next = null;
                        break;
                    }
                    p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                    if (p != null && p.hash < 0) {
                        if (p instanceof ForwardingNode) {
                            tab = ((ForwardingNode)p).nextTable;
                            p = null;
                            s = spare;
                            if (s != null) {
                                spare = s.next;
                            } else {
                                s = new TableStack();
                            }
                            s.tab = t;
                            s.length = n;
                            s.index = i;
                            s.next = stack;
                            stack = s;
                            continue;
                        }
                        p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                    }
                    if (stack != null) {
                        int len;
                        while ((s = stack) != null && (index += (len = s.length)) >= n) {
                            n = len;
                            index = s.index;
                            tab = s.tab;
                            s.tab = null;
                            TableStack anext = s.next;
                            s.next = spare;
                            stack = anext;
                            spare = s;
                        }
                        if (s != null || (index += baseSize) < n) continue;
                        index = ++baseIndex;
                        continue;
                    }
                    index = i + baseSize;
                    if (index < n) continue;
                    index = ++baseIndex;
                }
                if (p == null) break;
                U u = transformer.apply(p.key, p.val);
                if (u == null) continue;
                r = r == null ? u : reducer.apply(r, u);
            }
            return (U)r;
        }
        return null;
    }

    public double reduceToDouble(long parallelismThreshold, ToDoubleShortObjFunction<? super V> transformer, double basis, DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceMappingsToDoubleTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public long reduceToLong(long parallelismThreshold, ToLongShortObjFunction<? super V> transformer, long basis, LongBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceMappingsToLongTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public int reduceToInt(long parallelismThreshold, ToIntShortObjFunction<? super V> transformer, int basis, IntBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceMappingsToIntTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public void forEachKey(long parallelismThreshold, ShortConsumer action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachKeyTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEachKey(long parallelismThreshold, ShortFunction<? extends U> transformer, Consumer<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedKeyTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U searchKeys(long parallelismThreshold, ShortFunction<? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchKeysTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public short reduceKeys(long parallelismThreshold, ShortReduceTaskOperator reducer) {
        if (reducer == null) {
            throw new NullPointerException();
        }
        return new ReduceKeysTask<V>(this.EMPTY, null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, reducer).invoke0();
    }

    public <U> U reduceKeys(long parallelismThreshold, ShortFunction<? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceKeysTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceKeysToDouble(long parallelismThreshold, ShortToDoubleFunction transformer, double basis, DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceKeysToDoubleTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public long reduceKeysToLong(long parallelismThreshold, ShortToLongFunction transformer, long basis, LongBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceKeysToLongTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public int reduceKeysToInt(long parallelismThreshold, ShortToIntFunction transformer, int basis, IntBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceKeysToIntTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public void forEachValue(long parallelismThreshold, Consumer<? super V> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachValueTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEachValue(long parallelismThreshold, Function<? super V, ? extends U> transformer, Consumer<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedValueTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U searchValues(long parallelismThreshold, Function<? super V, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchValuesTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public V reduceValues(long parallelismThreshold, BiFunction<? super V, ? super V, ? extends V> reducer) {
        if (reducer == null) {
            throw new NullPointerException();
        }
        return new ReduceValuesTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, reducer).invoke();
    }

    public <U> U reduceValues(long parallelismThreshold, Function<? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceValuesTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceValuesToDouble(long parallelismThreshold, ToDoubleFunction<? super V> transformer, double basis, DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceValuesToDoubleTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public long reduceValuesToLong(long parallelismThreshold, ToLongFunction<? super V> transformer, long basis, LongBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceValuesToLongTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public int reduceValuesToInt(long parallelismThreshold, ToIntFunction<? super V> transformer, int basis, IntBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceValuesToIntTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public void forEachEntry(long parallelismThreshold, Consumer<? super Entry<V>> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachEntryTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEachEntry(long parallelismThreshold, Function<Entry<V>, ? extends U> transformer, Consumer<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedEntryTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U searchEntries(long parallelismThreshold, Function<Entry<V>, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchEntriesTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public Entry<V> reduceEntries(long parallelismThreshold, BiFunction<Entry<V>, Entry<V>, ? extends Entry<V>> reducer) {
        if (reducer == null) {
            throw new NullPointerException();
        }
        return (Entry)new ReduceEntriesTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, reducer).invoke();
    }

    public <U> U reduceEntries(long parallelismThreshold, Function<Entry<V>, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceEntriesTask<V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceEntriesToDouble(long parallelismThreshold, ToDoubleFunction<Entry<V>> transformer, double basis, DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceEntriesToDoubleTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public long reduceEntriesToLong(long parallelismThreshold, ToLongFunction<Entry<V>> transformer, long basis, LongBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceEntriesToLongTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public int reduceEntriesToInt(long parallelismThreshold, ToIntFunction<Entry<V>> transformer, int basis, IntBinaryOperator reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return new MapReduceEntriesToIntTask<V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke0();
    }

    public V valueMatching(Predicate<V> predicate) {
        int f;
        Node next = null;
        TableStack stack = null;
        TableStack spare = null;
        int index = 0;
        int baseIndex = 0;
        Node<V>[] tab = this.table;
        int baseLimit = f = this.table == null ? 0 : tab.length;
        int baseSize = f;
        boolean b = false;
        block0: while (next != null || !b) {
            b |= true;
            Node e = next;
            if (e != null) {
                e = e.next;
            }
            while (true) {
                TableStack s;
                int i;
                int n;
                Node<V>[] t;
                block14: {
                    block13: {
                        if (e != null) {
                            Node node = next = e;
                            if (!predicate.test(node.val)) continue block0;
                            return e.val;
                        }
                        if (baseIndex >= baseLimit) break block13;
                        t = tab;
                        if (tab != null && (n = t.length) > (i = index) && i >= 0) break block14;
                    }
                    next = null;
                    continue block0;
                }
                e = Short2ObjectConcurrentHashMap.tabAt(t, i);
                if (e != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode)e).nextTable;
                        e = null;
                        s = spare;
                        if (s != null) {
                            spare = s.next;
                        } else {
                            s = new TableStack();
                        }
                        s.tab = t;
                        s.length = n;
                        s.index = i;
                        s.next = stack;
                        stack = s;
                        continue;
                    }
                    e = e instanceof TreeBin ? ((TreeBin)e).first : null;
                }
                if (stack != null) {
                    int len;
                    while ((s = stack) != null && (index += (len = s.length)) >= n) {
                        n = len;
                        index = s.index;
                        tab = s.tab;
                        s.tab = null;
                        TableStack next1 = s.next;
                        s.next = spare;
                        stack = next1;
                        spare = s;
                    }
                    if (s != null || (index += baseSize) < n) continue;
                    index = ++baseIndex;
                    continue;
                }
                index = i + baseSize;
                if (index < n) continue;
                index = ++baseIndex;
            }
        }
        return null;
    }

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            U = (Unsafe)f.get(null);
            Class<Short2ObjectConcurrentHashMap> k = Short2ObjectConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
            Class<CounterCell> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
            Class<Node[]> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & scale - 1) != 0) {
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    protected static class Node<V>
    implements Entry<V> {
        public final short EMPTY;
        public final int hash;
        public final short key;
        public volatile V val;
        public volatile Node<V> next;

        public Node(short empty, int hash, short key, V val, Node<V> next) {
            this.EMPTY = empty;
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        @Override
        public final boolean isEmpty() {
            return this.key == this.EMPTY;
        }

        @Override
        public final Short getKey() {
            return this.key;
        }

        @Override
        public final short getShortKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.val;
        }

        @Override
        public final int hashCode() {
            return Short.hashCode(this.key) ^ this.val.hashCode();
        }

        @Override
        public final String toString() {
            if (this.isEmpty()) {
                return "EMPTY=" + String.valueOf(this.val);
            }
            return this.key + "=" + String.valueOf(this.val);
        }

        @Override
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean equals(Object o) {
            boolean empty = this.isEmpty();
            if (o instanceof Entry) {
                if (empty != ((Entry)o).isEmpty()) {
                    return false;
                }
                if (!empty && this.key != ((Entry)o).getShortKey()) {
                    return false;
                }
                return this.val.equals(((Entry)o).getValue());
            }
            return false;
        }

        protected Node<V> find(int h, short k) {
            Node<V> e = this;
            if (k != this.EMPTY) {
                do {
                    short ek;
                    if (e.hash != h || (ek = e.key) != k && (ek == this.EMPTY || k != ek)) continue;
                    return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    protected static final class ForwardingNode<V>
    extends Node<V> {
        public final Node<V>[] nextTable;

        public ForwardingNode(short empty, Node<V>[] tab) {
            super(empty, -1, empty, null, null);
            this.nextTable = tab;
        }

        @Override
        protected Node<V> find(int h, short k) {
            Node<V>[] tab = this.nextTable;
            block0: while (true) {
                Node<V> e;
                int n;
                if (k == this.EMPTY || tab == null || (n = tab.length) == 0 || (e = Short2ObjectConcurrentHashMap.tabAt(tab, n - 1 & h)) == null) {
                    return null;
                }
                do {
                    short ek;
                    int eh;
                    if ((eh = e.hash) == h && ((ek = e.key) == k || ek != this.EMPTY && k == ek)) {
                        return e;
                    }
                    if (eh >= 0) continue;
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode)e).nextTable;
                        continue block0;
                    }
                    return e.find(h, k);
                } while ((e = e.next) != null);
                break;
            }
            return null;
        }
    }

    protected static final class TableStack<V> {
        public int length;
        public int index;
        public Node<V>[] tab;
        public TableStack<V> next;
    }

    protected static final class TreeBin<V>
    extends Node<V> {
        public TreeNode<V> root;
        public volatile TreeNode<V> first;
        public volatile Thread waiter;
        public volatile int lockState;
        public static final int WRITER = 1;
        public static final int WAITER = 2;
        public static final int READER = 4;
        protected static final Unsafe U;
        protected static final long LOCKSTATE;

        protected int tieBreakOrder(short a, short b) {
            int comp = Short.compare(a, b);
            return comp > 0 ? 1 : -1;
        }

        public TreeBin(short empty, TreeNode<V> b) {
            super(empty, -2, empty, null, null);
            this.first = b;
            TreeNode r = null;
            TreeNode x = b;
            while (x != null) {
                TreeNode next = (TreeNode)x.next;
                x.right = null;
                x.left = null;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                } else {
                    TreeNode xp;
                    int dir;
                    short k = x.key;
                    int h = x.hash;
                    Object kc = null;
                    TreeNode p = r;
                    do {
                        short pk = p.key;
                        int ph = p.hash;
                        if (ph > h) {
                            dir = -1;
                        } else if (ph < h) {
                            dir = 1;
                        } else {
                            dir = Short.compare(k, pk);
                            if (dir == 0) {
                                dir = this.tieBreakOrder(k, pk);
                            }
                        }
                        xp = p;
                    } while ((p = dir <= 0 ? p.left : p.right) != null);
                    x.parent = xp;
                    if (dir <= 0) {
                        xp.left = x;
                    } else {
                        xp.right = x;
                    }
                    r = this.balanceInsertion(r, x);
                }
                x = next;
            }
            this.root = r;
            assert (this.checkInvariants(this.root));
        }

        protected final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, 1)) {
                this.contendedLock();
            }
        }

        protected final void unlockRoot() {
            this.lockState = 0;
        }

        protected final void contendedLock() {
            boolean waiting = false;
            while (true) {
                int s;
                if (((s = this.lockState) & 0xFFFFFFFD) == 0) {
                    if (!U.compareAndSwapInt(this, LOCKSTATE, s, 1)) continue;
                    if (waiting) {
                        this.waiter = null;
                    }
                    return;
                }
                if ((s & 2) == 0) {
                    if (!U.compareAndSwapInt(this, LOCKSTATE, s, s | 2)) continue;
                    waiting = true;
                    this.waiter = Thread.currentThread();
                    continue;
                }
                if (!waiting) continue;
                LockSupport.park(this);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected final Node<V> find(int h, short k) {
            if (k != this.EMPTY) {
                Node e = this.first;
                while (e != null) {
                    TreeNode<V> p;
                    int s = this.lockState;
                    if ((s & 3) != 0) {
                        short ek;
                        if (e.hash == h && ((ek = e.key) == k || ek != this.EMPTY && k == ek)) {
                            return e;
                        }
                        e = e.next;
                        continue;
                    }
                    if (!U.compareAndSwapInt(this, LOCKSTATE, s, s + 4)) continue;
                    try {
                        TreeNode<V> r = this.root;
                        p = r == null ? null : r.findTreeNode(h, k, null);
                    }
                    finally {
                        Thread w;
                        if (U.getAndAddInt(this, LOCKSTATE, -4) == 6 && (w = this.waiter) != null) {
                            LockSupport.unpark(w);
                        }
                    }
                    return p;
                }
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected final TreeNode<V> putTreeVal(int h, short k, V v) {
            block19: {
                TreeNode<V> xp;
                int dir;
                Class<?> kc = null;
                boolean searched = false;
                TreeNode<V> p = this.root;
                do {
                    if (p == null) {
                        this.root = new TreeNode<V>(this.EMPTY, h, k, v, null, null);
                        this.first = this.root;
                        break block19;
                    }
                    int ph = p.hash;
                    if (ph > h) {
                        dir = -1;
                    } else if (ph < h) {
                        dir = 1;
                    } else {
                        short pk = p.key;
                        if (pk == k || pk != this.EMPTY && k == pk) {
                            return p;
                        }
                        dir = Short.compare(k, pk);
                        if (dir == 0) {
                            if (!searched) {
                                TreeNode q;
                                searched = true;
                                TreeNode ch = p.left;
                                if (ch != null && (q = ch.findTreeNode(h, k, kc)) != null || (ch = p.right) != null && (q = ch.findTreeNode(h, k, kc)) != null) {
                                    return q;
                                }
                            }
                            dir = this.tieBreakOrder(k, pk);
                        }
                    }
                    xp = p;
                } while ((p = dir <= 0 ? p.left : p.right) != null);
                TreeNode<V> f = this.first;
                TreeNode<V> x = new TreeNode<V>(this.EMPTY, h, k, v, f, xp);
                this.first = x;
                if (f != null) {
                    f.prev = x;
                }
                if (dir <= 0) {
                    xp.left = x;
                } else {
                    xp.right = x;
                }
                if (!xp.red) {
                    x.red = true;
                } else {
                    this.lockRoot();
                    try {
                        this.root = this.balanceInsertion(this.root, x);
                    }
                    finally {
                        this.unlockRoot();
                    }
                }
            }
            assert (this.checkInvariants(this.root));
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected final boolean removeTreeNode(TreeNode<V> p) {
            TreeNode rl;
            TreeNode next = (TreeNode)p.next;
            TreeNode pred = p.prev;
            if (pred == null) {
                this.first = next;
            } else {
                pred.next = next;
            }
            if (next != null) {
                next.prev = pred;
            }
            if (this.first == null) {
                this.root = null;
                return true;
            }
            TreeNode<V> r = this.root;
            if (r == null || r.right == null || (rl = r.left) == null || rl.left == null) {
                return true;
            }
            this.lockRoot();
            try {
                TreeNode pp;
                TreeNode replacement;
                TreeNode pl = p.left;
                TreeNode pr = p.right;
                if (pl != null && pr != null) {
                    TreeNode sl;
                    TreeNode s = pr;
                    while ((sl = s.left) != null) {
                        s = sl;
                    }
                    boolean c = s.red;
                    s.red = p.red;
                    p.red = c;
                    TreeNode sr = s.right;
                    TreeNode pp2 = p.parent;
                    if (s == pr) {
                        p.parent = s;
                        s.right = p;
                    } else {
                        TreeNode sp = s.parent;
                        p.parent = sp;
                        if (p.parent != null) {
                            if (s == sp.left) {
                                sp.left = p;
                            } else {
                                sp.right = p;
                            }
                        }
                        if ((s.right = pr) != null) {
                            pr.parent = s;
                        }
                    }
                    p.left = null;
                    p.right = sr;
                    if (p.right != null) {
                        sr.parent = p;
                    }
                    if ((s.left = pl) != null) {
                        pl.parent = s;
                    }
                    if ((s.parent = pp2) == null) {
                        r = s;
                    } else if (p == pp2.left) {
                        pp2.left = s;
                    } else {
                        pp2.right = s;
                    }
                    replacement = sr != null ? sr : p;
                } else {
                    replacement = pl != null ? pl : (pr != null ? pr : p);
                }
                if (replacement != p) {
                    replacement.parent = p.parent;
                    pp = replacement.parent;
                    if (pp == null) {
                        r = replacement;
                    } else if (p == pp.left) {
                        pp.left = replacement;
                    } else {
                        pp.right = replacement;
                    }
                    p.parent = null;
                    p.right = null;
                    p.left = null;
                }
                TreeNode<V> treeNode = this.root = p.red ? r : this.balanceDeletion(r, replacement);
                if (p == replacement && (pp = p.parent) != null) {
                    if (p == pp.left) {
                        pp.left = null;
                    } else if (p == pp.right) {
                        pp.right = null;
                    }
                    p.parent = null;
                }
            }
            finally {
                this.unlockRoot();
            }
            assert (this.checkInvariants(this.root));
            return false;
        }

        protected <V> TreeNode<V> rotateLeft(TreeNode<V> root, TreeNode<V> p) {
            TreeNode r;
            if (p != null && (r = p.right) != null) {
                p.right = r.left;
                TreeNode rl = p.right;
                if (p.right != null) {
                    rl.parent = p;
                }
                TreeNode pp = r.parent = p.parent;
                if (r.parent == null) {
                    root = r;
                    r.red = false;
                } else if (pp.left == p) {
                    pp.left = r;
                } else {
                    pp.right = r;
                }
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        protected <V> TreeNode<V> rotateRight(TreeNode<V> root, TreeNode<V> p) {
            TreeNode l;
            if (p != null && (l = p.left) != null) {
                p.left = l.right;
                TreeNode lr = p.left;
                if (p.left != null) {
                    lr.parent = p;
                }
                TreeNode pp = l.parent = p.parent;
                if (l.parent == null) {
                    root = l;
                    l.red = false;
                } else if (pp.right == p) {
                    pp.right = l;
                } else {
                    pp.left = l;
                }
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        protected <V> TreeNode<V> balanceInsertion(TreeNode<V> root, TreeNode<V> x) {
            x.red = true;
            while (true) {
                TreeNode xpp;
                TreeNode xp;
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                if (!xp.red || (xpp = xp.parent) == null) {
                    return root;
                }
                TreeNode xppl = xpp.left;
                if (xp == xppl) {
                    TreeNode xppr = xpp.right;
                    if (xppr != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                        continue;
                    }
                    if (x == xp.right) {
                        x = xp;
                        root = this.rotateLeft(root, x);
                        xp = x.parent;
                        TreeNode treeNode = xpp = xp == null ? null : xp.parent;
                    }
                    if (xp == null) continue;
                    xp.red = false;
                    if (xpp == null) continue;
                    xpp.red = true;
                    root = this.rotateRight(root, xpp);
                    continue;
                }
                if (xppl != null && xppl.red) {
                    xppl.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                    continue;
                }
                if (x == xp.left) {
                    x = xp;
                    root = this.rotateRight(root, x);
                    xp = x.parent;
                    TreeNode treeNode = xpp = xp == null ? null : xp.parent;
                }
                if (xp == null) continue;
                xp.red = false;
                if (xpp == null) continue;
                xpp.red = true;
                root = this.rotateLeft(root, xpp);
            }
        }

        protected <V> TreeNode<V> balanceDeletion(TreeNode<V> root, TreeNode<V> x) {
            while (x != null && x != root) {
                TreeNode sr;
                TreeNode sl;
                TreeNode xp = x.parent;
                if (xp == null) {
                    x.red = false;
                    return x;
                }
                if (x.red) {
                    x.red = false;
                    return root;
                }
                TreeNode xpl = xp.left;
                if (xpl == x) {
                    TreeNode xpr = xp.right;
                    if (xpr != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = this.rotateLeft(root, xp);
                        xp = x.parent;
                        TreeNode treeNode = xpr = xp == null ? null : xp.right;
                    }
                    if (xpr == null) {
                        x = xp;
                        continue;
                    }
                    sl = xpr.left;
                    sr = xpr.right;
                    if (!(sr != null && sr.red || sl != null && sl.red)) {
                        xpr.red = true;
                        x = xp;
                        continue;
                    }
                    if (sr == null || !sr.red) {
                        if (sl != null) {
                            sl.red = false;
                        }
                        xpr.red = true;
                        root = this.rotateRight(root, xpr);
                        xp = x.parent;
                        TreeNode treeNode = xpr = xp == null ? null : xp.right;
                    }
                    if (xpr != null) {
                        xpr.red = xp == null ? false : xp.red;
                        sr = xpr.right;
                        if (sr != null) {
                            sr.red = false;
                        }
                    }
                    if (xp != null) {
                        xp.red = false;
                        root = this.rotateLeft(root, xp);
                    }
                    x = root;
                    continue;
                }
                if (xpl != null && xpl.red) {
                    xpl.red = false;
                    xp.red = true;
                    root = this.rotateRight(root, xp);
                    xp = x.parent;
                    TreeNode treeNode = xpl = xp == null ? null : xp.left;
                }
                if (xpl == null) {
                    x = xp;
                    continue;
                }
                sl = xpl.left;
                sr = xpl.right;
                if (!(sl != null && sl.red || sr != null && sr.red)) {
                    xpl.red = true;
                    x = xp;
                    continue;
                }
                if (sl == null || !sl.red) {
                    if (sr != null) {
                        sr.red = false;
                    }
                    xpl.red = true;
                    root = this.rotateLeft(root, xpl);
                    xp = x.parent;
                    TreeNode treeNode = xpl = xp == null ? null : xp.left;
                }
                if (xpl != null) {
                    xpl.red = xp == null ? false : xp.red;
                    sl = xpl.left;
                    if (sl != null) {
                        sl.red = false;
                    }
                }
                if (xp != null) {
                    xp.red = false;
                    root = this.rotateRight(root, xp);
                }
                x = root;
            }
            return root;
        }

        protected <V> boolean checkInvariants(TreeNode<V> t) {
            TreeNode tp = t.parent;
            TreeNode tl = t.left;
            TreeNode tr = t.right;
            TreeNode tb = t.prev;
            TreeNode tn = (TreeNode)t.next;
            if (tb != null && tb.next != t) {
                return false;
            }
            if (tn != null && tn.prev != t) {
                return false;
            }
            if (tp != null && t != tp.left && t != tp.right) {
                return false;
            }
            if (tl != null && (tl.parent != t || tl.hash > t.hash)) {
                return false;
            }
            if (tr != null && (tr.parent != t || tr.hash < t.hash)) {
                return false;
            }
            if (t.red && tl != null && tl.red && tr != null && tr.red) {
                return false;
            }
            if (tl != null && !this.checkInvariants(tl)) {
                return false;
            }
            return tr == null || this.checkInvariants(tr);
        }

        static {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                U = (Unsafe)f.get(null);
                Class<TreeBin> k = TreeBin.class;
                LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
            }
            catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    protected static final class TreeNode<V>
    extends Node<V> {
        public TreeNode<V> parent;
        public TreeNode<V> left;
        public TreeNode<V> right;
        public TreeNode<V> prev;
        public boolean red;

        public TreeNode(short empty, int hash, short key, V val, Node<V> next, TreeNode<V> parent) {
            super(empty, hash, key, val, next);
            this.parent = parent;
        }

        @Override
        protected Node<V> find(int h, short k) {
            return this.findTreeNode(h, k, null);
        }

        protected final TreeNode<V> findTreeNode(int h, short k, Class<?> kc) {
            if (k != this.EMPTY) {
                TreeNode<V> p = this;
                do {
                    TreeNode<V> pl = p.left;
                    TreeNode<V> pr = p.right;
                    int ph = p.hash;
                    if (ph > h) {
                        p = pl;
                        continue;
                    }
                    if (ph < h) {
                        p = pr;
                        continue;
                    }
                    short pk = p.key;
                    if (pk == k || pk != this.EMPTY && k == pk) {
                        return p;
                    }
                    if (pl == null) {
                        p = pr;
                        continue;
                    }
                    if (pr == null) {
                        p = pl;
                        continue;
                    }
                    int dir = Short.compare(k, pk);
                    if (dir != 0) {
                        p = dir < 0 ? pl : pr;
                        continue;
                    }
                    TreeNode<V> q = pr.findTreeNode(h, k, kc);
                    if (q != null) {
                        return q;
                    }
                    p = pl;
                } while (p != null);
            }
            return null;
        }
    }

    public static class KeySetView<V>
    implements ShortSet {
        public static final long serialVersionUID = 7249069246763182397L;
        public final Short2ObjectConcurrentHashMap<V> map;
        public final V value;

        public KeySetView(Short2ObjectConcurrentHashMap<V> map, V value) {
            this.map = map;
            this.value = value;
        }

        public V getMappedValue() {
            return this.value;
        }

        @Override
        public boolean contains(short o) {
            return this.map.containsKey(o);
        }

        @Override
        public boolean remove(short o) {
            return this.map.remove(o) != null;
        }

        @Override
        public ShortIterator iterator() {
            Short2ObjectConcurrentHashMap<V> m = this.map;
            Node<V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new KeyIterator(t, f, 0, f, m);
        }

        @Override
        public boolean add(short e) {
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            return this.map.putVal(e, v, true) == null;
        }

        @Override
        public boolean addAll(ShortCollection c) {
            boolean added = false;
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            ShortIterator iter = c.iterator();
            while (iter.hasNext()) {
                short e = iter.nextShort();
                if (this.map.putVal(e, v, true) != null) continue;
                added = true;
            }
            return added;
        }

        @Override
        public int hashCode() {
            int h = 0;
            ShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                h += Short.hashCode(iter.nextShort());
            }
            return h;
        }

        @Override
        public boolean equals(Object o) {
            ShortSet c;
            return o instanceof ShortSet && ((c = (ShortSet)o) == this || this.containsAll(c) && c.containsAll(this));
        }

        public short getNoEntryValue() {
            return this.map.EMPTY;
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public Object[] toArray() {
            int i;
            Object[] out = new Short[this.size()];
            ShortIterator iter = this.iterator();
            for (i = 0; i < out.length && iter.hasNext(); ++i) {
                out[i] = iter.nextShort();
            }
            if (out.length > i + 1) {
                out[i] = this.map.EMPTY;
            }
            return out;
        }

        @Override
        public Object[] toArray(Object[] dest) {
            int i;
            ShortIterator iter = this.iterator();
            for (i = 0; i < dest.length && iter.hasNext() && i <= dest.length; ++i) {
                dest[i] = iter.next();
            }
            if (dest.length > i + 1) {
                dest[i] = this.map.EMPTY;
            }
            return dest;
        }

        @Override
        public short[] toShortArray() {
            int i;
            short[] out = new short[this.size()];
            ShortIterator iter = this.iterator();
            for (i = 0; i < out.length && iter.hasNext(); ++i) {
                out[i] = iter.next();
            }
            if (out.length > i + 1) {
                out[i] = this.map.EMPTY;
            }
            return out;
        }

        @Override
        public short[] toArray(short[] dest) {
            int i;
            ShortIterator iter = this.iterator();
            for (i = 0; i < dest.length && iter.hasNext() && i <= dest.length; ++i) {
                dest[i] = iter.next();
            }
            if (dest.length > i + 1) {
                dest[i] = this.map.EMPTY;
            }
            return dest;
        }

        @Override
        public short[] toShortArray(short[] dest) {
            return this.toArray(dest);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Long) {
                    short c = (Short)element;
                    if (this.contains(c)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(ShortCollection collection) {
            ShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                short element = iter.next();
                if (this.contains(element)) continue;
                return false;
            }
            return true;
        }

        public boolean containsAll(short[] array) {
            int i = array.length;
            while (i-- > 0) {
                if (this.contains(array[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            boolean changed = false;
            for (Short s : collection) {
                short e = s;
                if (!this.add(e)) continue;
                changed = true;
            }
            return changed;
        }

        public boolean addAll(short[] array) {
            boolean changed = false;
            int i = array.length;
            while (i-- > 0) {
                if (!this.add(array[i])) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            ShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains((short)iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(ShortCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            ShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        public boolean retainAll(short[] array) {
            boolean modified = false;
            ShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (Arrays.binarySearch(array, iter.next()) >= 0) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                short c;
                if (!(element instanceof Short) || !this.remove(c = ((Short)element).shortValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(ShortCollection collection) {
            boolean changed = false;
            ShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                short element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        public boolean removeAll(short[] array) {
            boolean changed = false;
            int i = array.length;
            while (i-- > 0) {
                if (!this.remove(array[i])) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public void clear() {
            this.map.clear();
        }

        @Override
        public ShortSpliterator spliterator() {
            Short2ObjectConcurrentHashMap<V> m = this.map;
            long n = m.sumCount();
            Node<V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new KeySpliterator(t, f, 0, f, n < 0L ? 0L : n);
        }
    }

    protected static final class ValuesView<V>
    extends CollectionView<V, V>
    implements FastCollection<V>,
    Serializable {
        public static final long serialVersionUID = 2249069246763182397L;

        public ValuesView(Short2ObjectConcurrentHashMap<V> map) {
            super(map);
        }

        @Override
        public final boolean contains(Object o) {
            return this.map.containsValue(o);
        }

        @Override
        public final boolean remove(Object o) {
            if (o != null) {
                Iterator it = this.iterator();
                while (it.hasNext()) {
                    if (!o.equals(it.next())) continue;
                    it.remove();
                    return true;
                }
            }
            return false;
        }

        @Override
        public final ObjectIterator<V> iterator() {
            Short2ObjectConcurrentHashMap m = this.map;
            Node<V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new ValueIterator(t, f, 0, f, m);
        }

        @Override
        public final boolean add(V e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSpliterator<V> spliterator() {
            Short2ObjectConcurrentHashMap m = this.map;
            long n = m.sumCount();
            Node<V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new ValueSpliterator(t, f, 0, f, n < 0L ? 0L : n);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            block14: {
                if (action == null) {
                    throw new NullPointerException();
                }
                Node<V>[] tt = this.map.table;
                if (this.map.table == null) break block14;
                Node<V>[] tab = tt;
                Node next = null;
                TableStack stack = null;
                TableStack spare = null;
                int index = 0;
                int baseIndex = 0;
                int baseLimit = tt.length;
                int baseSize = tt.length;
                while (true) {
                    Node p = null;
                    p = next;
                    if (p != null) {
                        p = p.next;
                    }
                    while (true) {
                        TableStack s;
                        int i;
                        int n;
                        Node<V>[] t;
                        block16: {
                            block15: {
                                if (p != null) {
                                    next = p;
                                    break;
                                }
                                if (baseIndex >= baseLimit) break block15;
                                t = tab;
                                if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                            }
                            next = null;
                            break;
                        }
                        p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                        if (p != null && p.hash < 0) {
                            if (p instanceof ForwardingNode) {
                                tab = ((ForwardingNode)p).nextTable;
                                p = null;
                                s = spare;
                                if (s != null) {
                                    spare = s.next;
                                } else {
                                    s = new TableStack();
                                }
                                s.tab = t;
                                s.length = n;
                                s.index = i;
                                s.next = stack;
                                stack = s;
                                continue;
                            }
                            p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                        }
                        if (stack != null) {
                            int len;
                            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                                n = len;
                                index = s.index;
                                tab = s.tab;
                                s.tab = null;
                                TableStack anext = s.next;
                                s.next = spare;
                                stack = anext;
                                spare = s;
                            }
                            if (s != null || (index += baseSize) < n) continue;
                            index = ++baseIndex;
                            continue;
                        }
                        index = i + baseSize;
                        if (index < n) continue;
                        index = ++baseIndex;
                    }
                    if (p == null) break;
                    action.accept(p.val);
                }
            }
        }

        @Override
        public <A, B, C, D> void forEach(FastCollection.FastConsumerD9<? super V, A, B, C, D> consumer, A a, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, B b, C c, D d) {
            block14: {
                if (consumer == null) {
                    throw new NullPointerException();
                }
                Node<V>[] tt = this.map.table;
                if (this.map.table == null) break block14;
                Node<V>[] tab = tt;
                Node next = null;
                TableStack stack = null;
                TableStack spare = null;
                int index = 0;
                int baseIndex = 0;
                int baseLimit = tt.length;
                int baseSize = tt.length;
                while (true) {
                    Node p = null;
                    p = next;
                    if (p != null) {
                        p = p.next;
                    }
                    while (true) {
                        TableStack s;
                        int i;
                        int n;
                        Node<V>[] t;
                        block16: {
                            block15: {
                                if (p != null) {
                                    next = p;
                                    break;
                                }
                                if (baseIndex >= baseLimit) break block15;
                                t = tab;
                                if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                            }
                            next = null;
                            break;
                        }
                        p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                        if (p != null && p.hash < 0) {
                            if (p instanceof ForwardingNode) {
                                tab = ((ForwardingNode)p).nextTable;
                                p = null;
                                s = spare;
                                if (s != null) {
                                    spare = s.next;
                                } else {
                                    s = new TableStack();
                                }
                                s.tab = t;
                                s.length = n;
                                s.index = i;
                                s.next = stack;
                                stack = s;
                                continue;
                            }
                            p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                        }
                        if (stack != null) {
                            int len;
                            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                                n = len;
                                index = s.index;
                                tab = s.tab;
                                s.tab = null;
                                TableStack anext = s.next;
                                s.next = spare;
                                stack = anext;
                                spare = s;
                            }
                            if (s != null || (index += baseSize) < n) continue;
                            index = ++baseIndex;
                            continue;
                        }
                        index = i + baseSize;
                        if (index < n) continue;
                        index = ++baseIndex;
                    }
                    if (p == null) break;
                    consumer.accept(p.val, a, d1, d2, d3, d4, d5, d6, d7, d8, d9, b, c, d);
                }
            }
        }

        @Override
        public <A, B, C, D> void forEach(FastCollection.FastConsumerD6<? super V, A, B, C, D> consumer, A a, double d1, double d2, double d3, double d4, double d5, double d6, B b, C c, D d) {
            block14: {
                if (consumer == null) {
                    throw new NullPointerException();
                }
                Node<V>[] tt = this.map.table;
                if (this.map.table == null) break block14;
                Node<V>[] tab = tt;
                Node next = null;
                TableStack stack = null;
                TableStack spare = null;
                int index = 0;
                int baseIndex = 0;
                int baseLimit = tt.length;
                int baseSize = tt.length;
                while (true) {
                    Node p = null;
                    p = next;
                    if (p != null) {
                        p = p.next;
                    }
                    while (true) {
                        TableStack s;
                        int i;
                        int n;
                        Node<V>[] t;
                        block16: {
                            block15: {
                                if (p != null) {
                                    next = p;
                                    break;
                                }
                                if (baseIndex >= baseLimit) break block15;
                                t = tab;
                                if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                            }
                            next = null;
                            break;
                        }
                        p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                        if (p != null && p.hash < 0) {
                            if (p instanceof ForwardingNode) {
                                tab = ((ForwardingNode)p).nextTable;
                                p = null;
                                s = spare;
                                if (s != null) {
                                    spare = s.next;
                                } else {
                                    s = new TableStack();
                                }
                                s.tab = t;
                                s.length = n;
                                s.index = i;
                                s.next = stack;
                                stack = s;
                                continue;
                            }
                            p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                        }
                        if (stack != null) {
                            int len;
                            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                                n = len;
                                index = s.index;
                                tab = s.tab;
                                s.tab = null;
                                TableStack anext = s.next;
                                s.next = spare;
                                stack = anext;
                                spare = s;
                            }
                            if (s != null || (index += baseSize) < n) continue;
                            index = ++baseIndex;
                            continue;
                        }
                        index = i + baseSize;
                        if (index < n) continue;
                        index = ++baseIndex;
                    }
                    if (p == null) break;
                    consumer.accept(p.val, a, d1, d2, d3, d4, d5, d6, b, c, d);
                }
            }
        }

        @Override
        public void forEachWithFloat(FastCollection.FastConsumerF<? super V> consumer, float ii) {
            block14: {
                if (consumer == null) {
                    throw new NullPointerException();
                }
                Node<V>[] tt = this.map.table;
                if (this.map.table == null) break block14;
                Node<V>[] tab = tt;
                Node next = null;
                TableStack stack = null;
                TableStack spare = null;
                int index = 0;
                int baseIndex = 0;
                int baseLimit = tt.length;
                int baseSize = tt.length;
                while (true) {
                    Node p = null;
                    p = next;
                    if (p != null) {
                        p = p.next;
                    }
                    while (true) {
                        TableStack s;
                        int i;
                        int n;
                        Node<V>[] t;
                        block16: {
                            block15: {
                                if (p != null) {
                                    next = p;
                                    break;
                                }
                                if (baseIndex >= baseLimit) break block15;
                                t = tab;
                                if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                            }
                            next = null;
                            break;
                        }
                        p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                        if (p != null && p.hash < 0) {
                            if (p instanceof ForwardingNode) {
                                tab = ((ForwardingNode)p).nextTable;
                                p = null;
                                s = spare;
                                if (s != null) {
                                    spare = s.next;
                                } else {
                                    s = new TableStack();
                                }
                                s.tab = t;
                                s.length = n;
                                s.index = i;
                                s.next = stack;
                                stack = s;
                                continue;
                            }
                            p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                        }
                        if (stack != null) {
                            int len;
                            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                                n = len;
                                index = s.index;
                                tab = s.tab;
                                s.tab = null;
                                TableStack anext = s.next;
                                s.next = spare;
                                stack = anext;
                                spare = s;
                            }
                            if (s != null || (index += baseSize) < n) continue;
                            index = ++baseIndex;
                            continue;
                        }
                        index = i + baseSize;
                        if (index < n) continue;
                        index = ++baseIndex;
                    }
                    if (p == null) break;
                    consumer.accept(p.val, ii);
                }
            }
        }

        @Override
        public void forEachWithInt(FastCollection.FastConsumerI<? super V> consumer, int ii) {
            block14: {
                if (consumer == null) {
                    throw new NullPointerException();
                }
                Node<V>[] tt = this.map.table;
                if (this.map.table == null) break block14;
                Node<V>[] tab = tt;
                Node next = null;
                TableStack stack = null;
                TableStack spare = null;
                int index = 0;
                int baseIndex = 0;
                int baseLimit = tt.length;
                int baseSize = tt.length;
                while (true) {
                    Node p = null;
                    p = next;
                    if (p != null) {
                        p = p.next;
                    }
                    while (true) {
                        TableStack s;
                        int i;
                        int n;
                        Node<V>[] t;
                        block16: {
                            block15: {
                                if (p != null) {
                                    next = p;
                                    break;
                                }
                                if (baseIndex >= baseLimit) break block15;
                                t = tab;
                                if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                            }
                            next = null;
                            break;
                        }
                        p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                        if (p != null && p.hash < 0) {
                            if (p instanceof ForwardingNode) {
                                tab = ((ForwardingNode)p).nextTable;
                                p = null;
                                s = spare;
                                if (s != null) {
                                    spare = s.next;
                                } else {
                                    s = new TableStack();
                                }
                                s.tab = t;
                                s.length = n;
                                s.index = i;
                                s.next = stack;
                                stack = s;
                                continue;
                            }
                            p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                        }
                        if (stack != null) {
                            int len;
                            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                                n = len;
                                index = s.index;
                                tab = s.tab;
                                s.tab = null;
                                TableStack anext = s.next;
                                s.next = spare;
                                stack = anext;
                                spare = s;
                            }
                            if (s != null || (index += baseSize) < n) continue;
                            index = ++baseIndex;
                            continue;
                        }
                        index = i + baseSize;
                        if (index < n) continue;
                        index = ++baseIndex;
                    }
                    if (p == null) break;
                    consumer.accept(p.val, ii);
                }
            }
        }

        @Override
        public void forEachWithLong(FastCollection.FastConsumerL<? super V> consumer, long ii) {
            block14: {
                if (consumer == null) {
                    throw new NullPointerException();
                }
                Node<V>[] tt = this.map.table;
                if (this.map.table == null) break block14;
                Node<V>[] tab = tt;
                Node next = null;
                TableStack stack = null;
                TableStack spare = null;
                int index = 0;
                int baseIndex = 0;
                int baseLimit = tt.length;
                int baseSize = tt.length;
                while (true) {
                    Node p = null;
                    p = next;
                    if (p != null) {
                        p = p.next;
                    }
                    while (true) {
                        TableStack s;
                        int i;
                        int n;
                        Node<V>[] t;
                        block16: {
                            block15: {
                                if (p != null) {
                                    next = p;
                                    break;
                                }
                                if (baseIndex >= baseLimit) break block15;
                                t = tab;
                                if (tab != null && (n = t.length) > (i = index) && i >= 0) break block16;
                            }
                            next = null;
                            break;
                        }
                        p = Short2ObjectConcurrentHashMap.tabAt(t, i);
                        if (p != null && p.hash < 0) {
                            if (p instanceof ForwardingNode) {
                                tab = ((ForwardingNode)p).nextTable;
                                p = null;
                                s = spare;
                                if (s != null) {
                                    spare = s.next;
                                } else {
                                    s = new TableStack();
                                }
                                s.tab = t;
                                s.length = n;
                                s.index = i;
                                s.next = stack;
                                stack = s;
                                continue;
                            }
                            p = p instanceof TreeBin ? ((TreeBin)p).first : null;
                        }
                        if (stack != null) {
                            int len;
                            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                                n = len;
                                index = s.index;
                                tab = s.tab;
                                s.tab = null;
                                TableStack anext = s.next;
                                s.next = spare;
                                stack = anext;
                                spare = s;
                            }
                            if (s != null || (index += baseSize) < n) continue;
                            index = ++baseIndex;
                            continue;
                        }
                        index = i + baseSize;
                        if (index < n) continue;
                        index = ++baseIndex;
                    }
                    if (p == null) break;
                    consumer.accept(p.val, ii);
                }
            }
        }
    }

    protected static final class EntrySetView<V>
    extends CollectionView<V, Short2ObjectMap.Entry<V>>
    implements ObjectSet<Short2ObjectMap.Entry<V>>,
    Serializable {
        public static final long serialVersionUID = 2249069246763182397L;

        public EntrySetView(Short2ObjectConcurrentHashMap<V> map) {
            super(map);
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Short2ObjectMap.Entry) {
                Short2ObjectMap.Entry e = (Short2ObjectMap.Entry)o;
                short k = e.getShortKey();
                if (!((Entry)o).isEmpty()) {
                    Object v;
                    Object r = this.map.get(k);
                    return r != null && (v = e.getValue()) != null && (v == r || v.equals(r));
                }
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Short2ObjectMap.Entry) {
                Short2ObjectMap.Entry e = (Short2ObjectMap.Entry)o;
                short k = e.getShortKey();
                if (!((Entry)o).isEmpty()) {
                    Object v = e.getValue();
                    return v != null && this.map.remove(k, v);
                }
            }
            return false;
        }

        @Override
        public ObjectIterator<Short2ObjectMap.Entry<V>> iterator() {
            Short2ObjectConcurrentHashMap m = this.map;
            Node<V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new EntryIterator(t, f, 0, f, m);
        }

        @Override
        public boolean add(Short2ObjectMap.Entry<V> e) {
            return this.map.putVal(e.getShortKey(), e.getValue(), false) == null;
        }

        @Override
        public boolean addAll(Collection<? extends Short2ObjectMap.Entry<V>> c) {
            boolean added = false;
            for (Short2ObjectMap.Entry<V> e : c) {
                if (!this.add(e)) continue;
                added = true;
            }
            return added;
        }

        @Override
        public final int hashCode() {
            int h = 0;
            Node<V>[] t = this.map.table;
            if (this.map.table != null) {
                Node p;
                Traverser it = new Traverser(t, t.length, 0, t.length);
                while ((p = it.advance()) != null) {
                    h += p.hashCode();
                }
            }
            return h;
        }

        @Override
        public final boolean equals(Object o) {
            Set c;
            return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
        }

        @Override
        public ObjectSpliterator<Short2ObjectMap.Entry<V>> spliterator() {
            Short2ObjectConcurrentHashMap m = this.map;
            long n = m.sumCount();
            Node<V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new EntrySpliterator(t, f, 0, f, n < 0L ? 0L : n, m);
        }

        @Override
        public void forEach(Consumer<? super Short2ObjectMap.Entry<V>> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node<V>[] t = this.map.table;
            if (this.map.table != null) {
                Node p;
                Traverser it = new Traverser(t, t.length, 0, t.length);
                while ((p = it.advance()) != null) {
                    action.accept(new MapEntry(p.isEmpty(), p.key, p.val, this.map));
                }
            }
        }
    }

    protected static class Traverser<V> {
        public Node<V>[] tab;
        public Node<V> next;
        public TableStack<V> stack;
        public TableStack<V> spare;
        public int index;
        public int baseIndex;
        public int baseLimit;
        public final int baseSize;

        public Traverser(Node<V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        protected final Node<V> advance() {
            Node<V> e = this.next;
            if (e != null) {
                e = e.next;
            }
            while (true) {
                int i;
                int n;
                Node<V>[] t;
                block10: {
                    block9: {
                        if (e != null) {
                            this.next = e;
                            return this.next;
                        }
                        if (this.baseIndex >= this.baseLimit) break block9;
                        t = this.tab;
                        if (this.tab != null && (n = t.length) > (i = this.index) && i >= 0) break block10;
                    }
                    this.next = null;
                    return null;
                }
                e = Short2ObjectConcurrentHashMap.tabAt(t, i);
                if (e != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        this.tab = ((ForwardingNode)e).nextTable;
                        e = null;
                        this.pushState(t, i, n);
                        continue;
                    }
                    e = e instanceof TreeBin ? ((TreeBin)e).first : null;
                }
                if (this.stack != null) {
                    this.recoverState(n);
                    continue;
                }
                this.index = i + this.baseSize;
                if (this.index < n) continue;
                this.index = ++this.baseIndex;
            }
        }

        protected void pushState(Node<V>[] t, int i, int n) {
            TableStack<V> s = this.spare;
            if (s != null) {
                this.spare = s.next;
            } else {
                s = new TableStack();
            }
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = this.stack;
            this.stack = s;
        }

        protected void recoverState(int n) {
            int len;
            TableStack<V> s;
            while ((s = this.stack) != null && (this.index += (len = s.length)) >= n) {
                n = len;
                this.index = s.index;
                this.tab = s.tab;
                s.tab = null;
                TableStack next = s.next;
                s.next = this.spare;
                this.stack = next;
                this.spare = s;
            }
            if (s == null && (this.index += this.baseSize) >= n) {
                this.index = ++this.baseIndex;
            }
        }
    }

    @FunctionalInterface
    public static interface ShortObjConsumer<V> {
        public void accept(short var1, V var2);
    }

    @FunctionalInterface
    public static interface ShortBiObjConsumer<V, X> {
        public void accept(short var1, V var2, X var3);
    }

    @FunctionalInterface
    public static interface ShortTriObjConsumer<V, X, Y> {
        public void accept(short var1, V var2, X var3, Y var4);
    }

    @FunctionalInterface
    public static interface ShortObjByteConsumer<V> {
        public void accept(short var1, V var2, byte var3);
    }

    @FunctionalInterface
    public static interface ShortObjShortConsumer<V> {
        public void accept(short var1, V var2, short var3);
    }

    @FunctionalInterface
    public static interface ShortObjIntConsumer<V> {
        public void accept(short var1, V var2, int var3);
    }

    @FunctionalInterface
    public static interface ShortObjLongConsumer<V> {
        public void accept(short var1, V var2, long var3);
    }

    @FunctionalInterface
    public static interface ShortObjFloatConsumer<V> {
        public void accept(short var1, V var2, float var3);
    }

    @FunctionalInterface
    public static interface ShortObjDoubleConsumer<V> {
        public void accept(short var1, V var2, double var3);
    }

    @FunctionalInterface
    public static interface ShortBiObjByteConsumer<V, X> {
        public void accept(short var1, V var2, byte var3, X var4);
    }

    @FunctionalInterface
    public static interface ShortBiObjShortConsumer<V, X> {
        public void accept(short var1, V var2, short var3, X var4);
    }

    @FunctionalInterface
    public static interface ShortBiObjIntConsumer<V, X> {
        public void accept(short var1, V var2, int var3, X var4);
    }

    @FunctionalInterface
    public static interface ShortBiObjLongConsumer<V, X> {
        public void accept(short var1, V var2, long var3, X var5);
    }

    @FunctionalInterface
    public static interface ShortBiObjFloatConsumer<V, X> {
        public void accept(short var1, V var2, float var3, X var4);
    }

    @FunctionalInterface
    public static interface ShortBiObjDoubleConsumer<V, X> {
        public void accept(short var1, V var2, double var3, X var5);
    }

    protected static final class ReservationNode<V>
    extends Node<V> {
        public ReservationNode(short empty) {
            super(empty, -3, empty, null, null);
        }

        @Override
        protected Node<V> find(int h, short k) {
            return null;
        }
    }

    @FunctionalInterface
    public static interface ShortFunction<R> {
        public R apply(short var1);
    }

    @FunctionalInterface
    public static interface ShortObjFunction<V, J> {
        public J apply(short var1, V var2);
    }

    protected static final class CounterCell {
        public volatile long value;

        public CounterCell(long x) {
            this.value = x;
        }
    }

    protected static final class ForEachMappingTask<V>
    extends BulkTask<V, Void> {
        public final ShortObjConsumer<? super V> action;

        public ForEachMappingTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ShortObjConsumer<? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            ShortObjConsumer action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachMappingTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.accept(p.key, p.val);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static abstract class BulkTask<V, R>
    extends CountedCompleter<R> {
        public Node<V>[] tab;
        public Node<V> next;
        public TableStack<V> stack;
        public TableStack<V> spare;
        public int index;
        public int baseIndex;
        public int baseLimit;
        public final int baseSize;
        public int batch;

        protected BulkTask(BulkTask<V, ?> par, int b, int i, int f, Node<V>[] t) {
            super(par);
            this.batch = b;
            this.index = this.baseIndex = i;
            this.tab = t;
            if (t == null) {
                this.baseLimit = 0;
                this.baseSize = 0;
            } else if (par == null) {
                this.baseSize = this.baseLimit = t.length;
            } else {
                this.baseLimit = f;
                this.baseSize = par.baseSize;
            }
        }

        protected final Node<V> advance() {
            Node<V> e = this.next;
            if (e != null) {
                e = e.next;
            }
            while (true) {
                int i;
                int n;
                Node<V>[] t;
                block10: {
                    block9: {
                        if (e != null) {
                            this.next = e;
                            return this.next;
                        }
                        if (this.baseIndex >= this.baseLimit) break block9;
                        t = this.tab;
                        if (this.tab != null && (n = t.length) > (i = this.index) && i >= 0) break block10;
                    }
                    this.next = null;
                    return null;
                }
                e = Short2ObjectConcurrentHashMap.tabAt(t, i);
                if (e != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        this.tab = ((ForwardingNode)e).nextTable;
                        e = null;
                        this.pushState(t, i, n);
                        continue;
                    }
                    e = e instanceof TreeBin ? ((TreeBin)e).first : null;
                }
                if (this.stack != null) {
                    this.recoverState(n);
                    continue;
                }
                this.index = i + this.baseSize;
                if (this.index < n) continue;
                this.index = ++this.baseIndex;
            }
        }

        protected void pushState(Node<V>[] t, int i, int n) {
            TableStack<V> s = this.spare;
            if (s != null) {
                this.spare = s.next;
            } else {
                s = new TableStack();
            }
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = this.stack;
            this.stack = s;
        }

        protected void recoverState(int n) {
            int len;
            TableStack<V> s;
            while ((s = this.stack) != null && (this.index += (len = s.length)) >= n) {
                n = len;
                this.index = s.index;
                this.tab = s.tab;
                s.tab = null;
                TableStack next = s.next;
                s.next = this.spare;
                this.stack = next;
                this.spare = s;
            }
            if (s == null && (this.index += this.baseSize) >= n) {
                this.index = ++this.baseIndex;
            }
        }
    }

    protected static final class ForEachTransformedMappingTask<V, U>
    extends BulkTask<V, Void> {
        public final ShortObjFunction<? super V, ? extends U> transformer;
        public final Consumer<? super U> action;

        public ForEachTransformedMappingTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ShortObjFunction<? super V, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Consumer<U> action;
            ShortObjFunction transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedMappingTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key, p.val);
                    if (u == null) continue;
                    action.accept(u);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class SearchMappingsTask<V, U>
    extends BulkTask<V, U> {
        public final ShortObjFunction<? super V, ? extends U> searchFunction;
        public final AtomicReference<U> result;

        public SearchMappingsTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ShortObjFunction<? super V, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            ShortObjFunction searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchMappingsTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p.key, p.val);
                    if (u == null) continue;
                    if (!result.compareAndSet(null, u)) break;
                    this.quietlyCompleteRoot();
                    break;
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ShortBiObjFunction<V, X, J> {
        public J apply(short var1, V var2, X var3);
    }

    @FunctionalInterface
    public static interface ShortObjByteFunction<V, J> {
        public J apply(short var1, V var2, byte var3);
    }

    @FunctionalInterface
    public static interface ShortObjShortFunction<V, J> {
        public J apply(short var1, V var2, short var3);
    }

    @FunctionalInterface
    public static interface ShortObjIntFunction<V, J> {
        public J apply(short var1, V var2, int var3);
    }

    @FunctionalInterface
    public static interface ShortObjLongFunction<V, J> {
        public J apply(short var1, V var2, long var3);
    }

    @FunctionalInterface
    public static interface ShortObjFloatFunction<V, J> {
        public J apply(short var1, V var2, float var3);
    }

    @FunctionalInterface
    public static interface ShortObjDoubleFunction<V, J> {
        public J apply(short var1, V var2, double var3);
    }

    protected static final class MapReduceMappingsTask<V, U>
    extends BulkTask<V, U> {
        public final ShortObjFunction<? super V, ? extends U> transformer;
        public final BiFunction<? super U, ? super U, ? extends U> reducer;
        public U result;
        public MapReduceMappingsTask<V, U> rights;
        public MapReduceMappingsTask<V, U> nextRight;

        public MapReduceMappingsTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceMappingsTask<V, U> nextRight, ShortObjFunction<? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFunction<U, U, U> reducer;
            ShortObjFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key, p.val);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsTask t = (MapReduceMappingsTask)c;
                    MapReduceMappingsTask<V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceMappingsToDoubleTask<V>
    extends DoubleReturningBulkTask<V> {
        public final ToDoubleShortObjFunction<? super V> transformer;
        public final DoubleBinaryOperator reducer;
        public final double basis;
        public MapReduceMappingsToDoubleTask<V> rights;
        public MapReduceMappingsToDoubleTask<V> nextRight;

        public MapReduceMappingsToDoubleTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceMappingsToDoubleTask<V> nextRight, ToDoubleShortObjFunction<? super V> transformer, double basis, DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            DoubleBinaryOperator reducer;
            ToDoubleShortObjFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsToDoubleTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key, p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsToDoubleTask t = (MapReduceMappingsToDoubleTask)c;
                    MapReduceMappingsToDoubleTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ToDoubleShortObjFunction<V> {
        public double applyAsDouble(short var1, V var2);
    }

    protected static final class MapReduceMappingsToLongTask<V>
    extends LongReturningBulkTask<V> {
        public final ToLongShortObjFunction<? super V> transformer;
        public final LongBinaryOperator reducer;
        public final long basis;
        public MapReduceMappingsToLongTask<V> rights;
        public MapReduceMappingsToLongTask<V> nextRight;

        public MapReduceMappingsToLongTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceMappingsToLongTask<V> nextRight, ToLongShortObjFunction<? super V> transformer, long basis, LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            LongBinaryOperator reducer;
            ToLongShortObjFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsToLongTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.key, p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsToLongTask t = (MapReduceMappingsToLongTask)c;
                    MapReduceMappingsToLongTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ToLongShortObjFunction<V> {
        public long applyAsLong(short var1, V var2);
    }

    protected static final class MapReduceMappingsToIntTask<V>
    extends IntReturningBulkTask<V> {
        public final ToIntShortObjFunction<? super V> transformer;
        public final IntBinaryOperator reducer;
        public final int basis;
        public MapReduceMappingsToIntTask<V> rights;
        public MapReduceMappingsToIntTask<V> nextRight;

        public MapReduceMappingsToIntTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceMappingsToIntTask<V> nextRight, ToIntShortObjFunction<? super V> transformer, int basis, IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            IntBinaryOperator reducer;
            ToIntShortObjFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsToIntTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.key, p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsToIntTask t = (MapReduceMappingsToIntTask)c;
                    MapReduceMappingsToIntTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ToIntShortObjFunction<V> {
        public int applyAsInt(short var1, V var2);
    }

    protected static final class ForEachKeyTask<V>
    extends BulkTask<V, Void> {
        public final ShortConsumer action;

        public ForEachKeyTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ShortConsumer action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            ShortConsumer action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachKeyTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.accept(p.key);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class ForEachTransformedKeyTask<V, U>
    extends BulkTask<V, Void> {
        public final ShortFunction<? extends U> transformer;
        public final Consumer<? super U> action;

        public ForEachTransformedKeyTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ShortFunction<? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Consumer<U> action;
            ShortFunction<U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedKeyTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key);
                    if (u == null) continue;
                    action.accept(u);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class SearchKeysTask<V, U>
    extends BulkTask<V, U> {
        public final ShortFunction<? extends U> searchFunction;
        public final AtomicReference<U> result;

        public SearchKeysTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ShortFunction<? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            ShortFunction<U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchKeysTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p.key);
                    if (u == null) continue;
                    if (!result.compareAndSet(null, u)) break;
                    this.quietlyCompleteRoot();
                    break;
                }
            }
        }
    }

    protected static final class ReduceKeysTask<V>
    extends ShortReturningBulkTask2<V> {
        public final short EMPTY;
        public final ShortReduceTaskOperator reducer;
        public ReduceKeysTask<V> rights;
        public ReduceKeysTask<V> nextRight;

        public ReduceKeysTask(short EMPTY, BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ReduceKeysTask<V> nextRight, ShortReduceTaskOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.EMPTY = EMPTY;
            this.reducer = reducer;
        }

        @Override
        public final Short getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            ShortReduceTaskOperator reducer = this.reducer;
            if (reducer != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new ReduceKeysTask<V>(this.EMPTY, this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, reducer);
                    this.rights.fork();
                }
                boolean found = false;
                short r = this.EMPTY;
                while ((p = this.advance()) != null) {
                    short u = p.key;
                    if (!found) {
                        found = true;
                        r = u;
                        continue;
                    }
                    if (p.isEmpty()) continue;
                    found = true;
                    r = reducer.reduce(this.EMPTY, r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    ReduceKeysTask t = (ReduceKeysTask)c;
                    ReduceKeysTask<V> s = t.rights;
                    while (s != null) {
                        short sr = s.result;
                        if (sr != this.EMPTY) {
                            short tr = t.result;
                            t.result = tr == this.EMPTY ? sr : reducer.reduce(this.EMPTY, tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ShortReduceTaskOperator {
        public short reduce(short var1, short var2, short var3);
    }

    protected static final class MapReduceKeysTask<V, U>
    extends BulkTask<V, U> {
        public final ShortFunction<? extends U> transformer;
        public final BiFunction<? super U, ? super U, ? extends U> reducer;
        public U result;
        public MapReduceKeysTask<V, U> rights;
        public MapReduceKeysTask<V, U> nextRight;

        public MapReduceKeysTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceKeysTask<V, U> nextRight, ShortFunction<? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFunction<U, U, U> reducer;
            ShortFunction<U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysTask t = (MapReduceKeysTask)c;
                    MapReduceKeysTask<V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceKeysToDoubleTask<V>
    extends DoubleReturningBulkTask<V> {
        public final ShortToDoubleFunction transformer;
        public final DoubleBinaryOperator reducer;
        public final double basis;
        public MapReduceKeysToDoubleTask<V> rights;
        public MapReduceKeysToDoubleTask<V> nextRight;

        public MapReduceKeysToDoubleTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceKeysToDoubleTask<V> nextRight, ShortToDoubleFunction transformer, double basis, DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            DoubleBinaryOperator reducer;
            ShortToDoubleFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysToDoubleTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysToDoubleTask t = (MapReduceKeysToDoubleTask)c;
                    MapReduceKeysToDoubleTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ShortToDoubleFunction {
        public double applyAsDouble(short var1);
    }

    protected static final class MapReduceKeysToLongTask<V>
    extends LongReturningBulkTask<V> {
        public final ShortToLongFunction transformer;
        public final LongBinaryOperator reducer;
        public final long basis;
        public MapReduceKeysToLongTask<V> rights;
        public MapReduceKeysToLongTask<V> nextRight;

        public MapReduceKeysToLongTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceKeysToLongTask<V> nextRight, ShortToLongFunction transformer, long basis, LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            LongBinaryOperator reducer;
            ShortToLongFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysToLongTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.key));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysToLongTask t = (MapReduceKeysToLongTask)c;
                    MapReduceKeysToLongTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ShortToLongFunction {
        public long applyAsLong(short var1);
    }

    protected static final class MapReduceKeysToIntTask<V>
    extends IntReturningBulkTask<V> {
        public final ShortToIntFunction transformer;
        public final IntBinaryOperator reducer;
        public final int basis;
        public MapReduceKeysToIntTask<V> rights;
        public MapReduceKeysToIntTask<V> nextRight;

        public MapReduceKeysToIntTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceKeysToIntTask<V> nextRight, ShortToIntFunction transformer, int basis, IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            IntBinaryOperator reducer;
            ShortToIntFunction transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysToIntTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.key));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysToIntTask t = (MapReduceKeysToIntTask)c;
                    MapReduceKeysToIntTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public static interface ShortToIntFunction {
        public int applyAsInt(short var1);
    }

    protected static final class ForEachValueTask<V>
    extends BulkTask<V, Void> {
        public final Consumer<? super V> action;

        public ForEachValueTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, Consumer<? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            Consumer<V> action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachValueTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.accept(p.val);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class ForEachTransformedValueTask<V, U>
    extends BulkTask<V, Void> {
        public final Function<? super V, ? extends U> transformer;
        public final Consumer<? super U> action;

        public ForEachTransformedValueTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, Function<? super V, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Consumer<U> action;
            Function<V, U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedValueTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.val);
                    if (u == null) continue;
                    action.accept(u);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class SearchValuesTask<V, U>
    extends BulkTask<V, U> {
        public final Function<? super V, ? extends U> searchFunction;
        public final AtomicReference<U> result;

        public SearchValuesTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, Function<? super V, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            Function<V, U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchValuesTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p.val);
                    if (u == null) continue;
                    if (!result.compareAndSet(null, u)) break;
                    this.quietlyCompleteRoot();
                    break;
                }
            }
        }
    }

    protected static final class ReduceValuesTask<V>
    extends BulkTask<V, V> {
        public final BiFunction<? super V, ? super V, ? extends V> reducer;
        public V result;
        public ReduceValuesTask<V> rights;
        public ReduceValuesTask<V> nextRight;

        public ReduceValuesTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ReduceValuesTask<V> nextRight, BiFunction<? super V, ? super V, ? extends V> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        @Override
        public final V getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFunction<V, V, V> reducer = this.reducer;
            if (reducer != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new ReduceValuesTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    Object v = p.val;
                    r = r == null ? v : reducer.apply(r, v);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    ReduceValuesTask t = (ReduceValuesTask)c;
                    ReduceValuesTask<V> s = t.rights;
                    while (s != null) {
                        V sr = s.result;
                        if (sr != null) {
                            V tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceValuesTask<V, U>
    extends BulkTask<V, U> {
        public final Function<? super V, ? extends U> transformer;
        public final BiFunction<? super U, ? super U, ? extends U> reducer;
        public U result;
        public MapReduceValuesTask<V, U> rights;
        public MapReduceValuesTask<V, U> nextRight;

        public MapReduceValuesTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceValuesTask<V, U> nextRight, Function<? super V, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFunction<U, U, U> reducer;
            Function<V, U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.val);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesTask t = (MapReduceValuesTask)c;
                    MapReduceValuesTask<V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceValuesToDoubleTask<V>
    extends DoubleReturningBulkTask<V> {
        public final ToDoubleFunction<? super V> transformer;
        public final DoubleBinaryOperator reducer;
        public final double basis;
        public MapReduceValuesToDoubleTask<V> rights;
        public MapReduceValuesToDoubleTask<V> nextRight;

        public MapReduceValuesToDoubleTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceValuesToDoubleTask<V> nextRight, ToDoubleFunction<? super V> transformer, double basis, DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            DoubleBinaryOperator reducer;
            ToDoubleFunction<V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesToDoubleTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesToDoubleTask t = (MapReduceValuesToDoubleTask)c;
                    MapReduceValuesToDoubleTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceValuesToLongTask<V>
    extends LongReturningBulkTask<V> {
        public final ToLongFunction<? super V> transformer;
        public final LongBinaryOperator reducer;
        public final long basis;
        public MapReduceValuesToLongTask<V> rights;
        public MapReduceValuesToLongTask<V> nextRight;

        public MapReduceValuesToLongTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceValuesToLongTask<V> nextRight, ToLongFunction<? super V> transformer, long basis, LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            LongBinaryOperator reducer;
            ToLongFunction<V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesToLongTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesToLongTask t = (MapReduceValuesToLongTask)c;
                    MapReduceValuesToLongTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceValuesToIntTask<V>
    extends IntReturningBulkTask<V> {
        public final ToIntFunction<? super V> transformer;
        public final IntBinaryOperator reducer;
        public final int basis;
        public MapReduceValuesToIntTask<V> rights;
        public MapReduceValuesToIntTask<V> nextRight;

        public MapReduceValuesToIntTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceValuesToIntTask<V> nextRight, ToIntFunction<? super V> transformer, int basis, IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            IntBinaryOperator reducer;
            ToIntFunction<V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesToIntTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesToIntTask t = (MapReduceValuesToIntTask)c;
                    MapReduceValuesToIntTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class ForEachEntryTask<V>
    extends BulkTask<V, Void> {
        public final Consumer<? super Entry<V>> action;

        public ForEachEntryTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, Consumer<? super Entry<V>> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            Consumer<Entry<V>> action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachEntryTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.accept(p);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class ForEachTransformedEntryTask<V, U>
    extends BulkTask<V, Void> {
        public final Function<Entry<V>, ? extends U> transformer;
        public final Consumer<? super U> action;

        public ForEachTransformedEntryTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, Function<Entry<V>, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Consumer<U> action;
            Function<Entry<V>, U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedEntryTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p);
                    if (u == null) continue;
                    action.accept(u);
                }
                this.propagateCompletion();
            }
        }
    }

    protected static final class SearchEntriesTask<V, U>
    extends BulkTask<V, U> {
        public final Function<Entry<V>, ? extends U> searchFunction;
        public final AtomicReference<U> result;

        public SearchEntriesTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, Function<Entry<V>, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            Function<Entry<V>, U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchEntriesTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p);
                    if (u == null) continue;
                    if (result.compareAndSet(null, u)) {
                        this.quietlyCompleteRoot();
                    }
                    return;
                }
            }
        }
    }

    protected static final class ReduceEntriesTask<V>
    extends BulkTask<V, Entry<V>> {
        public final BiFunction<Entry<V>, Entry<V>, ? extends Entry<V>> reducer;
        public Entry<V> result;
        public ReduceEntriesTask<V> rights;
        public ReduceEntriesTask<V> nextRight;

        public ReduceEntriesTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, ReduceEntriesTask<V> nextRight, BiFunction<Entry<V>, Entry<V>, ? extends Entry<V>> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        @Override
        public final Entry<V> getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFunction<Entry<V>, Entry<V>, Entry<V>> reducer = this.reducer;
            if (reducer != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new ReduceEntriesTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, reducer);
                    this.rights.fork();
                }
                Node r = null;
                while ((p = this.advance()) != null) {
                    r = r == null ? p : reducer.apply(r, p);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    ReduceEntriesTask t = (ReduceEntriesTask)c;
                    ReduceEntriesTask<V> s = t.rights;
                    while (s != null) {
                        Entry<V> sr = s.result;
                        if (sr != null) {
                            Entry<V> tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    public static interface Entry<V>
    extends Short2ObjectMap.Entry<V> {
        public boolean isEmpty();

        @Override
        @Deprecated
        public Short getKey();

        @Override
        public short getShortKey();

        @Override
        public V getValue();

        @Override
        public int hashCode();

        public String toString();

        @Override
        public boolean equals(Object var1);

        @Override
        public V setValue(V var1);
    }

    protected static final class MapReduceEntriesTask<V, U>
    extends BulkTask<V, U> {
        public final Function<Entry<V>, ? extends U> transformer;
        public final BiFunction<? super U, ? super U, ? extends U> reducer;
        public U result;
        public MapReduceEntriesTask<V, U> rights;
        public MapReduceEntriesTask<V, U> nextRight;

        public MapReduceEntriesTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceEntriesTask<V, U> nextRight, Function<Entry<V>, ? extends U> transformer, BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFunction<U, U, U> reducer;
            Function<Entry<V>, U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesTask<V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesTask t = (MapReduceEntriesTask)c;
                    MapReduceEntriesTask<V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceEntriesToDoubleTask<V>
    extends DoubleReturningBulkTask<V> {
        public final ToDoubleFunction<Entry<V>> transformer;
        public final DoubleBinaryOperator reducer;
        public final double basis;
        public MapReduceEntriesToDoubleTask<V> rights;
        public MapReduceEntriesToDoubleTask<V> nextRight;

        public MapReduceEntriesToDoubleTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceEntriesToDoubleTask<V> nextRight, ToDoubleFunction<Entry<V>> transformer, double basis, DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            DoubleBinaryOperator reducer;
            ToDoubleFunction<Entry<V>> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesToDoubleTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesToDoubleTask t = (MapReduceEntriesToDoubleTask)c;
                    MapReduceEntriesToDoubleTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceEntriesToLongTask<V>
    extends LongReturningBulkTask<V> {
        public final ToLongFunction<Entry<V>> transformer;
        public final LongBinaryOperator reducer;
        public final long basis;
        public MapReduceEntriesToLongTask<V> rights;
        public MapReduceEntriesToLongTask<V> nextRight;

        public MapReduceEntriesToLongTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceEntriesToLongTask<V> nextRight, ToLongFunction<Entry<V>> transformer, long basis, LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            LongBinaryOperator reducer;
            ToLongFunction<Entry<V>> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesToLongTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesToLongTask t = (MapReduceEntriesToLongTask)c;
                    MapReduceEntriesToLongTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static final class MapReduceEntriesToIntTask<V>
    extends IntReturningBulkTask<V> {
        public final ToIntFunction<Entry<V>> transformer;
        public final IntBinaryOperator reducer;
        public final int basis;
        public MapReduceEntriesToIntTask<V> rights;
        public MapReduceEntriesToIntTask<V> nextRight;

        public MapReduceEntriesToIntTask(BulkTask<V, ?> p, int b, int i, int f, Node<V>[] t, MapReduceEntriesToIntTask<V> nextRight, ToIntFunction<Entry<V>> transformer, int basis, IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void compute() {
            IntBinaryOperator reducer;
            ToIntFunction<Entry<V>> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesToIntTask<V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesToIntTask t = (MapReduceEntriesToIntTask)c;
                    MapReduceEntriesToIntTask<V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    protected static abstract class DoubleReturningBulkTask<V>
    extends BulkTask<V, Double> {
        public double result;

        public DoubleReturningBulkTask(BulkTask<V, ?> par, int b, int i, int f, Node<V>[] t) {
            super(par, b, i, f, t);
        }

        protected double invoke0() {
            this.quietlyInvoke();
            Throwable exc = this.getException();
            if (exc != null) {
                throw SneakyThrow.sneakyThrow(exc);
            }
            return this.result;
        }
    }

    protected static abstract class IntReturningBulkTask<V>
    extends BulkTask<V, Integer> {
        public int result;

        public IntReturningBulkTask(BulkTask<V, ?> par, int b, int i, int f, Node<V>[] t) {
            super(par, b, i, f, t);
        }

        protected int invoke0() {
            this.quietlyInvoke();
            Throwable exc = this.getException();
            if (exc != null) {
                throw SneakyThrow.sneakyThrow(exc);
            }
            return this.result;
        }
    }

    protected static abstract class LongReturningBulkTask<V>
    extends BulkTask<V, Long> {
        public long result;

        public LongReturningBulkTask(BulkTask<V, ?> par, int b, int i, int f, Node<V>[] t) {
            super(par, b, i, f, t);
        }

        protected long invoke0() {
            this.quietlyInvoke();
            Throwable exc = this.getException();
            if (exc != null) {
                throw SneakyThrow.sneakyThrow(exc);
            }
            return this.result;
        }
    }

    protected static abstract class ShortReturningBulkTask2<V>
    extends BulkTask<V, Short> {
        public short result;

        public ShortReturningBulkTask2(BulkTask<V, ?> par, int b, int i, int f, Node<V>[] t) {
            super(par, b, i, f, t);
        }

        protected short invoke0() {
            this.quietlyInvoke();
            Throwable exc = this.getException();
            if (exc != null) {
                throw SneakyThrow.sneakyThrow(exc);
            }
            return this.result;
        }
    }

    protected static abstract class CollectionView<K, E>
    implements ObjectCollection<E>,
    Serializable {
        public static final long serialVersionUID = 7249069246763182397L;
        public final Short2ObjectConcurrentHashMap<K> map;
        protected static final String oomeMsg = "Required array size too large";

        public CollectionView(Short2ObjectConcurrentHashMap<K> map) {
            this.map = map;
        }

        public Short2ObjectConcurrentHashMap<K> getMap() {
            return this.map;
        }

        @Override
        public final void clear() {
            this.map.clear();
        }

        @Override
        public final int size() {
            return this.map.size();
        }

        @Override
        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public abstract ObjectIterator<E> iterator();

        @Override
        public abstract boolean contains(Object var1);

        @Override
        public abstract boolean remove(Object var1);

        @Override
        public final Object[] toArray() {
            long sz = this.map.mappingCount();
            if (sz > 0x7FFFFFF7L) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int n = (int)sz;
            Object[] r = new Object[n];
            int i = 0;
            for (Object e : this) {
                if (i == n) {
                    if (n >= 0x7FFFFFF7) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    n = n >= 0x3FFFFFFB ? 0x7FFFFFF7 : (n += (n >>> 1) + 1);
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        @Override
        public final <T> T[] toArray(T[] a) {
            long sz = this.map.mappingCount();
            if (sz > 0x7FFFFFF7L) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int m = (int)sz;
            T[] r = a.length >= m ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = 0;
            for (Object e : this) {
                if (i == n) {
                    if (n >= 0x7FFFFFF7) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    n = n >= 0x3FFFFFFB ? 0x7FFFFFF7 : (n += (n >>> 1) + 1);
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            if (a == r && i < n) {
                r[i] = null;
                return r;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator it = this.iterator();
            if (it.hasNext()) {
                while (true) {
                    Object e;
                    sb.append((Object)((e = it.next()) == this ? "(this Collection)" : e));
                    if (!it.hasNext()) break;
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }

        @Override
        public final boolean containsAll(Collection<?> c) {
            if (c != this) {
                for (Object e : c) {
                    if (e != null && this.contains(e)) continue;
                    return false;
                }
            }
            return true;
        }

        @Override
        public final boolean removeAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException();
            }
            boolean modified = false;
            Iterator it = this.iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public final boolean retainAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException();
            }
            boolean modified = false;
            Iterator it = this.iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }
    }

    protected static final class EntrySpliterator<V>
    extends Traverser<V>
    implements ObjectSpliterator<Short2ObjectMap.Entry<V>> {
        public final Short2ObjectConcurrentHashMap<V> map;
        public long est;

        public EntrySpliterator(Node<V>[] tab, int size, int index, int limit, long est, Short2ObjectConcurrentHashMap<V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.est = est;
        }

        @Override
        public ObjectSpliterator<Short2ObjectMap.Entry<V>> trySplit() {
            EntrySpliterator<V> entrySpliterator;
            int i = this.baseIndex;
            int f = this.baseLimit;
            int h = i + f >>> 1;
            if (h <= i) {
                entrySpliterator = null;
            } else {
                this.baseLimit = h;
                EntrySpliterator<V> entrySpliterator2 = new EntrySpliterator<V>(this.tab, this.baseSize, this.baseLimit, f, this.est >>>= 1, this.map);
                entrySpliterator = entrySpliterator2;
            }
            return entrySpliterator;
        }

        @Override
        public void forEachRemaining(Consumer<? super Short2ObjectMap.Entry<V>> action) {
            Node p;
            if (action == null) {
                throw new NullPointerException();
            }
            while ((p = this.advance()) != null) {
                action.accept(new MapEntry(p.isEmpty(), p.key, p.val, this.map));
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super Short2ObjectMap.Entry<V>> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node p = this.advance();
            if (p == null) {
                return false;
            }
            action.accept(new MapEntry(p.isEmpty(), p.key, p.val, this.map));
            return true;
        }

        @Override
        public long estimateSize() {
            return this.est;
        }

        @Override
        public int characteristics() {
            return 4353;
        }
    }

    protected static final class ValueSpliterator<V>
    extends Traverser<V>
    implements ObjectSpliterator<V> {
        public long est;

        public ValueSpliterator(Node<V>[] tab, int size, int index, int limit, long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        @Override
        public ObjectSpliterator<V> trySplit() {
            ValueSpliterator<V> valueSpliterator;
            int i = this.baseIndex;
            int f = this.baseLimit;
            int h = i + f >>> 1;
            if (h <= i) {
                valueSpliterator = null;
            } else {
                this.baseLimit = h;
                ValueSpliterator<V> valueSpliterator2 = new ValueSpliterator<V>(this.tab, this.baseSize, this.baseLimit, f, this.est >>>= 1);
                valueSpliterator = valueSpliterator2;
            }
            return valueSpliterator;
        }

        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            Node p;
            if (action == null) {
                throw new NullPointerException();
            }
            while ((p = this.advance()) != null) {
                action.accept(p.val);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super V> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node p = this.advance();
            if (p == null) {
                return false;
            }
            action.accept(p.val);
            return true;
        }

        @Override
        public long estimateSize() {
            return this.est;
        }

        @Override
        public int characteristics() {
            return 4352;
        }
    }

    protected static final class KeySpliterator<V>
    extends Traverser<V>
    implements ShortSpliterator {
        public long est;

        public KeySpliterator(Node<V>[] tab, int size, int index, int limit, long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        @Override
        public ShortSpliterator trySplit() {
            KeySpliterator<V> keySpliterator;
            int i = this.baseIndex;
            int f = this.baseLimit;
            int h = i + f >>> 1;
            if (h <= i) {
                keySpliterator = null;
            } else {
                this.baseLimit = h;
                KeySpliterator<V> keySpliterator2 = new KeySpliterator<V>(this.tab, this.baseSize, this.baseLimit, f, this.est >>>= 1);
                keySpliterator = keySpliterator2;
            }
            return keySpliterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Short> action) {
            if (action instanceof ShortConsumer) {
                return this.tryAdvance((ShortConsumer)action);
            }
            return this.tryAdvance((short value) -> action.accept(value));
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            Node p;
            if (action == null) {
                throw new NullPointerException();
            }
            while ((p = this.advance()) != null) {
                action.accept(p.key);
            }
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node p = this.advance();
            if (p == null) {
                return false;
            }
            action.accept(p.key);
            return true;
        }

        @Override
        public long estimateSize() {
            return this.est;
        }

        @Override
        public int characteristics() {
            return 4353;
        }
    }

    protected static final class MapEntry<V>
    implements Entry<V> {
        public final boolean empty;
        public final short key;
        public V val;
        public final Short2ObjectConcurrentHashMap<V> map;

        public MapEntry(boolean empty, short key, V val, Short2ObjectConcurrentHashMap<V> map) {
            this.empty = empty;
            this.key = key;
            this.val = val;
            this.map = map;
        }

        @Override
        public boolean isEmpty() {
            return this.empty;
        }

        @Override
        public Short getKey() {
            return this.key;
        }

        @Override
        public short getShortKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.val;
        }

        @Override
        public String toString() {
            if (this.empty) {
                return "EMPTY=" + String.valueOf(this.val);
            }
            return this.key + "=" + String.valueOf(this.val);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Entry) {
                if (this.empty != ((Entry)o).isEmpty()) {
                    return false;
                }
                if (!this.empty && this.key != ((Entry)o).getShortKey()) {
                    return false;
                }
                return this.val.equals(((Entry)o).getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = this.empty ? 1 : 0;
            result = 31 * result + Short.hashCode(this.key);
            result = 31 * result + this.val.hashCode();
            return result;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = this.val;
            this.val = value;
            this.map.put(this.key, value);
            return v;
        }
    }

    protected static final class EntryIterator<V>
    extends BaseIterator<V>
    implements ObjectIterator<Short2ObjectMap.Entry<V>> {
        public EntryIterator(Node<V>[] tab, int index, int size, int limit, Short2ObjectConcurrentHashMap<V> map) {
            super(tab, index, size, limit, map);
        }

        @Override
        public final Entry<V> next() {
            Node p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            short k = p.key;
            Object v = p.val;
            this.lastReturned = p;
            this.advance();
            return new MapEntry(p.isEmpty(), k, v, this.map);
        }
    }

    protected static final class ValueIterator<V>
    extends BaseIterator<V>
    implements ObjectIterator<V>,
    Enumeration<V> {
        public ValueIterator(Node<V>[] tab, int index, int size, int limit, Short2ObjectConcurrentHashMap<V> map) {
            super(tab, index, size, limit, map);
        }

        @Override
        public final V next() {
            Node p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            Object v = p.val;
            this.lastReturned = p;
            this.advance();
            return v;
        }

        @Override
        public final V nextElement() {
            return this.next();
        }
    }

    protected static final class KeyIterator<V>
    implements ShortIterator {
        public Node<V>[] tab;
        public Node<V> next;
        public TableStack<V> stack;
        public TableStack<V> spare;
        public int index;
        public int baseIndex;
        public int baseLimit;
        public final int baseSize;
        public final Short2ObjectConcurrentHashMap<V> map;
        public Node<V> lastReturned;

        public KeyIterator(Node<V>[] tab, int size, int index, int limit, Short2ObjectConcurrentHashMap<V> map) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
            this.map = map;
            this.advance();
        }

        protected final Node<V> advance() {
            Node<V> e = this.next;
            if (e != null) {
                e = e.next;
            }
            while (true) {
                int i;
                int n;
                Node<V>[] t;
                block10: {
                    block9: {
                        if (e != null) {
                            this.next = e;
                            return this.next;
                        }
                        if (this.baseIndex >= this.baseLimit) break block9;
                        t = this.tab;
                        if (this.tab != null && (n = t.length) > (i = this.index) && i >= 0) break block10;
                    }
                    this.next = null;
                    return null;
                }
                e = Short2ObjectConcurrentHashMap.tabAt(t, i);
                if (e != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        this.tab = ((ForwardingNode)e).nextTable;
                        e = null;
                        this.pushState(t, i, n);
                        continue;
                    }
                    e = e instanceof TreeBin ? ((TreeBin)e).first : null;
                }
                if (this.stack != null) {
                    this.recoverState(n);
                    continue;
                }
                this.index = i + this.baseSize;
                if (this.index < n) continue;
                this.index = ++this.baseIndex;
            }
        }

        protected void pushState(Node<V>[] t, int i, int n) {
            TableStack<V> s = this.spare;
            if (s != null) {
                this.spare = s.next;
            } else {
                s = new TableStack();
            }
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = this.stack;
            this.stack = s;
        }

        protected void recoverState(int n) {
            int len;
            TableStack<V> s;
            while ((s = this.stack) != null && (this.index += (len = s.length)) >= n) {
                n = len;
                this.index = s.index;
                this.tab = s.tab;
                s.tab = null;
                TableStack next = s.next;
                s.next = this.spare;
                this.stack = next;
                this.spare = s;
            }
            if (s == null && (this.index += this.baseSize) >= n) {
                this.index = ++this.baseIndex;
            }
        }

        @Override
        public final boolean hasNext() {
            return this.next != null;
        }

        public final boolean hasMoreElements() {
            return this.next != null;
        }

        @Override
        public final void remove() {
            Node<V> p = this.lastReturned;
            if (p == null) {
                throw new IllegalStateException();
            }
            this.lastReturned = null;
            this.map.replaceNode(p.key, null, null);
        }

        @Override
        public final short nextShort() {
            Node<V> p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            short k = p.key;
            this.lastReturned = p;
            this.advance();
            return k;
        }
    }

    protected static class BaseIterator<V>
    extends Traverser<V> {
        public final Short2ObjectConcurrentHashMap<V> map;
        public Node<V> lastReturned;

        public BaseIterator(Node<V>[] tab, int size, int index, int limit, Short2ObjectConcurrentHashMap<V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.advance();
        }

        public final boolean hasNext() {
            return this.next != null;
        }

        public final boolean hasMoreElements() {
            return this.next != null;
        }

        public final void remove() {
            Node<V> p = this.lastReturned;
            if (p == null) {
                throw new IllegalStateException();
            }
            this.lastReturned = null;
            this.map.replaceNode(p.key, null, null);
        }
    }

    @FunctionalInterface
    public static interface ToShortFunction<T> {
        public short applyAsShort(T var1);
    }

    protected static class Segment<V>
    extends ReentrantLock
    implements Serializable {
        public static final long serialVersionUID = 2249069246763182397L;
        public final float loadFactor;

        public Segment(float lf) {
            this.loadFactor = lf;
        }
    }
}

