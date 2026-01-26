/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ISentryLifecycleToken;
import io.sentry.SynchronizedCollection;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;

final class SynchronizedQueue<E>
extends SynchronizedCollection<E>
implements Queue<E> {
    private static final long serialVersionUID = 1L;

    static <E> SynchronizedQueue<E> synchronizedQueue(Queue<E> queue) {
        return new SynchronizedQueue<E>(queue);
    }

    private SynchronizedQueue(Queue<E> queue) {
        super(queue);
    }

    protected SynchronizedQueue(Queue<E> queue, AutoClosableReentrantLock lock) {
        super(queue, lock);
    }

    @Override
    protected Queue<E> decorated() {
        return (Queue)super.decorated();
    }

    @Override
    public E element() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object e = this.decorated().element();
            return e;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = ((Object)this.decorated()).equals(object);
            return bl;
        }
    }

    @Override
    public int hashCode() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            int n = ((Object)this.decorated()).hashCode();
            return n;
        }
    }

    @Override
    public boolean offer(E e) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            boolean bl = this.decorated().offer(e);
            return bl;
        }
    }

    @Override
    public E peek() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object e = this.decorated().peek();
            return e;
        }
    }

    @Override
    public E poll() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object e = this.decorated().poll();
            return e;
        }
    }

    @Override
    public E remove() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            Object e = this.decorated().remove();
            return e;
        }
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
}

