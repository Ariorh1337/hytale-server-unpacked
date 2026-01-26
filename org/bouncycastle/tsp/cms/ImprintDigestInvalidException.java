/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.cms;

import org.bouncycastle.tsp.TimeStampToken;

public class ImprintDigestInvalidException
extends Exception {
    private TimeStampToken token;

    public ImprintDigestInvalidException(String string, TimeStampToken timeStampToken) {
        super(string);
        this.token = timeStampToken;
    }

    public TimeStampToken getTimeStampToken() {
        return this.token;
    }
}

