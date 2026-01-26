/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;

public class CertificateReqMessages {
    private final CertReqMsg[] reqs;

    public CertificateReqMessages(CertReqMessages certReqMessages) {
        this.reqs = certReqMessages.toCertReqMsgArray();
    }

    public static CertificateReqMessages fromPKIBody(PKIBody pKIBody) {
        if (!CertificateReqMessages.isCertificateRequestMessages(pKIBody.getType())) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
        }
        return new CertificateReqMessages(CertReqMessages.getInstance(pKIBody.getContent()));
    }

    public static boolean isCertificateRequestMessages(int n) {
        switch (n) {
            case 0: 
            case 2: 
            case 7: 
            case 9: 
            case 13: {
                return true;
            }
        }
        return false;
    }

    public CertificateRequestMessage[] getRequests() {
        CertificateRequestMessage[] certificateRequestMessageArray = new CertificateRequestMessage[this.reqs.length];
        for (int i = 0; i != certificateRequestMessageArray.length; ++i) {
            certificateRequestMessageArray[i] = new CertificateRequestMessage(this.reqs[i]);
        }
        return certificateRequestMessageArray;
    }

    public CertReqMessages toASN1Structure() {
        return new CertReqMessages(this.reqs);
    }
}

