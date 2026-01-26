/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.util.Collection;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class CertificateResponse {
    private final CertResponse certResponse;

    public CertificateResponse(CertResponse certResponse) {
        this.certResponse = certResponse;
    }

    public boolean hasEncryptedCertificate() {
        return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().hasEncryptedCertificate();
    }

    public CMSEnvelopedData getEncryptedCertificate() throws CMSException {
        if (!this.hasEncryptedCertificate()) {
            throw new IllegalStateException("encrypted certificate asked for, none found");
        }
        CertifiedKeyPair certifiedKeyPair = this.certResponse.getCertifiedKeyPair();
        CMSEnvelopedData cMSEnvelopedData = new CMSEnvelopedData(new ContentInfo(PKCSObjectIdentifiers.envelopedData, certifiedKeyPair.getCertOrEncCert().getEncryptedCert().getValue()));
        if (cMSEnvelopedData.getRecipientInfos().size() != 1) {
            throw new IllegalStateException("data encrypted for more than one recipient");
        }
        return cMSEnvelopedData;
    }

    public CMPCertificate getCertificate(Recipient recipient) throws CMSException {
        CMSEnvelopedData cMSEnvelopedData = this.getEncryptedCertificate();
        RecipientInformationStore recipientInformationStore = cMSEnvelopedData.getRecipientInfos();
        Collection<RecipientInformation> collection = recipientInformationStore.getRecipients();
        RecipientInformation recipientInformation = collection.iterator().next();
        return CMPCertificate.getInstance(recipientInformation.getContent(recipient));
    }

    public CMPCertificate getCertificate() throws CMSException {
        if (this.hasEncryptedCertificate()) {
            throw new IllegalStateException("plaintext certificate asked for, none found");
        }
        return this.certResponse.getCertifiedKeyPair().getCertOrEncCert().getCertificate();
    }

    public CertResponse toASN1Structure() {
        return this.certResponse;
    }
}

