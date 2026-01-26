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
import org.bouncycastle.pqc.asn1.SPHINCSPLUSPublicKey;
import org.bouncycastle.util.Arrays;

public class SPHINCSPLUSPrivateKey
extends ASN1Object {
    private int version;
    private byte[] skseed;
    private byte[] skprf;
    private SPHINCSPLUSPublicKey PublicKey;

    public int getVersion() {
        return this.version;
    }

    public byte[] getSkseed() {
        return Arrays.clone(this.skseed);
    }

    public byte[] getSkprf() {
        return Arrays.clone(this.skprf);
    }

    public SPHINCSPLUSPublicKey getPublicKey() {
        return this.PublicKey;
    }

    public SPHINCSPLUSPrivateKey(int n, byte[] byArray, byte[] byArray2) {
        this(n, byArray, byArray2, null);
    }

    public SPHINCSPLUSPrivateKey(int n, byte[] byArray, byte[] byArray2, SPHINCSPLUSPublicKey sPHINCSPLUSPublicKey) {
        this.version = n;
        this.skseed = byArray;
        this.skprf = byArray2;
        this.PublicKey = sPHINCSPLUSPublicKey;
    }

    @Deprecated
    public SPHINCSPLUSPrivateKey(ASN1Sequence aSN1Sequence) {
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
        if (this.version != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.skseed = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        this.skprf = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
        if (aSN1Sequence.size() == 4) {
            this.PublicKey = SPHINCSPLUSPublicKey.getInstance(aSN1Sequence.getObjectAt(3));
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.version));
        aSN1EncodableVector.add(new DEROctetString(this.skseed));
        aSN1EncodableVector.add(new DEROctetString(this.skprf));
        if (this.PublicKey != null) {
            aSN1EncodableVector.add(new SPHINCSPLUSPublicKey(this.PublicKey.getPkseed(), this.PublicKey.getPkroot()));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static SPHINCSPLUSPrivateKey getInstance(Object object) {
        if (object instanceof SPHINCSPLUSPrivateKey) {
            return (SPHINCSPLUSPrivateKey)object;
        }
        if (object != null) {
            return new SPHINCSPLUSPrivateKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

