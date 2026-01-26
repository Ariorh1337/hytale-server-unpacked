/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;

public class JPAKERound3Payload {
    private final String participantId;
    private final BigInteger macTag;

    public JPAKERound3Payload(String string, BigInteger bigInteger) {
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

