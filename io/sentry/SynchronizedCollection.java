/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ISentryLifecycleToken;
import io.sentry.util.AutoClosableReentrantLock;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

class SynchronizedCollection<E>
implements Collection<E>,
Serializable {
    private static final long serialVersionUID = 2412805092710877986L;
    private final Collection<E> collection;
    final AutoClosableReentrantLock lock;

    public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> coll) {
        return new SynchronizedCollection<T>(coll);
    }

    SynchronizedCollection(Collection<E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        this.collection = collection;
        this.lock = new AutoClosableReentrantLock();
    }

    SynchronizedCollection(Collection<E> collection, AutoClosableReentrantLock lock) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        if (lock == null) {
            throw new NullPointerException("Lock must not be null.");
        }
        this.collection = collection;
        this.lock = lock;
    }

    protected Collection<E> decorated() {
        return this.collection;
    }

    @Override
    public boolean add(E object) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().add(object);
            return bl;
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().addAll(coll);
            return bl;
        }
    }

    @Override
    public void clear() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            this.decorated().clear();
        }
    }

    @Override
    public boolean contains(Object object) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().contains(object);
            return bl;
        }
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().containsAll(coll);
            return bl;
        }
    }

    @Override
    public boolean isEmpty() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().isEmpty();
            return bl;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return this.decorated().iterator();
    }

    @Override
    public Object[] toArray() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object[] objectArray = this.decorated().toArray();
            return objectArray;
        }
    }

    @Override
    public <T> T[] toArray(T[] object) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            T[] TArray = this.decorated().toArray(object);
            return TArray;
        }
    }

    @Override
    public boolean remove(Object object) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().remove(object);
            return bl;
        }
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().removeAll(coll);
            return bl;
        }
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().retainAll(coll);
            return bl;
        }
    }

    @Override
    public int size() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            int n = this.decorated().size();
            return n;
        }
    }

    @Override
    public boolean equals(Object object) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            if (object == this) {
                boolean bl = true;
                return bl;
            }
            boolean bl = object == this || this.decorated().equals(object);
            return bl;
        }
    }

    @Override
    public int hashCode() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            int n = this.decorated().hashCode();
            return n;
        }
    }

    public String toString() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            String string = this.decorated().toString();
            return string;
        }
    }
}

