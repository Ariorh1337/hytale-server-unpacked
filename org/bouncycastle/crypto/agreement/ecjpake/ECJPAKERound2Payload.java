/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.ecjpake;

import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKEUtil;
import org.bouncycastle.crypto.agreement.ecjpake.ECSchnorrZKP;
import org.bouncycastle.math.ec.ECPoint;

public class ECJPAKERound2Payload {
    private final String participantId;
    private final ECPoint a;
    private final ECSchnorrZKP knowledgeProofForX2s;

    public ECJPAKERound2Payload(String string, ECPoint eCPoint, ECSchnorrZKP eCSchnorrZKP) {
        ECJPAKEUtil.validateNotNull(string, "participantId");
        ECJPAKEUtil.validateNotNull(eCPoint, "a");
        ECJPAKEUtil.validateNotNull(eCSchnorrZKP, "knowledgeProofForX2s");
        this.participantId = string;
        this.a = eCPoint;
        this.knowledgeProofForX2s = eCSchnorrZKP;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public ECPoint getA() {
        return this.a;
    }

    public ECSchnorrZKP getKnowledgeProofForX2s() {
        return this.knowledgeProofForX2s;
    }
}

