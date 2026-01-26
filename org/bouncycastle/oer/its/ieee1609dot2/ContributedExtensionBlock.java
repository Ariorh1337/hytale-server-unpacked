/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiOriginatingHeaderInfoExtension;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfoContributorId;

public class ContributedExtensionBlock
extends ASN1Object {
    private final HeaderInfoContributorId contributorId;
    private final List<EtsiOriginatingHeaderInfoExtension> extns;

    public ContributedExtensionBlock(HeaderInfoContributorId headerInfoContributorId, List<EtsiOriginatingHeaderInfoExtension> list) {
        this.contributorId = headerInfoContributorId;
        this.extns = list;
    }

    private ContributedExtensionBlock(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.contributorId = HeaderInfoContributorId.getInstance(aSN1Sequence.getObjectAt(0));
        Iterator<ASN1Encodable> iterator = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(1)).iterator();
        ArrayList<EtsiOriginatingHeaderInfoExtension> arrayList = new ArrayList<EtsiOriginatingHeaderInfoExtension>();
        while (iterator.hasNext()) {
            arrayList.add(EtsiOriginatingHeaderInfoExtension.getInstance(iterator.next()));
        }
        this.extns = Collections.unmodifiableList(arrayList);
    }

    public static ContributedExtensionBlock getInstance(Object object) {
        if (object instanceof ContributedExtensionBlock) {
            return (ContributedExtensionBlock)object;
        }
        if (object != null) {
            return new ContributedExtensionBlock(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.contributorId, ItsUtils.toSequence(this.extns));
    }

    public HeaderInfoContributorId getContributorId() {
        return this.contributorId;
    }

    public List<EtsiOriginatingHeaderInfoExtension> getExtns() {
        return this.extns;
    }
}

