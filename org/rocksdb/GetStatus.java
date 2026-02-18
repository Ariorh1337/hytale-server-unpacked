/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Status;

public class GetStatus {
    public final Status status;
    public final int requiredSize;

    GetStatus(Status status, int n) {
        this.status = status;
        this.requiredSize = n;
    }

    static GetStatus fromStatusCode(Status.Code code, int n) {
        return new GetStatus(new Status(code, Status.SubCode.getSubCode((byte)0), null), n);
    }
}

