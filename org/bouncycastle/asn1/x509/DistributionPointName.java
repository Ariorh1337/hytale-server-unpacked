/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.util.Strings;

public class DistributionPointName
extends ASN1Object
implements ASN1Choice {
    public static final int FULL_NAME = 0;
    public static final int NAME_RELATIVE_TO_CRL_ISSUER = 1;
    private final ASN1Encodable name;
    private final int type;

    public static DistributionPointName getInstance(Object object) {
        if (object == null || object instanceof DistributionPointName) {
            return (DistributionPointName)object;
        }
        if (object instanceof ASN1TaggedObject) {
            return new DistributionPointName((ASN1TaggedObject)object);
        }
        throw new IllegalArgumentException("unknown object in factory: " + object.getClass().getName());
    }

    public static DistributionPointName getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return DistributionPointName.getInstance(ASN1Util.getInstanceChoiceBaseObject(aSN1TaggedObject, bl, "DistributionPointName"));
    }

    public static DistributionPointName getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return DistributionPointName.getInstance(ASN1Util.getTaggedChoiceBaseObject(aSN1TaggedObject, bl, "DistributionPointName"));
    }

    public DistributionPointName(int n, ASN1Encodable aSN1Encodable) {
        this.type = n;
        this.name = aSN1Encodable;
    }

    public DistributionPointName(GeneralNames generalNames) {
        this(0, generalNames);
    }

    public int getType() {
        return this.type;
    }

    public ASN1Encodable getName() {
        return this.name;
    }

    public DistributionPointName(ASN1TaggedObject aSN1TaggedObject) {
        this.type = aSN1TaggedObject.getTagNo();
        if (aSN1TaggedObject.hasContextTag(0)) {
            this.name = GeneralNames.getInstance(aSN1TaggedObject, false);
        } else if (aSN1TaggedObject.hasContextTag(1)) {
            this.name = ASN1Set.getInstance(aSN1TaggedObject, false);
        } else {
            throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(aSN1TaggedObject));
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.type, this.name);
    }

    public String toString() {
        String string = Strings.lineSeparator();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DistributionPointName: [");
        stringBuilder.append(string);
        if (this.type == 0) {
            this.appendObject(stringBuilder, string, "fullName", this.name.toString());
        } else {
            this.appendObject(stringBuilder, string, "nameRelativeToCRLIssuer", this.name.toString());
        }
        stringBuilder.append("]");
        stringBuilder.append(string);
        return stringBuilder.toString();
    }

    private void appendObject(StringBuilder stringBuilder, String string, String string2, String string3) {
        String string4 = "    ";
        stringBuilder.append(string4);
        stringBuilder.append(string2);
        stringBuilder.append(":");
        stringBuilder.append(string);
        stringBuilder.append(string4);
        stringBuilder.append(string4);
        stringBuilder.append(string3);
        stringBuilder.append(string);
    }
}

