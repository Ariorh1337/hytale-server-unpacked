/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.util.Store;

public class OriginatorInfoGenerator {
    private final List origCerts;
    private final List origCRLs;

    public OriginatorInfoGenerator(X509CertificateHolder x509CertificateHolder) {
        this.origCerts = new ArrayList(1);
        this.origCRLs = null;
        this.origCerts.add(x509CertificateHolder.toASN1Structure());
    }

    public OriginatorInfoGenerator(Store store) throws CMSException {
        this(store, null);
    }

    public OriginatorInfoGenerator(Store store, Store store2) throws CMSException {
        this.origCerts = store != null ? CMSUtils.getCertificatesFromStore(store) : null;
        this.origCRLs = store2 != null ? CMSUtils.getCRLsFromStore(store2) : null;
    }

    public OriginatorInformation generate() {
        ASN1Set aSN1Set = this.origCerts == null ? null : CMSUtils.createDerSetFromList(this.origCerts);
        ASN1Set aSN1Set2 = this.origCRLs == null ? null : CMSUtils.createDerSetFromList(this.origCRLs);
        return new OriginatorInformation(new OriginatorInfo(aSN1Set, aSN1Set2));
    }
}

