/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.TableProperties;

public interface TableFilter {
    public boolean filter(TableProperties var1);
}

