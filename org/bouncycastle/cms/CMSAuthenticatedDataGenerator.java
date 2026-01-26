/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAuthenticatedData;
import org.bouncycastle.cms.CMSAuthenticatedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.DefaultAuthenticatedAttributeTableGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataGenerator
extends CMSAuthenticatedGenerator {
    public CMSAuthenticatedData generate(CMSTypedData cMSTypedData, MacCalculator macCalculator) throws CMSException {
        return this.generate(cMSTypedData, macCalculator, null);
    }

    public CMSAuthenticatedData generate(CMSTypedData cMSTypedData, MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        AuthenticatedData authenticatedData;
        Object object;
        ASN1EncodableVector aSN1EncodableVector = CMSUtils.getRecipentInfos(macCalculator.getKey(), this.recipientInfoGenerators);
        if (digestCalculator != null) {
            DEROctetString dEROctetString;
            Object object2;
            BEROctetString bEROctetString;
            Object object3;
            try {
                object = new ByteArrayOutputStream();
                object3 = new TeeOutputStream(digestCalculator.getOutputStream(), (OutputStream)object);
                cMSTypedData.write((OutputStream)object3);
                ((OutputStream)object3).close();
                bEROctetString = new BEROctetString(((ByteArrayOutputStream)object).toByteArray());
            }
            catch (IOException iOException) {
                throw new CMSException("unable to perform digest calculation: " + iOException.getMessage(), iOException);
            }
            object = Collections.unmodifiableMap(this.getBaseParameters(cMSTypedData.getContentType(), digestCalculator.getAlgorithmIdentifier(), macCalculator.getAlgorithmIdentifier(), digestCalculator.getDigest()));
            if (this.authGen == null) {
                this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
            }
            object3 = new DERSet(this.authGen.getAttributes((Map)object).toASN1EncodableVector());
            try {
                object2 = macCalculator.getOutputStream();
                ((OutputStream)object2).write(((ASN1Object)object3).getEncoded("DER"));
                ((OutputStream)object2).close();
                dEROctetString = new DEROctetString(macCalculator.getMac());
            }
            catch (IOException iOException) {
                throw new CMSException("unable to perform MAC calculation: " + iOException.getMessage(), iOException);
            }
            object2 = CMSUtils.getAttrBERSet(this.unauthGen);
            ContentInfo contentInfo = new ContentInfo(cMSTypedData.getContentType(), bEROctetString);
            authenticatedData = new AuthenticatedData(this.originatorInfo, new DERSet(aSN1EncodableVector), macCalculator.getAlgorithmIdentifier(), digestCalculator.getAlgorithmIdentifier(), contentInfo, (ASN1Set)object3, dEROctetString, (ASN1Set)object2);
        } else {
            DEROctetString dEROctetString;
            BEROctetString bEROctetString;
            Object object4;
            try {
                object = new ByteArrayOutputStream();
                object4 = new TeeOutputStream((OutputStream)object, macCalculator.getOutputStream());
                cMSTypedData.write((OutputStream)object4);
                ((OutputStream)object4).close();
                bEROctetString = new BEROctetString(((ByteArrayOutputStream)object).toByteArray());
                dEROctetString = new DEROctetString(macCalculator.getMac());
            }
            catch (IOException iOException) {
                throw new CMSException("unable to perform MAC calculation: " + iOException.getMessage(), iOException);
            }
            object = CMSUtils.getAttrBERSet(this.unauthGen);
            object4 = new ContentInfo(cMSTypedData.getContentType(), bEROctetString);
            authenticatedData = new AuthenticatedData(this.originatorInfo, new DERSet(aSN1EncodableVector), macCalculator.getAlgorithmIdentifier(), null, (ContentInfo)object4, null, dEROctetString, (ASN1Set)object);
        }
        object = new ContentInfo(CMSObjectIdentifiers.authenticatedData, authenticatedData);
        return new CMSAuthenticatedData((ContentInfo)object, new DigestCalculatorProvider(){
            final /* synthetic */ CMSAuthenticatedDataGenerator this$0;
            {
                this.this$0 = cMSAuthenticatedDataGenerator;
            }

            @Override
            public DigestCalculator get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                return digestCalculator;
            }
        });
    }
}

