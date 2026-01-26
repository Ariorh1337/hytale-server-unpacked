/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadableWithAAD;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.InputStreamWithMAC;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSAuthEnvelopedData
implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private OriginatorInformation originatorInfo;
    private AlgorithmIdentifier authEncAlg;
    private ASN1Set authAttrs;
    private byte[] mac;
    private ASN1Set unauthAttrs;

    public CMSAuthEnvelopedData(byte[] byArray) throws CMSException {
        this(CMSUtils.readContentInfo(byArray));
    }

    public CMSAuthEnvelopedData(InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }

    public CMSAuthEnvelopedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        AuthEnvelopedData authEnvelopedData = AuthEnvelopedData.getInstance(contentInfo.getContent());
        if (authEnvelopedData.getOriginatorInfo() != null) {
            this.originatorInfo = new OriginatorInformation(authEnvelopedData.getOriginatorInfo());
        }
        ASN1Set aSN1Set = authEnvelopedData.getRecipientInfos();
        final EncryptedContentInfo encryptedContentInfo = authEnvelopedData.getAuthEncryptedContentInfo();
        this.authEncAlg = encryptedContentInfo.getContentEncryptionAlgorithm();
        this.mac = authEnvelopedData.getMac().getOctets();
        CMSSecureReadableWithAAD cMSSecureReadableWithAAD = new CMSSecureReadableWithAAD(){
            private OutputStream aadStream;
            final /* synthetic */ CMSAuthEnvelopedData this$0;
            {
                this.this$0 = cMSAuthEnvelopedData;
            }

            @Override
            public ASN1Set getAuthAttrSet() {
                return this.this$0.authAttrs;
            }

            @Override
            public void setAuthAttrSet(ASN1Set aSN1Set) {
            }

            @Override
            public boolean hasAdditionalData() {
                return this.aadStream != null && this.this$0.authAttrs != null;
            }

            @Override
            public ASN1ObjectIdentifier getContentType() {
                return encryptedContentInfo.getContentType();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                if (this.aadStream != null && this.this$0.authAttrs != null) {
                    this.aadStream.write(this.this$0.authAttrs.getEncoded("DER"));
                }
                return new InputStreamWithMAC((InputStream)new ByteArrayInputStream(encryptedContentInfo.getEncryptedContent().getOctets()), this.this$0.mac);
            }

            @Override
            public void setAADStream(OutputStream outputStream) {
                this.aadStream = outputStream;
            }

            @Override
            public OutputStream getAADStream() {
                return this.aadStream;
            }

            @Override
            public byte[] getMAC() {
                return Arrays.clone(this.this$0.mac);
            }
        };
        this.authAttrs = authEnvelopedData.getAuthAttrs();
        this.unauthAttrs = authEnvelopedData.getUnauthAttrs();
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.authEncAlg, cMSSecureReadableWithAAD);
    }

    public String getEncryptionAlgOID() {
        return this.authEncAlg.getAlgorithm().getId();
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public AttributeTable getAuthAttrs() {
        if (this.authAttrs == null) {
            return null;
        }
        return new AttributeTable(this.authAttrs);
    }

    public AttributeTable getUnauthAttrs() {
        if (this.unauthAttrs == null) {
            return null;
        }
        return new AttributeTable(this.unauthAttrs);
    }

    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}

