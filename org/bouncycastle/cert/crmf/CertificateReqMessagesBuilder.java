/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.cert.crmf.CertificateReqMessages;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;

public class CertificateReqMessagesBuilder {
    private final List<CertReqMsg> requests = new ArrayList<CertReqMsg>();

    public void addRequest(CertificateRequestMessage certificateRequestMessage) {
        this.requests.add(certificateRequestMessage.toASN1Structure());
    }

    public CertificateReqMessages build() {
        CertificateReqMessages certificateReqMessages = new CertificateReqMessages(new CertReqMessages(this.requests.toArray(new CertReqMsg[0])));
        this.requests.clear();
        return certificateReqMessages;
    }
}

