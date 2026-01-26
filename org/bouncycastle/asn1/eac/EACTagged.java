/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;

class EACTagged {
    EACTagged() {
    }

    static ASN1TaggedObject create(int n, ASN1Sequence aSN1Sequence) {
        return new DERTaggedObject(false, 64, n, (ASN1Encodable)aSN1Sequence);
    }

    static ASN1TaggedObject create(int n, PublicKeyDataObject publicKeyDataObject) {
        return new DERTaggedObject(false, 64, n, (ASN1Encodable)publicKeyDataObject);
    }

    static ASN1TaggedObject create(int n, byte[] byArray) {
        return new DERTaggedObject(false, 64, n, (ASN1Encodable)new DEROctetString(byArray));
    }
}

