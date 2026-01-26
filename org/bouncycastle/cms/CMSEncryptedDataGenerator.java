/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSEncryptedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEncryptedDataGenerator
extends CMSEncryptedGenerator {
    private CMSEncryptedData doGenerate(CMSTypedData cMSTypedData, OutputEncryptor outputEncryptor) throws CMSException {
        Object object;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            object = outputEncryptor.getOutputStream(byteArrayOutputStream);
            cMSTypedData.write((OutputStream)object);
            ((OutputStream)object).close();
        }
        catch (IOException iOException) {
            throw new CMSException("");
        }
        object = byteArrayOutputStream.toByteArray();
        AlgorithmIdentifier algorithmIdentifier = outputEncryptor.getAlgorithmIdentifier();
        BEROctetString bEROctetString = new BEROctetString((byte[])object);
        EncryptedContentInfo encryptedContentInfo = CMSUtils.getEncryptedContentInfo(cMSTypedData.getContentType(), algorithmIdentifier, (byte[])object);
        ASN1Set aSN1Set = CMSUtils.getAttrBERSet(this.unprotectedAttributeGenerator);
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.encryptedData, new EncryptedData(encryptedContentInfo, aSN1Set));
        return new CMSEncryptedData(contentInfo);
    }

    public CMSEncryptedData generate(CMSTypedData cMSTypedData, OutputEncryptor outputEncryptor) throws CMSException {
        return this.doGenerate(cMSTypedData, outputEncryptor);
    }
}

