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
import org.bouncycastle.pqc.asn1.CMCEPublicKey;
import org.bouncycastle.util.Arrays;

public class CMCEPrivateKey
extends ASN1Object {
    private int version;
    private byte[] delta;
    private byte[] C;
    private byte[] g;
    private byte[] alpha;
    private byte[] s;
    private CMCEPublicKey PublicKey;

    public CMCEPrivateKey(int n, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
        this(n, byArray, byArray2, byArray3, byArray4, byArray5, null);
    }

    public CMCEPrivateKey(int n, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, CMCEPublicKey cMCEPublicKey) {
        this.version = n;
        if (n != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.delta = Arrays.clone(byArray);
        this.C = Arrays.clone(byArray2);
        this.g = Arrays.clone(byArray3);
        this.alpha = Arrays.clone(byArray4);
        this.s = Arrays.clone(byArray5);
        this.PublicKey = cMCEPublicKey;
    }

    private CMCEPrivateKey(ASN1Sequence aSN1Sequence) {
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
        if (this.version != 0) {
            throw new IllegalArgumentException("unrecognized version");
        }
        this.delta = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        this.C = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
        this.g = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(3)).getOctets());
        this.alpha = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(4)).getOctets());
        this.s = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(5)).getOctets());
        if (aSN1Sequence.size() == 7) {
            this.PublicKey = CMCEPublicKey.getInstance(aSN1Sequence.getObjectAt(6));
        }
    }

    public int getVersion() {
        return this.version;
    }

    public byte[] getDelta() {
        return Arrays.clone(this.delta);
    }

    public byte[] getC() {
        return Arrays.clone(this.C);
    }

    public byte[] getG() {
        return Arrays.clone(this.g);
    }

    public byte[] getAlpha() {
        return Arrays.clone(this.alpha);
    }

    public byte[] getS() {
        return Arrays.clone(this.s);
    }

    public CMCEPublicKey getPublicKey() {
        return this.PublicKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new ASN1Integer(this.version));
        aSN1EncodableVector.add(new DEROctetString(this.delta));
        aSN1EncodableVector.add(new DEROctetString(this.C));
        aSN1EncodableVector.add(new DEROctetString(this.g));
        aSN1EncodableVector.add(new DEROctetString(this.alpha));
        aSN1EncodableVector.add(new DEROctetString(this.s));
        if (this.PublicKey != null) {
            aSN1EncodableVector.add(new CMCEPublicKey(this.PublicKey.getT()));
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static CMCEPrivateKey getInstance(Object object) {
        if (object instanceof CMCEPrivateKey) {
            return (CMCEPrivateKey)object;
        }
        if (object != null) {
            return new CMCEPrivateKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

