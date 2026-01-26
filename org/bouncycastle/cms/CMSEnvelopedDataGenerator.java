/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataGenerator
extends CMSEnvelopedGenerator {
    private CMSEnvelopedData doGenerate(CMSTypedData cMSTypedData, OutputEncryptor outputEncryptor) throws CMSException {
        Object object;
        Object object2;
        ASN1EncodableVector aSN1EncodableVector = CMSUtils.getRecipentInfos(outputEncryptor.getKey(), this.recipientInfoGenerators);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            object2 = outputEncryptor.getOutputStream(byteArrayOutputStream);
            cMSTypedData.write((OutputStream)object2);
            ((OutputStream)object2).close();
            if (outputEncryptor instanceof OutputAEADEncryptor) {
                object = ((OutputAEADEncryptor)outputEncryptor).getMAC();
                byteArrayOutputStream.write((byte[])object, 0, ((byte[])object).length);
            }
        }
        catch (IOException iOException) {
            throw new CMSException("");
        }
        object2 = byteArrayOutputStream.toByteArray();
        object = CMSUtils.getEncryptedContentInfo(cMSTypedData, outputEncryptor, (byte[])object2);
        ASN1Set aSN1Set = CMSUtils.getAttrBERSet(this.unprotectedAttributeGenerator);
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.envelopedData, new EnvelopedData(this.originatorInfo, (ASN1Set)new DERSet(aSN1EncodableVector), (EncryptedContentInfo)object, aSN1Set));
        return new CMSEnvelopedData(contentInfo);
    }

    public CMSEnvelopedData generate(CMSTypedData cMSTypedData, OutputEncryptor outputEncryptor) throws CMSException {
        return this.doGenerate(cMSTypedData, outputEncryptor);
    }
}

