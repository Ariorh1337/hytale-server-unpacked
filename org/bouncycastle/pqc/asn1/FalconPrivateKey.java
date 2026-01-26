/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.pqc.asn1.FalconPublicKey;
import org.bouncycastle.util.Arrays;

public class FalconPrivateKey
extends ASN1Object {
    private int version;
    private byte[] f;
    private byte[] g;
    private byte[] F;
    private FalconPublicKey publicKey;

    public FalconPrivateKey(int n, byte[] byArray, byte[] byArray2, byte[] byArray3, FalconPublicKey falconPublicKey) {
        this.version = n;
        this.f = byArray;
        this.g = byArray2;
        this.F = byArray3;
        this.publicKey = falconPublicKey;
    }

    public FalconPrivateKey(int n, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        this(n, byArray, byArray2, byArray3, null);
    }

    public int getVersion() {
        return this.version;
    }

    public byte[] getf() {
        return Arrays.clone(this.f);
    }

    public byte[] getF() {
        return Arrays.clone(this.F);
    }

    public FalconPublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] getG() {
        return Arrays.clone(this.g);
    }

    private FalconPrivateKey(ASN1Sequence aSN1Sequence) {
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
        if (this.version != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.f = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        this.g = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
        this.F = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets());
        if (aSN1Sequence.size() == 5) {
            this.publicKey = FalconPublicKey.getInstance(aSN1Sequence.getObjectAt(4));
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.version));
        aSN1EncodableVector.add(new DEROctetString(this.f));
        aSN1EncodableVector.add(new DEROctetString(this.g));
        aSN1EncodableVector.add(new DEROctetString(this.F));
        if (this.publicKey != null) {
            aSN1EncodableVector.add(new FalconPublicKey(this.publicKey.getH()));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static FalconPrivateKey getInstance(Object object) {
        if (object instanceof FalconPrivateKey) {
            return (FalconPrivateKey)object;
        }
        if (object != null) {
            return new FalconPrivateKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

