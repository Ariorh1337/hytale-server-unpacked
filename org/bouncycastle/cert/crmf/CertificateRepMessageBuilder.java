/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateRepMessage;
import org.bouncycastle.cert.crmf.CertificateResponse;

public class CertificateRepMessageBuilder {
    private final List<CertResponse> responses = new ArrayList<CertResponse>();
    private final CMPCertificate[] caCerts;

    public CertificateRepMessageBuilder(X509CertificateHolder ... x509CertificateHolderArray) {
        this.caCerts = new CMPCertificate[x509CertificateHolderArray.length];
        for (int i = 0; i != x509CertificateHolderArray.length; ++i) {
            this.caCerts[i] = new CMPCertificate(x509CertificateHolderArray[i].toASN1Structure());
        }
    }

    public CertificateRepMessageBuilder addCertificateResponse(CertificateResponse certificateResponse) {
        this.responses.add(certificateResponse.toASN1Structure());
        return this;
    }

    public CertificateRepMessage build() {
        CertRepMessage certRepMessage = this.caCerts.length != 0 ? new CertRepMessage(this.caCerts, this.responses.toArray(new CertResponse[0])) : new CertRepMessage(null, this.responses.toArray(new CertResponse[0]));
        this.responses.clear();
        return new CertificateRepMessage(certRepMessage);
    }
}

