/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.CtlFormat;
import org.bouncycastle.oer.its.etsi102941.SequenceOfCtlCommand;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class FullCtl
extends CtlFormat {
    public FullCtl(Version version, Time32 time32, ASN1Boolean aSN1Boolean, UINT8 uINT8, SequenceOfCtlCommand sequenceOfCtlCommand) {
        super(version, time32, aSN1Boolean, uINT8, sequenceOfCtlCommand);
    }

    protected FullCtl(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static FullCtl getInstance(Object object) {
        if (object instanceof FullCtl) {
            return (FullCtl)object;
        }
        if (object != null) {
            return new FullCtl(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

