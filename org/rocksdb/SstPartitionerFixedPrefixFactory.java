/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.SstPartitionerFactory;

public class SstPartitionerFixedPrefixFactory
extends SstPartitionerFactory {
    public SstPartitionerFixedPrefixFactory(long l) {
        super(SstPartitionerFixedPrefixFactory.newSstPartitionerFixedPrefixFactory0(l));
    }

    private static native long newSstPartitionerFixedPrefixFactory0(long var0);

    @Override
    protected final void disposeInternal(long l) {
        SstPartitionerFixedPrefixFactory.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

