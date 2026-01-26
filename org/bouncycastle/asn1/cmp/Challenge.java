/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class Challenge
extends ASN1Object {
    private final AlgorithmIdentifier owf;
    private final ASN1OctetString witness;
    private final ASN1OctetString challenge;
    private final EnvelopedData encryptedRand;

    private Challenge(ASN1Sequence aSN1Sequence) {
        int n = 0;
        this.owf = aSN1Sequence.getObjectAt(0).toASN1Primitive() instanceof ASN1Sequence ? AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(n++)) : null;
        this.witness = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(n++));
        this.challenge = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(n++));
        if (aSN1Sequence.size() > n) {
            if (this.challenge.getOctets().length != 0) {
                throw new IllegalArgumentException("ambigous challenge");
            }
            this.encryptedRand = EnvelopedData.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(n)), true);
        } else {
            this.encryptedRand = null;
        }
    }

    public Challenge(byte[] byArray, byte[] byArray2) {
        this(null, byArray, byArray2);
    }

    public Challenge(byte[] byArray, EnvelopedData envelopedData) {
        this(null, byArray, envelopedData);
    }

    public Challenge(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, byte[] byArray2) {
        this.owf = algorithmIdentifier;
        this.witness = new DEROctetString(byArray);
        this.challenge = new DEROctetString(byArray2);
        this.encryptedRand = null;
    }

    public Challenge(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, EnvelopedData envelopedData) {
        this.owf = algorithmIdentifier;
        this.witness = new DEROctetString(byArray);
        this.challenge = new DEROctetString(new byte[0]);
        this.encryptedRand = envelopedData;
    }

    public static Challenge getInstance(Object object) {
        if (object instanceof Challenge) {
            return (Challenge)object;
        }
        if (object != null) {
            return new Challenge(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }

    public byte[] getWitness() {
        return this.witness.getOctets();
    }

    public boolean isEncryptedRand() {
        return this.encryptedRand != null;
    }

    public byte[] getChallenge() {
        return this.challenge.getOctets();
    }

    public EnvelopedData getEncryptedRand() {
        return this.encryptedRand;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.addOptional(this.owf);
        aSN1EncodableVector.add(this.witness);
        aSN1EncodableVector.add(this.challenge);
        if (this.encryptedRand != null) {
            aSN1EncodableVector.add(new DERTaggedObject(0, this.encryptedRand));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static class Rand
    extends ASN1Object {
        private final ASN1Integer integer;
        private final GeneralName sender;

        public Rand(byte[] byArray, GeneralName generalName) {
            this(new ASN1Integer(byArray), generalName);
        }

        public Rand(ASN1Integer aSN1Integer, GeneralName generalName) {
            this.integer = aSN1Integer;
            this.sender = generalName;
        }

        private Rand(ASN1Sequence aSN1Sequence) {
            if (aSN1Sequence.size() != 2) {
                throw new IllegalArgumentException("expected sequence size of 2");
            }
            this.integer = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
            this.sender = GeneralName.getInstance(aSN1Sequence.getObjectAt(1));
        }

        public static Rand getInstance(Object object) {
            if (object instanceof Rand) {
                return (Rand)object;
            }
            if (object != null) {
                return new Rand(ASN1Sequence.getInstance(object));
            }
            return null;
        }

        public ASN1Integer getInt() {
            return this.integer;
        }

        public GeneralName getSender() {
            return this.sender;
        }

        @Override
        public ASN1Primitive toASN1Primitive() {
            return new DERSequence(new ASN1Encodable[]{this.integer, this.sender});
        }
    }
}

