package org.rocksdb;

interface TransactionalOptions<T extends TransactionalOptions<T>> extends AutoCloseable {
   boolean isSetSnapshot();

   T setSetSnapshot(boolean var1);
}
