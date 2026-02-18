/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public abstract class TablePropertiesCollectorFactory
extends RocksObject {
    private TablePropertiesCollectorFactory(long l) {
        super(l);
    }

    public static TablePropertiesCollectorFactory NewCompactOnDeletionCollectorFactory(long l, long l2, double d) {
        long l3 = TablePropertiesCollectorFactory.newCompactOnDeletionCollectorFactory(l, l2, d);
        return new TablePropertiesCollectorFactory(l3){

            @Override
            protected void disposeInternal(long l) {
                TablePropertiesCollectorFactory.deleteCompactOnDeletionCollectorFactory(l);
            }
        };
    }

    static TablePropertiesCollectorFactory newWrapper(long l) {
        return new TablePropertiesCollectorFactory(l){

            @Override
            protected void disposeInternal(long l) {
                TablePropertiesCollectorFactory.deleteCompactOnDeletionCollectorFactory(l);
            }
        };
    }

    private static native long newCompactOnDeletionCollectorFactory(long var0, long var2, double var4);

    private static native void deleteCompactOnDeletionCollectorFactory(long var0);
}

