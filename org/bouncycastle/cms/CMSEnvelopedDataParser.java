/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.EnvelopedDataParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableInputStream;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class CMSEnvelopedDataParser
extends CMSContentInfoParser {
    RecipientInformationStore recipientInfoStore;
    EnvelopedDataParser envelopedData = new EnvelopedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
    private AlgorithmIdentifier encAlg;
    private AttributeTable unprotectedAttributes;
    private boolean attrNotRead = true;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedDataParser(byte[] byArray) throws CMSException, IOException {
        this(new ByteArrayInputStream(byArray));
    }

    public CMSEnvelopedDataParser(InputStream inputStream) throws CMSException, IOException {
        super(inputStream);
        OriginatorInfo originatorInfo = this.envelopedData.getOriginatorInfo();
        if (originatorInfo != null) {
            this.originatorInfo = new OriginatorInformation(originatorInfo);
        }
        ASN1Set aSN1Set = ASN1Set.getInstance(this.envelopedData.getRecipientInfos().toASN1Primitive());
        EncryptedContentInfoParser encryptedContentInfoParser = this.envelopedData.getEncryptedContentInfo();
        this.encAlg = encryptedContentInfoParser.getContentEncryptionAlgorithm();
        CMSProcessableInputStream cMSProcessableInputStream = new CMSProcessableInputStream(((ASN1OctetStringParser)encryptedContentInfoParser.getEncryptedContent(4)).getOctetStream());
        CMSEnvelopedHelper.CMSAuthEnveSecureReadable cMSAuthEnveSecureReadable = new CMSEnvelopedHelper.CMSAuthEnveSecureReadable(this.encAlg, encryptedContentInfoParser.getContentType(), cMSProcessableInputStream);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.encAlg, cMSAuthEnveSecureReadable);
    }

    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().toString();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return CMSUtils.encodeObj(this.encAlg.getParameters());
        }
        catch (Exception exception) {
            throw new RuntimeException("exception getting encryption parameters " + exception);
        }
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public AttributeTable getUnprotectedAttributes() throws IOException {
        if (this.unprotectedAttributes == null && this.attrNotRead) {
            this.attrNotRead = false;
            this.unprotectedAttributes = CMSUtils.getAttributesTable(this.envelopedData.getUnprotectedAttrs());
        }
        return this.unprotectedAttributes;
    }
}

