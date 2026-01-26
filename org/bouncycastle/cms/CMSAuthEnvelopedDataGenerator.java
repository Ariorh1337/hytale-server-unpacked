/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSAuthEnvelopedData;
import org.bouncycastle.cms.CMSAuthEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.OutputAEADEncryptor;

public class CMSAuthEnvelopedDataGenerator
extends CMSAuthEnvelopedGenerator {
    private CMSAuthEnvelopedData doGenerate(CMSTypedData cMSTypedData, OutputAEADEncryptor outputAEADEncryptor) throws CMSException {
        ASN1Set aSN1Set;
        Object object;
        ASN1EncodableVector aSN1EncodableVector = CMSUtils.getRecipentInfos(outputAEADEncryptor.getKey(), this.recipientInfoGenerators);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            object = outputAEADEncryptor.getOutputStream(byteArrayOutputStream);
            if (CMSAlgorithm.ChaCha20Poly1305.equals(outputAEADEncryptor.getAlgorithmIdentifier().getAlgorithm())) {
                aSN1Set = CMSUtils.processAuthAttrSet(this.authAttrsGenerator, outputAEADEncryptor);
                cMSTypedData.write((OutputStream)object);
            } else {
                cMSTypedData.write((OutputStream)object);
                aSN1Set = CMSUtils.processAuthAttrSet(this.authAttrsGenerator, outputAEADEncryptor);
            }
            ((OutputStream)object).close();
        }
        catch (IOException iOException) {
            throw new CMSException("unable to process authenticated content: " + iOException.getMessage(), iOException);
        }
        object = byteArrayOutputStream.toByteArray();
        byte[] byArray = outputAEADEncryptor.getMAC();
        EncryptedContentInfo encryptedContentInfo = CMSUtils.getEncryptedContentInfo(cMSTypedData, outputAEADEncryptor, (byte[])object);
        ASN1Set aSN1Set2 = CMSUtils.getAttrDLSet(this.unauthAttrsGenerator);
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.authEnvelopedData, new AuthEnvelopedData(this.originatorInfo, new DERSet(aSN1EncodableVector), encryptedContentInfo, aSN1Set, new DEROctetString(byArray), aSN1Set2));
        return new CMSAuthEnvelopedData(contentInfo);
    }

    public CMSAuthEnvelopedData generate(CMSTypedData cMSTypedData, OutputAEADEncryptor outputAEADEncryptor) throws CMSException {
        return this.doGenerate(cMSTypedData, outputAEADEncryptor);
    }
}

