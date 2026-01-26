/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.ContributedExtensionBlock;

public class ContributedExtensionBlocks
extends ASN1Object {
    private final List<ContributedExtensionBlock> contributedExtensionBlocks;

    public ContributedExtensionBlocks(List<ContributedExtensionBlock> list) {
        this.contributedExtensionBlocks = Collections.unmodifiableList(list);
    }

    private ContributedExtensionBlocks(ASN1Sequence aSN1Sequence) {
        ArrayList<ContributedExtensionBlock> arrayList = new ArrayList<ContributedExtensionBlock>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(ContributedExtensionBlock.getInstance(iterator.next()));
        }
        this.contributedExtensionBlocks = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<ContributedExtensionBlock> getContributedExtensionBlocks() {
        return this.contributedExtensionBlocks;
    }

    public int size() {
        return this.contributedExtensionBlocks.size();
    }

    public static ContributedExtensionBlocks getInstance(Object object) {
        if (object instanceof ContributedExtensionBlocks) {
            return (ContributedExtensionBlocks)object;
        }
        if (object != null) {
            return new ContributedExtensionBlocks(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.contributedExtensionBlocks.toArray(new ContributedExtensionBlock[0]));
    }

    public static class Builder {
        private final List<ContributedExtensionBlock> extensionBlocks = new ArrayList<ContributedExtensionBlock>();

        public Builder add(ContributedExtensionBlock ... contributedExtensionBlockArray) {
            this.extensionBlocks.addAll(Arrays.asList(contributedExtensionBlockArray));
            return this;
        }

        public ContributedExtensionBlocks build() {
            return new ContributedExtensionBlocks(this.extensionBlocks);
        }
    }
}

