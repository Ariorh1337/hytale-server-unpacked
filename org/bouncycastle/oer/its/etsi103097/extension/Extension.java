/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941CrlRequest;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941DeltaCtlRequest;
import org.bouncycastle.oer.its.etsi103097.extension.ExtId;

public class Extension
extends ASN1Object {
    public static final ExtId etsiTs102941CrlRequestId = new ExtId(1L);
    public static final ExtId etsiTs102941DeltaCtlRequestId = new ExtId(2L);
    private final ExtId id;
    private final ASN1Encodable content;

    protected Extension(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.id = ExtId.getInstance(aSN1Sequence.getObjectAt(0));
        if (this.id.equals(etsiTs102941CrlRequestId)) {
            this.content = EtsiTs102941CrlRequest.getInstance(aSN1Sequence.getObjectAt(1));
        } else if (this.id.equals(etsiTs102941DeltaCtlRequestId)) {
            this.content = EtsiTs102941DeltaCtlRequest.getInstance(aSN1Sequence.getObjectAt(1));
        } else {
            throw new IllegalArgumentException("id not 1 (EtsiTs102941CrlRequest) or 2 (EtsiTs102941DeltaCtlRequest)");
        }
    }

    public Extension(ExtId extId, ASN1Encodable aSN1Encodable) {
        this.id = extId;
        if (extId.getExtId().intValue() != 1 && extId.getExtId().intValue() != 2) {
            throw new IllegalArgumentException("id not 1 (EtsiTs102941CrlRequest) or 2 (EtsiTs102941DeltaCtlRequest)");
        }
        this.content = aSN1Encodable;
    }

    public static Extension etsiTs102941CrlRequest(EtsiTs102941CrlRequest etsiTs102941CrlRequest) {
        return new Extension(etsiTs102941CrlRequestId, etsiTs102941CrlRequest);
    }

    public static Extension etsiTs102941DeltaCtlRequest(EtsiTs102941DeltaCtlRequest etsiTs102941DeltaCtlRequest) {
        return new Extension(etsiTs102941DeltaCtlRequestId, etsiTs102941DeltaCtlRequest);
    }

    public static Extension getInstance(Object object) {
        if (object instanceof Extension) {
            return (Extension)object;
        }
        if (object != null) {
            return new Extension(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.id, this.content});
    }

    public ExtId getId() {
        return this.id;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }
}

