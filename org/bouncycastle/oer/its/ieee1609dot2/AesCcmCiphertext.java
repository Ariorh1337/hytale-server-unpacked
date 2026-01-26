/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.util.Arrays;

public class AesCcmCiphertext
extends ASN1Object {
    private final ASN1OctetString nonce;
    private final Opaque ccmCiphertext;

    public AesCcmCiphertext(ASN1OctetString aSN1OctetString, Opaque opaque) {
        this.nonce = aSN1OctetString;
        this.ccmCiphertext = opaque;
    }

    private AesCcmCiphertext(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        this.nonce = ASN1OctetString.getInstance(iterator.next());
        this.ccmCiphertext = Opaque.getInstance(iterator.next());
    }

    public static AesCcmCiphertext getInstance(Object object) {
        if (object instanceof AesCcmCiphertext) {
            return (AesCcmCiphertext)object;
        }
        if (object != null) {
            return new AesCcmCiphertext(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1OctetString getNonce() {
        return this.nonce;
    }

    public Opaque getCcmCiphertext() {
        return this.ccmCiphertext;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.nonce, this.ccmCiphertext);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString nonce;
        private Opaque opaque;

        public Builder setNonce(ASN1OctetString aSN1OctetString) {
            this.nonce = aSN1OctetString;
            return this;
        }

        public Builder setNonce(byte[] byArray) {
            return this.setNonce(new DEROctetString(Arrays.clone(byArray)));
        }

        public Builder setCcmCiphertext(Opaque opaque) {
            this.opaque = opaque;
            return this;
        }

        public Builder setCcmCiphertext(byte[] byArray) {
            return this.setCcmCiphertext(new Opaque(byArray));
        }

        public AesCcmCiphertext createAesCcmCiphertext() {
            return new AesCcmCiphertext(this.nonce, this.opaque);
        }
    }
}

