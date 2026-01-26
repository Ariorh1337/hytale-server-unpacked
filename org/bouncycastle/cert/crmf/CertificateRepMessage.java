/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateResponse;

public class CertificateRepMessage {
    private final CertResponse[] resps;
    private final CMPCertificate[] caCerts;

    public CertificateRepMessage(CertRepMessage certRepMessage) {
        this.resps = certRepMessage.getResponse();
        this.caCerts = certRepMessage.getCaPubs();
    }

    public static CertificateRepMessage fromPKIBody(PKIBody pKIBody) {
        if (!CertificateRepMessage.isCertificateRepMessage(pKIBody.getType())) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
        }
        return new CertificateRepMessage(CertRepMessage.getInstance(pKIBody.getContent()));
    }

    public static boolean isCertificateRepMessage(int n) {
        switch (n) {
            case 1: 
            case 3: 
            case 8: 
            case 14: {
                return true;
            }
        }
        return false;
    }

    public CertificateResponse[] getResponses() {
        CertificateResponse[] certificateResponseArray = new CertificateResponse[this.resps.length];
        for (int i = 0; i != certificateResponseArray.length; ++i) {
            certificateResponseArray[i] = new CertificateResponse(this.resps[i]);
        }
        return certificateResponseArray;
    }

    public X509CertificateHolder[] getX509Certificates() {
        ArrayList<X509CertificateHolder> arrayList = new ArrayList<X509CertificateHolder>();
        for (int i = 0; i != this.caCerts.length; ++i) {
            if (!this.caCerts[i].isX509v3PKCert()) continue;
            arrayList.add(new X509CertificateHolder(this.caCerts[i].getX509v3PKCert()));
        }
        return arrayList.toArray(new X509CertificateHolder[0]);
    }

    public boolean isOnlyX509PKCertificates() {
        boolean bl = true;
        for (int i = 0; i != this.caCerts.length; ++i) {
            bl &= this.caCerts[i].isX509v3PKCert();
        }
        return bl;
    }

    public CMPCertificate[] getCMPCertificates() {
        CMPCertificate[] cMPCertificateArray = new CMPCertificate[this.caCerts.length];
        System.arraycopy(this.caCerts, 0, cMPCertificateArray, 0, cMPCertificateArray.length);
        return cMPCertificateArray;
    }

    public CertRepMessage toASN1Structure() {
        return new CertRepMessage(this.caCerts, this.resps);
    }
}

