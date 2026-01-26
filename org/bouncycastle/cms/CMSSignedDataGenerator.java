/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CMSSignedDataGenerator
extends CMSSignedGenerator {
    private boolean isDefiniteLength = false;

    public CMSSignedDataGenerator() {
    }

    public CMSSignedDataGenerator(DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        super(digestAlgorithmIdentifierFinder);
    }

    public void setDefiniteLengthEncoding(boolean bl) {
        this.isDefiniteLength = bl;
    }

    public CMSSignedData generate(CMSTypedData cMSTypedData) throws CMSException {
        return this.generate(cMSTypedData, false);
    }

    public CMSSignedData generate(CMSTypedData cMSTypedData, boolean bl) throws CMSException {
        ASN1Object aSN1Object;
        Object object4;
        Object object2;
        Object object32;
        LinkedHashSet<AlgorithmIdentifier> linkedHashSet = new LinkedHashSet<AlgorithmIdentifier>();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        this.digests.clear();
        for (Object object32 : this._signers) {
            CMSUtils.addDigestAlgs(linkedHashSet, (SignerInformation)object32, this.digestAlgIdFinder);
            aSN1EncodableVector.add(((SignerInformation)object32).toASN1Structure());
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = cMSTypedData.getContentType();
        object32 = null;
        if (cMSTypedData.getContent() != null) {
            if (bl) {
                object2 = new ByteArrayOutputStream();
                this.writeContentViaSignerGens(cMSTypedData, (OutputStream)object2);
                object32 = this.isDefiniteLength ? new DEROctetString(((ByteArrayOutputStream)object2).toByteArray()) : new BEROctetString(((ByteArrayOutputStream)object2).toByteArray());
            } else {
                this.writeContentViaSignerGens(cMSTypedData, null);
            }
        }
        for (Object object4 : this.signerGens) {
            aSN1Object = this.generateSignerInfo((SignerInfoGenerator)object4, aSN1ObjectIdentifier);
            linkedHashSet.add(aSN1Object.getDigestAlgorithm());
            aSN1EncodableVector.add(aSN1Object);
        }
        object2 = CMSSignedDataGenerator.createSetFromList(this.certs, this.isDefiniteLength);
        object4 = CMSSignedDataGenerator.createSetFromList(this.crls, this.isDefiniteLength);
        aSN1Object = new ContentInfo(aSN1ObjectIdentifier, (ASN1Encodable)object32);
        SignedData signedData = new SignedData(CMSUtils.convertToDlSet(linkedHashSet), (ContentInfo)aSN1Object, (ASN1Set)object2, (ASN1Set)object4, new DERSet(aSN1EncodableVector));
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.signedData, signedData);
        return new CMSSignedData((CMSProcessable)cMSTypedData, contentInfo);
    }

    public SignerInformationStore generateCounterSigners(SignerInformation signerInformation) throws CMSException {
        SignerInfo signerInfo;
        this.digests.clear();
        CMSProcessableByteArray cMSProcessableByteArray = new CMSProcessableByteArray(null, signerInformation.getSignature());
        ArrayList<SignerInformation> arrayList = new ArrayList<SignerInformation>();
        for (Object object : this._signers) {
            signerInfo = ((SignerInformation)object).toASN1Structure();
            arrayList.add(new SignerInformation(signerInfo, null, cMSProcessableByteArray, null));
        }
        this.writeContentViaSignerGens(cMSProcessableByteArray, null);
        for (Object object : this.signerGens) {
            signerInfo = this.generateSignerInfo((SignerInfoGenerator)object, null);
            arrayList.add(new SignerInformation(signerInfo, null, cMSProcessableByteArray, null));
        }
        return new SignerInformationStore(arrayList);
    }

    private SignerInfo generateSignerInfo(SignerInfoGenerator signerInfoGenerator, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        SignerInfo signerInfo = signerInfoGenerator.generate(aSN1ObjectIdentifier);
        byte[] byArray = signerInfoGenerator.getCalculatedDigest();
        if (byArray != null) {
            this.digests.put(signerInfo.getDigestAlgorithm().getAlgorithm().getId(), byArray);
        }
        return signerInfo;
    }

    private void writeContentViaSignerGens(CMSTypedData cMSTypedData, OutputStream outputStream) throws CMSException {
        OutputStream outputStream2 = CMSUtils.attachSignersToOutputStream(this.signerGens, outputStream);
        outputStream2 = CMSUtils.getSafeOutputStream(outputStream2);
        try {
            cMSTypedData.write(outputStream2);
            outputStream2.close();
        }
        catch (IOException iOException) {
            throw new CMSException("data processing exception: " + iOException.getMessage(), iOException);
        }
    }

    private static ASN1Set createSetFromList(List list, boolean bl) {
        return list.size() < 1 ? null : (bl ? CMSUtils.createDlSetFromList(list) : CMSUtils.createBerSetFromList(list));
    }
}

