/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Status;

public class RocksDBException
extends Exception {
    private static final long serialVersionUID = -5187634878466267120L;
    private final Status status;

    public RocksDBException(String string) {
        this(string, (Status)null);
    }

    public RocksDBException(String string, Status status) {
        super(string);
        this.status = status;
    }

    public RocksDBException(Status status) {
        super(status.getState() != null ? status.getState() : status.getCodeString());
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }
}

