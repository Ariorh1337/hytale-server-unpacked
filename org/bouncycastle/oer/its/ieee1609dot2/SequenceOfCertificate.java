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
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;

public class SequenceOfCertificate
extends ASN1Object {
    private final List<Certificate> certificates;

    public SequenceOfCertificate(List<Certificate> list) {
        this.certificates = Collections.unmodifiableList(list);
    }

    private SequenceOfCertificate(ASN1Sequence aSN1Sequence) {
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        ArrayList<Certificate> arrayList = new ArrayList<Certificate>();
        while (iterator.hasNext()) {
            arrayList.add(Certificate.getInstance(iterator.next()));
        }
        this.certificates = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfCertificate getInstance(Object object) {
        if (object instanceof SequenceOfCertificate) {
            return (SequenceOfCertificate)object;
        }
        if (object != null) {
            return new SequenceOfCertificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.certificates);
    }

    public List<Certificate> getCertificates() {
        return this.certificates;
    }

    public static class Builder {
        List<Certificate> certificates = new ArrayList<Certificate>();

        public Builder add(Certificate ... certificateArray) {
            this.certificates.addAll(Arrays.asList(certificateArray));
            return this;
        }

        public SequenceOfCertificate build() {
            return new SequenceOfCertificate(this.certificates);
        }
    }
}

