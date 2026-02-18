/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Transaction;
import org.rocksdb.TransactionalOptions;
import org.rocksdb.WriteOptions;

interface TransactionalDB<T extends TransactionalOptions<T>>
extends AutoCloseable {
    public Transaction beginTransaction(WriteOptions var1);

    public Transaction beginTransaction(WriteOptions var1, T var2);

    public Transaction beginTransaction(WriteOptions var1, Transaction var2);

    public Transaction beginTransaction(WriteOptions var1, T var2, Transaction var3);
}

