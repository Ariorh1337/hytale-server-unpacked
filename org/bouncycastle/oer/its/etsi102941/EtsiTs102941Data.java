/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.EtsiTs102941DataContent;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;

public class EtsiTs102941Data
extends ASN1Object {
    private final Version version;
    private final EtsiTs102941DataContent content;

    public EtsiTs102941Data(Version version, EtsiTs102941DataContent etsiTs102941DataContent) {
        this.version = version;
        this.content = etsiTs102941DataContent;
    }

    private EtsiTs102941Data(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.version = Version.getInstance(aSN1Sequence.getObjectAt(0));
        this.content = EtsiTs102941DataContent.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static EtsiTs102941Data getInstance(Object object) {
        if (object instanceof EtsiTs102941Data) {
            return (EtsiTs102941Data)object;
        }
        if (object != null) {
            if (object instanceof Opaque) {
                return new EtsiTs102941Data(ASN1Sequence.getInstance(((Opaque)object).getContent()));
            }
            return new EtsiTs102941Data(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public Version getVersion() {
        return this.version;
    }

    public EtsiTs102941DataContent getContent() {
        return this.content;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.version, this.content});
    }
}

