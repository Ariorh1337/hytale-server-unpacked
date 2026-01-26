/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;

public abstract class SwitchIndexer {
    public abstract ASN1Encodable get(int var1);

    public static class Asn1EncodableVectorIndexer
    extends SwitchIndexer {
        private final ASN1EncodableVector asn1EncodableVector;

        public Asn1EncodableVectorIndexer(ASN1EncodableVector aSN1EncodableVector) {
            this.asn1EncodableVector = aSN1EncodableVector;
        }

        @Override
        public ASN1Encodable get(int n) {
            return this.asn1EncodableVector.get(n);
        }
    }

    public static class Asn1SequenceIndexer
    extends SwitchIndexer {
        private final ASN1Sequence sequence;

        public Asn1SequenceIndexer(ASN1Sequence aSN1Sequence) {
            this.sequence = aSN1Sequence;
        }

        @Override
        public ASN1Encodable get(int n) {
            return this.sequence.getObjectAt(n);
        }
    }

    public static class FixedValueIndexer
    extends SwitchIndexer {
        private final ASN1Encodable returnValue;

        public FixedValueIndexer(ASN1Encodable aSN1Encodable) {
            this.returnValue = aSN1Encodable;
        }

        @Override
        public ASN1Encodable get(int n) {
            return this.returnValue;
        }
    }
}

