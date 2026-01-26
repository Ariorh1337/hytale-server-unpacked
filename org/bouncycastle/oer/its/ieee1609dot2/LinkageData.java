/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GroupLinkageValue;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.IValue;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.LinkageValue;

public class LinkageData
extends ASN1Object {
    private final IValue iCert;
    private final LinkageValue linkageValue;
    private final GroupLinkageValue groupLinkageValue;

    public LinkageData(IValue iValue, LinkageValue linkageValue, GroupLinkageValue groupLinkageValue) {
        this.iCert = iValue;
        this.linkageValue = linkageValue;
        this.groupLinkageValue = groupLinkageValue;
    }

    private LinkageData(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.iCert = IValue.getInstance(aSN1Sequence.getObjectAt(0));
        this.linkageValue = LinkageValue.getInstance(aSN1Sequence.getObjectAt(1));
        this.groupLinkageValue = OEROptional.getValue(GroupLinkageValue.class, aSN1Sequence.getObjectAt(2));
    }

    public static LinkageData getInstance(Object object) {
        if (object instanceof LinkageData) {
            return (LinkageData)object;
        }
        if (object != null) {
            return new LinkageData(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public IValue getICert() {
        return this.iCert;
    }

    public LinkageValue getLinkageValue() {
        return this.linkageValue;
    }

    public GroupLinkageValue getGroupLinkageValue() {
        return this.groupLinkageValue;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.iCert, this.linkageValue, OEROptional.getInstance(this.groupLinkageValue)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private IValue iCert;
        private LinkageValue linkageValue;
        private GroupLinkageValue groupLinkageValue;

        public Builder setICert(IValue iValue) {
            this.iCert = iValue;
            return this;
        }

        public Builder setLinkageValue(LinkageValue linkageValue) {
            this.linkageValue = linkageValue;
            return this;
        }

        public Builder setGroupLinkageValue(GroupLinkageValue groupLinkageValue) {
            this.groupLinkageValue = groupLinkageValue;
            return this;
        }

        public LinkageData createLinkageData() {
            return new LinkageData(this.iCert, this.linkageValue, this.groupLinkageValue);
        }
    }
}

