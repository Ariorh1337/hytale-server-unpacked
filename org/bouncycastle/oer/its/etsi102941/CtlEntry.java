/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.etsi102941.AaEntry;
import org.bouncycastle.oer.its.etsi102941.DcEntry;
import org.bouncycastle.oer.its.etsi102941.EaEntry;
import org.bouncycastle.oer.its.etsi102941.RootCaEntry;
import org.bouncycastle.oer.its.etsi102941.TlmEntry;

public class CtlEntry
extends ASN1Object
implements ASN1Choice {
    public static final int rca = 0;
    public static final int ea = 1;
    public static final int aa = 2;
    public static final int dc = 3;
    public static final int tlm = 4;
    private final int choice;
    private final ASN1Encodable ctlEntry;

    public CtlEntry(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.ctlEntry = aSN1Encodable;
    }

    private CtlEntry(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.ctlEntry = RootCaEntry.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.ctlEntry = EaEntry.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 2: {
                this.ctlEntry = AaEntry.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 3: {
                this.ctlEntry = DcEntry.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
            case 4: {
                this.ctlEntry = TlmEntry.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + this.choice);
    }

    public static CtlEntry getInstance(Object object) {
        if (object instanceof CtlEntry) {
            return (CtlEntry)object;
        }
        if (object != null) {
            return new CtlEntry(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public static CtlEntry rca(RootCaEntry rootCaEntry) {
        return new CtlEntry(0, rootCaEntry);
    }

    public static CtlEntry ea(EaEntry eaEntry) {
        return new CtlEntry(1, eaEntry);
    }

    public static CtlEntry aa(AaEntry aaEntry) {
        return new CtlEntry(2, aaEntry);
    }

    public static CtlEntry dc(DcEntry dcEntry) {
        return new CtlEntry(3, dcEntry);
    }

    public static CtlEntry tlm(TlmEntry tlmEntry) {
        return new CtlEntry(4, tlmEntry);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCtlEntry() {
        return this.ctlEntry;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.ctlEntry);
    }
}

