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
import org.bouncycastle.oer.its.etsi102941.DcDelete;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class CtlDelete
extends ASN1Object
implements ASN1Choice {
    public static final int cert = 0;
    public static final int dc = 1;
    private final int choice;
    private final ASN1Encodable ctlDelete;

    public static CtlDelete cert(HashedId8 hashedId8) {
        return new CtlDelete(0, hashedId8);
    }

    public static CtlDelete dc(DcDelete dcDelete) {
        return new CtlDelete(1, dcDelete);
    }

    public CtlDelete(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        switch (n) {
            case 0: {
                this.ctlDelete = HashedId8.getInstance(aSN1Encodable);
                return;
            }
            case 1: {
                this.ctlDelete = DcDelete.getInstance(aSN1Encodable);
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + n);
    }

    private CtlDelete(ASN1TaggedObject aSN1TaggedObject) {
        this(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
    }

    public static CtlDelete getInstance(Object object) {
        if (object instanceof CtlDelete) {
            return (CtlDelete)object;
        }
        if (object != null) {
            return new CtlDelete(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCtlDelete() {
        return this.ctlDelete;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.ctlDelete);
    }
}

