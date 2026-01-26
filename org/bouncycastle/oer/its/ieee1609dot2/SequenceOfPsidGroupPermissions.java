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
import org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions;

public class SequenceOfPsidGroupPermissions
extends ASN1Object {
    private final List<PsidGroupPermissions> psidGroupPermissions;

    public SequenceOfPsidGroupPermissions(List<PsidGroupPermissions> list) {
        this.psidGroupPermissions = Collections.unmodifiableList(list);
    }

    private SequenceOfPsidGroupPermissions(ASN1Sequence aSN1Sequence) {
        ArrayList<PsidGroupPermissions> arrayList = new ArrayList<PsidGroupPermissions>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(PsidGroupPermissions.getInstance(iterator.next()));
        }
        this.psidGroupPermissions = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfPsidGroupPermissions getInstance(Object object) {
        if (object instanceof SequenceOfPsidGroupPermissions) {
            return (SequenceOfPsidGroupPermissions)object;
        }
        if (object != null) {
            return new SequenceOfPsidGroupPermissions(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<PsidGroupPermissions> getPsidGroupPermissions() {
        return this.psidGroupPermissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.psidGroupPermissions.toArray(new PsidGroupPermissions[0]));
    }

    public static class Builder {
        private final List<PsidGroupPermissions> groupPermissions = new ArrayList<PsidGroupPermissions>();

        public Builder setGroupPermissions(List<PsidGroupPermissions> list) {
            this.groupPermissions.addAll(list);
            return this;
        }

        public Builder addGroupPermission(PsidGroupPermissions ... psidGroupPermissionsArray) {
            this.groupPermissions.addAll(Arrays.asList(psidGroupPermissionsArray));
            return this;
        }

        public SequenceOfPsidGroupPermissions createSequenceOfPsidGroupPermissions() {
            return new SequenceOfPsidGroupPermissions(this.groupPermissions);
        }
    }
}

