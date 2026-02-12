package org.rocksdb;

interface TransactionalDB<T extends TransactionalOptions<T>> extends AutoCloseable {
   Transaction beginTransaction(WriteOptions var1);

   Transaction beginTransaction(WriteOptions var1, T var2);

   Transaction beginTransaction(WriteOptions var1, Transaction var2);

   Transaction beginTransaction(WriteOptions var1, T var2, Transaction var3);
}
