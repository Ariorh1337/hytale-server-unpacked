/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertPolicyId;

public class PolicyMappings
extends ASN1Object {
    ASN1Sequence seq = null;

    public static PolicyMappings getInstance(Object object) {
        if (object instanceof PolicyMappings) {
            return (PolicyMappings)object;
        }
        if (object != null) {
            return new PolicyMappings(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private PolicyMappings(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
    }

    public PolicyMappings(Hashtable hashtable) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(hashtable.size());
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            String string2 = (String)hashtable.get(string);
            aSN1EncodableVector.add(new DERSequence(new ASN1ObjectIdentifier(string), new ASN1ObjectIdentifier(string2)));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public PolicyMappings(CertPolicyId certPolicyId, CertPolicyId certPolicyId2) {
        this.seq = new DERSequence(new DERSequence(certPolicyId, certPolicyId2));
    }

    public PolicyMappings(CertPolicyId[] certPolicyIdArray, CertPolicyId[] certPolicyIdArray2) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(certPolicyIdArray.length);
        for (int i = 0; i != certPolicyIdArray.length; ++i) {
            aSN1EncodableVector.add(new DERSequence(certPolicyIdArray[i], certPolicyIdArray2[i]));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}

