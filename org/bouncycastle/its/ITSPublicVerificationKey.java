/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class ITSPublicVerificationKey {
    protected final PublicVerificationKey verificationKey;

    public ITSPublicVerificationKey(PublicVerificationKey publicVerificationKey) {
        this.verificationKey = publicVerificationKey;
    }

    public PublicVerificationKey toASN1Structure() {
        return this.verificationKey;
    }
}

