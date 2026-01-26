/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;

public class ECJPAKERound3Payload {
    private final String participantId;
    private final BigInteger macTag;

    public ECJPAKERound3Payload(String string, BigInteger bigInteger) {
        this.participantId = string;
        this.macTag = bigInteger;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getMacTag() {
        return this.macTag;
    }
}

