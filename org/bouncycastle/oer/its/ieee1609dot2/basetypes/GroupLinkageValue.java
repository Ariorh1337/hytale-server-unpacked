/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class GroupLinkageValue
extends ASN1Object {
    private final ASN1OctetString jValue;
    private final ASN1OctetString value;

    private GroupLinkageValue(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.jValue = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.value = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        this.assertValues();
    }

    public GroupLinkageValue(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2) {
        this.jValue = aSN1OctetString;
        this.value = aSN1OctetString2;
        this.assertValues();
    }

    private void assertValues() {
        if (this.jValue == null || this.jValue.getOctets().length != 4) {
            throw new IllegalArgumentException("jValue is null or not four bytes long");
        }
        if (this.value == null || this.value.getOctets().length != 9) {
            throw new IllegalArgumentException("value is null or not nine bytes long");
        }
    }

    public static GroupLinkageValue getInstance(Object object) {
        if (object instanceof GroupLinkageValue) {
            return (GroupLinkageValue)object;
        }
        if (object != null) {
            return new GroupLinkageValue(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1OctetString getJValue() {
        return this.jValue;
    }

    public ASN1OctetString getValue() {
        return this.value;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.jValue, this.value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString jValue;
        private ASN1OctetString value;

        public Builder setJValue(ASN1OctetString aSN1OctetString) {
            this.jValue = aSN1OctetString;
            return this;
        }

        public Builder setJValue(byte[] byArray) {
            return this.setJValue(new DEROctetString(Arrays.clone(byArray)));
        }

        public Builder setValue(ASN1OctetString aSN1OctetString) {
            this.value = aSN1OctetString;
            return this;
        }

        public Builder setValue(byte[] byArray) {
            return this.setValue(new DEROctetString(Arrays.clone(byArray)));
        }

        public GroupLinkageValue createGroupLinkageValue() {
            return new GroupLinkageValue(this.jValue, this.value);
        }
    }
}

