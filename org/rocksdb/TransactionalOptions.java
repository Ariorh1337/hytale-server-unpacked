/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

interface TransactionalOptions<T extends TransactionalOptions<T>>
extends AutoCloseable {
    public boolean isSetSnapshot();

    public T setSetSnapshot(boolean var1);
}

