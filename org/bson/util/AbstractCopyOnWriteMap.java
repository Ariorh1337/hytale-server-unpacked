/*
 * Decompiled with CFR 0.152.
 */
package org.bson.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.bson.assertions.Assertions;

abstract class AbstractCopyOnWriteMap<K, V, M extends Map<K, V>>
implements ConcurrentMap<K, V> {
    private volatile M delegate;
    private final transient Lock lock = new ReentrantLock();
    private final View<K, V> view;

    protected <N extends Map<? extends K, ? extends V>> AbstractCopyOnWriteMap(N map, View.Type viewType) {
        this.delegate = (Map)Assertions.notNull("delegate", this.copy(Assertions.notNull("map", map)));
        this.view = Assertions.notNull("viewType", viewType).get(this);
    }

    abstract <N extends Map<? extends K, ? extends V>> M copy(N var1);

    @Override
    public final void clear() {
        this.lock.lock();
        try {
            this.set(this.copy(Collections.emptyMap()));
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final V remove(Object key) {
        this.lock.lock();
        try {
            Object v;
            if (!this.delegate.containsKey(key)) {
                V v2 = null;
                return v2;
            }
            M map = this.copy();
            try {
                v = map.remove(key);
                this.set(map);
            }
            catch (Throwable throwable) {
                this.set(map);
                throw throwable;
            }
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object key, Object value) {
        this.lock.lock();
        try {
            if (this.delegate.containsKey(key) && this.equals(value, this.delegate.get(key))) {
                M map = this.copy();
                map.remove(key);
                this.set(map);
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.lock.lock();
        try {
            if (!this.delegate.containsKey(key) || !this.equals(oldValue, this.delegate.get(key))) {
                boolean bl = false;
                return bl;
            }
            M map = this.copy();
            map.put(key, newValue);
            this.set(map);
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V replace(K key, V value) {
        this.lock.lock();
        try {
            V v;
            if (!this.delegate.containsKey(key)) {
                V v2 = null;
                return v2;
            }
            M map = this.copy();
            try {
                v = map.put(key, value);
                this.set(map);
            }
            catch (Throwable throwable) {
                this.set(map);
                throw throwable;
            }
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final V put(K key, V value) {
        this.lock.lock();
        try {
            V v;
            M map = this.copy();
            try {
                v = map.put(key, value);
                this.set(map);
            }
            catch (Throwable throwable) {
                this.set(map);
                throw throwable;
            }
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V putIfAbsent(K key, V value) {
        this.lock.lock();
        if (!this.delegate.containsKey(key)) {
            M map = this.copy();
            V v = map.put(key, value);
            return v;
            finally {
                this.set(map);
            }
        }
        Object v = this.delegate.get(key);
        return v;
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> t) {
        this.lock.lock();
        try {
            M map = this.copy();
            map.putAll(t);
            this.set(map);
        }
        finally {
            this.lock.unlock();
        }
    }

    protected M copy() {
        this.lock.lock();
        try {
            M m = this.copy((Map)this.delegate);
            return m;
        }
        finally {
            this.lock.unlock();
        }
    }

    protected void set(M map) {
        this.delegate = map;
    }

    @Override
    public final Set<Map.Entry<K, V>> entrySet() {
        return this.view.entrySet();
    }

    @Override
    public final Set<K> keySet() {
        return this.view.keySet();
    }

    @Override
    public final Collection<V> values() {
        return this.view.values();
    }

    @Override
    public final boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public final boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public final V get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public final boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public final int size() {
        return this.delegate.size();
    }

    @Override
    public final boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    @Override
    public final int hashCode() {
        return this.delegate.hashCode();
    }

    protected final M getDelegate() {
        return this.delegate;
    }

    public String toString() {
        return this.delegate.toString();
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    final class Mutable
    extends View<K, V> {
        private final transient KeySet keySet;
        private final transient EntrySet entrySet;
        private final transient Values values;

        Mutable() {
            this.keySet = new KeySet();
            this.entrySet = new EntrySet();
            this.values = new Values();
        }

        @Override
        public Set<K> keySet() {
            return this.keySet;
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return this.entrySet;
        }

        @Override
        public Collection<V> values() {
            return this.values;
        }
    }

    final class Immutable
    extends View<K, V> {
        Immutable() {
        }

        @Override
        public Set<K> keySet() {
            return Collections.unmodifiableSet(AbstractCopyOnWriteMap.this.delegate.keySet());
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return Collections.unmodifiableSet(AbstractCopyOnWriteMap.this.delegate.entrySet());
        }

        @Override
        public Collection<V> values() {
            return Collections.unmodifiableCollection(AbstractCopyOnWriteMap.this.delegate.values());
        }
    }

    public static abstract class View<K, V> {
        View() {
        }

        abstract Set<K> keySet();

        abstract Set<Map.Entry<K, V>> entrySet();

        abstract Collection<V> values();

        public static enum Type {
            STABLE{

                @Override
                <K, V, M extends Map<K, V>> View<K, V> get(AbstractCopyOnWriteMap<K, V, M> host) {
                    return host.new Immutable();
                }
            }
            ,
            LIVE{

                @Override
                <K, V, M extends Map<K, V>> View<K, V> get(AbstractCopyOnWriteMap<K, V, M> host) {
                    return host.new Mutable();
                }
            };


            abstract <K, V, M extends Map<K, V>> View<K, V> get(AbstractCopyOnWriteMap<K, V, M> var1);
        }
    }

    protected static abstract class CollectionView<E>
    implements Collection<E> {
        protected CollectionView() {
        }

        abstract Collection<E> getDelegate();

        @Override
        public final boolean contains(Object o) {
            return this.getDelegate().contains(o);
        }

        @Override
        public final boolean containsAll(Collection<?> c) {
            return this.getDelegate().containsAll(c);
        }

        @Override
        public final Iterator<E> iterator() {
            return new UnmodifiableIterator<E>(this.getDelegate().iterator());
        }

        @Override
        public final boolean isEmpty() {
            return this.getDelegate().isEmpty();
        }

        @Override
        public final int size() {
            return this.getDelegate().size();
        }

        @Override
        public final Object[] toArray() {
            return this.getDelegate().toArray();
        }

        @Override
        public final <T> T[] toArray(T[] a) {
            return this.getDelegate().toArray(a);
        }

        @Override
        public int hashCode() {
            return this.getDelegate().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.getDelegate().equals(obj);
        }

        public String toString() {
            return this.getDelegate().toString();
        }

        @Override
        public final boolean add(E o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }
    }

    private static class UnmodifiableIterator<T>
    implements Iterator<T> {
        private final Iterator<T> delegate;

        UnmodifiableIterator(Iterator<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public T next() {
            return this.delegate.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class EntrySet
    extends CollectionView<Map.Entry<K, V>>
    implements Set<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override
        Collection<Map.Entry<K, V>> getDelegate() {
            return AbstractCopyOnWriteMap.this.delegate.entrySet();
        }

        @Override
        public void clear() {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                map.entrySet().clear();
                AbstractCopyOnWriteMap.this.set(map);
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object o) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                if (!this.contains(o)) {
                    boolean bl = false;
                    return bl;
                }
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.entrySet().remove(o);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.entrySet().removeAll(c);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.entrySet().retainAll(c);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }
    }

    private final class Values
    extends CollectionView<V> {
        private Values() {
        }

        @Override
        Collection<V> getDelegate() {
            return AbstractCopyOnWriteMap.this.delegate.values();
        }

        @Override
        public void clear() {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                map.values().clear();
                AbstractCopyOnWriteMap.this.set(map);
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object o) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                if (!this.contains(o)) {
                    boolean bl = false;
                    return bl;
                }
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.values().remove(o);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.values().removeAll(c);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.values().retainAll(c);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }
    }

    private class KeySet
    extends CollectionView<K>
    implements Set<K> {
        private KeySet() {
        }

        @Override
        Collection<K> getDelegate() {
            return AbstractCopyOnWriteMap.this.delegate.keySet();
        }

        @Override
        public void clear() {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                map.keySet().clear();
                AbstractCopyOnWriteMap.this.set(map);
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        @Override
        public boolean remove(Object o) {
            return AbstractCopyOnWriteMap.this.remove(o) != null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.keySet().removeAll(c);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            AbstractCopyOnWriteMap.this.lock.lock();
            try {
                Object map = AbstractCopyOnWriteMap.this.copy();
                try {
                    boolean bl = map.keySet().retainAll(c);
                    AbstractCopyOnWriteMap.this.set(map);
                    return bl;
                }
                catch (Throwable throwable) {
                    AbstractCopyOnWriteMap.this.set(map);
                    throw throwable;
                }
            }
            finally {
                AbstractCopyOnWriteMap.this.lock.unlock();
            }
        }
    }
}

