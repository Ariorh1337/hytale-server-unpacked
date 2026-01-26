/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class PublicEncryptionKey
extends ASN1Object {
    private final SymmAlgorithm supportedSymmAlg;
    private final BasePublicEncryptionKey publicKey;

    public PublicEncryptionKey(SymmAlgorithm symmAlgorithm2, BasePublicEncryptionKey basePublicEncryptionKey) {
        this.supportedSymmAlg = symmAlgorithm2;
        this.publicKey = basePublicEncryptionKey;
    }

    private PublicEncryptionKey(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.supportedSymmAlg = SymmAlgorithm.getInstance(aSN1Sequence.getObjectAt(0));
        this.publicKey = BasePublicEncryptionKey.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static PublicEncryptionKey getInstance(Object object) {
        if (object instanceof PublicEncryptionKey) {
            return (PublicEncryptionKey)object;
        }
        if (object != null) {
            return new PublicEncryptionKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public SymmAlgorithm getSupportedSymmAlg() {
        return this.supportedSymmAlg;
    }

    public BasePublicEncryptionKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.supportedSymmAlg, this.publicKey);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SymmAlgorithm supportedSymmAlg;
        private BasePublicEncryptionKey publicKey;

        public Builder setSupportedSymmAlg(SymmAlgorithm symmAlgorithm2) {
            this.supportedSymmAlg = symmAlgorithm2;
            return this;
        }

        public Builder setPublicKey(BasePublicEncryptionKey basePublicEncryptionKey) {
            this.publicKey = basePublicEncryptionKey;
            return this;
        }

        public PublicEncryptionKey createPublicEncryptionKey() {
            return new PublicEncryptionKey(this.supportedSymmAlg, this.publicKey);
        }
    }
}

