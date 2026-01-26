/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;

public class ArchiveTimeStampChain
extends ASN1Object {
    private ASN1Sequence archiveTimeStamps;

    public static ArchiveTimeStampChain getInstance(Object object) {
        if (object instanceof ArchiveTimeStampChain) {
            return (ArchiveTimeStampChain)object;
        }
        if (object != null) {
            return new ArchiveTimeStampChain(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ArchiveTimeStampChain(ArchiveTimeStamp archiveTimeStamp) {
        this.archiveTimeStamps = new DERSequence(archiveTimeStamp);
    }

    public ArchiveTimeStampChain(ArchiveTimeStamp[] archiveTimeStampArray) {
        this.archiveTimeStamps = new DERSequence(archiveTimeStampArray);
    }

    private ArchiveTimeStampChain(ASN1Sequence aSN1Sequence) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(aSN1Sequence.size());
        Enumeration enumeration = aSN1Sequence.getObjects();
        while (enumeration.hasMoreElements()) {
            aSN1EncodableVector.add(ArchiveTimeStamp.getInstance(enumeration.nextElement()));
        }
        this.archiveTimeStamps = new DERSequence(aSN1EncodableVector);
    }

    public ArchiveTimeStamp[] getArchiveTimestamps() {
        ArchiveTimeStamp[] archiveTimeStampArray = new ArchiveTimeStamp[this.archiveTimeStamps.size()];
        for (int i = 0; i != archiveTimeStampArray.length; ++i) {
            archiveTimeStampArray[i] = ArchiveTimeStamp.getInstance(this.archiveTimeStamps.getObjectAt(i));
        }
        return archiveTimeStampArray;
    }

    public ArchiveTimeStampChain append(ArchiveTimeStamp archiveTimeStamp) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(this.archiveTimeStamps.size() + 1);
        for (int i = 0; i != this.archiveTimeStamps.size(); ++i) {
            aSN1EncodableVector.add(this.archiveTimeStamps.getObjectAt(i));
        }
        aSN1EncodableVector.add(archiveTimeStamp);
        return new ArchiveTimeStampChain(new DERSequence(aSN1EncodableVector));
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.archiveTimeStamps;
    }
}

