/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PKMACValue
extends ASN1Object {
    private AlgorithmIdentifier algId;
    private ASN1BitString value;

    private PKMACValue(ASN1Sequence aSN1Sequence) {
        this.algId = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.value = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static PKMACValue getInstance(Object object) {
        if (object instanceof PKMACValue) {
            return (PKMACValue)object;
        }
        if (object != null) {
            return new PKMACValue(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static PKMACValue getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return PKMACValue.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public PKMACValue(PBMParameter pBMParameter, DERBitString dERBitString) {
        this(new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, pBMParameter), dERBitString);
    }

    public PKMACValue(AlgorithmIdentifier algorithmIdentifier, DERBitString dERBitString) {
        this.algId = algorithmIdentifier;
        this.value = dERBitString;
    }

    public AlgorithmIdentifier getAlgId() {
        return this.algId;
    }

    public ASN1BitString getValue() {
        return this.value;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.algId, this.value);
    }
}

