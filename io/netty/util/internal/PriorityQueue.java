/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import java.util.Queue;

public interface PriorityQueue<T>
extends Queue<T> {
    public boolean removeTyped(T var1);

    public boolean containsTyped(T var1);

    public void priorityChanged(T var1);

    public void clearIgnoringIndexes();
}

