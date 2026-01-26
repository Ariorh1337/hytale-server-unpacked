/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

import org.bouncycastle.tsp.TSPException;

public class TSPValidationException
extends TSPException {
    private int failureCode = -1;

    public TSPValidationException(String string) {
        super(string);
    }

    public TSPValidationException(String string, int n) {
        super(string);
        this.failureCode = n;
    }

    public int getFailureCode() {
        return this.failureCode;
    }
}

