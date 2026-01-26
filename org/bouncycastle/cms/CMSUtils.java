/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.NullOutputStream;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

class CMSUtils {
    private static final Set<String> des = new HashSet<String>();
    private static final Set mqvAlgs = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();

    CMSUtils() {
    }

    static boolean isMQV(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return mqvAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isEC(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ecAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isGOST(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return gostAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isRFC2631(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_ESDH) || aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_SSDH);
    }

    static boolean isDES(String string) {
        String string2 = Strings.toUpperCase(string);
        return des.contains(string2);
    }

    static boolean isEquivalent(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        if (algorithmIdentifier == null || algorithmIdentifier2 == null) {
            return false;
        }
        if (!algorithmIdentifier.getAlgorithm().equals(algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        ASN1Encodable aSN1Encodable = algorithmIdentifier.getParameters();
        ASN1Encodable aSN1Encodable2 = algorithmIdentifier2.getParameters();
        if (aSN1Encodable != null) {
            return aSN1Encodable.equals(aSN1Encodable2) || aSN1Encodable.equals(DERNull.INSTANCE) && aSN1Encodable2 == null;
        }
        return aSN1Encodable2 == null || aSN1Encodable2.equals(DERNull.INSTANCE);
    }

    static ContentInfo readContentInfo(byte[] byArray) throws CMSException {
        return CMSUtils.readContentInfo(new ASN1InputStream(byArray));
    }

    static ContentInfo readContentInfo(InputStream inputStream) throws CMSException {
        return CMSUtils.readContentInfo(new ASN1InputStream(inputStream));
    }

    static ASN1Set convertToDlSet(Set<AlgorithmIdentifier> set) {
        return new DLSet(set.toArray(new AlgorithmIdentifier[set.size()]));
    }

    static void addDigestAlgs(Set<AlgorithmIdentifier> set, SignerInformation signerInformation, DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        set.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(signerInformation.getDigestAlgorithmID(), digestAlgorithmIdentifierFinder));
        SignerInformationStore signerInformationStore = signerInformation.getCounterSignatures();
        for (SignerInformation signerInformation2 : signerInformationStore) {
            set.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(signerInformation2.getDigestAlgorithmID(), digestAlgorithmIdentifierFinder));
        }
    }

    static List getCertificatesFromStore(Store store) throws CMSException {
        ArrayList<Certificate> arrayList = new ArrayList<Certificate>();
        try {
            for (X509CertificateHolder x509CertificateHolder : store.getMatches(null)) {
                arrayList.add(x509CertificateHolder.toASN1Structure());
            }
            return arrayList;
        }
        catch (ClassCastException classCastException) {
            throw new CMSException("error processing certs", classCastException);
        }
    }

    static List getAttributeCertificatesFromStore(Store store) throws CMSException {
        ArrayList<DERTaggedObject> arrayList = new ArrayList<DERTaggedObject>();
        try {
            for (X509AttributeCertificateHolder x509AttributeCertificateHolder : store.getMatches(null)) {
                arrayList.add(new DERTaggedObject(false, 2, (ASN1Encodable)x509AttributeCertificateHolder.toASN1Structure()));
            }
            return arrayList;
        }
        catch (ClassCastException classCastException) {
            throw new CMSException("error processing certs", classCastException);
        }
    }

    static List getCRLsFromStore(Store store) throws CMSException {
        ArrayList<ASN1Object> arrayList = new ArrayList<ASN1Object>();
        try {
            for (Object t : store.getMatches(null)) {
                Encodable encodable;
                if (t instanceof X509CRLHolder) {
                    encodable = (X509CRLHolder)t;
                    arrayList.add(encodable.toASN1Structure());
                    continue;
                }
                if (t instanceof OtherRevocationInfoFormat) {
                    encodable = OtherRevocationInfoFormat.getInstance(t);
                    CMSUtils.validateInfoFormat((OtherRevocationInfoFormat)encodable);
                    arrayList.add(new DERTaggedObject(false, 1, (ASN1Encodable)((Object)encodable)));
                    continue;
                }
                if (!(t instanceof ASN1TaggedObject)) continue;
                arrayList.add((ASN1Object)t);
            }
            return arrayList;
        }
        catch (ClassCastException classCastException) {
            throw new CMSException("error processing certs", classCastException);
        }
    }

    static void validateInfoFormat(OtherRevocationInfoFormat otherRevocationInfoFormat) {
        OCSPResponse oCSPResponse;
        if (CMSObjectIdentifiers.id_ri_ocsp_response.equals(otherRevocationInfoFormat.getInfoFormat()) && 0 != (oCSPResponse = OCSPResponse.getInstance(otherRevocationInfoFormat.getInfo())).getResponseStatus().getIntValue()) {
            throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData");
        }
    }

    static Collection getOthersFromStore(ASN1ObjectIdentifier aSN1ObjectIdentifier, Store store) {
        ArrayList<DERTaggedObject> arrayList = new ArrayList<DERTaggedObject>();
        for (ASN1Encodable aSN1Encodable : store.getMatches(null)) {
            OtherRevocationInfoFormat otherRevocationInfoFormat = new OtherRevocationInfoFormat(aSN1ObjectIdentifier, aSN1Encodable);
            CMSUtils.validateInfoFormat(otherRevocationInfoFormat);
            arrayList.add(new DERTaggedObject(false, 1, (ASN1Encodable)otherRevocationInfoFormat));
        }
        return arrayList;
    }

    static ASN1Set createBerSetFromList(List list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            aSN1EncodableVector.add((ASN1Encodable)iterator.next());
        }
        return new BERSet(aSN1EncodableVector);
    }

    static ASN1Set createDlSetFromList(List list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            aSN1EncodableVector.add((ASN1Encodable)iterator.next());
        }
        return new DLSet(aSN1EncodableVector);
    }

    static ASN1Set createDerSetFromList(List list) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            aSN1EncodableVector.add((ASN1Encodable)iterator.next());
        }
        return new DERSet(aSN1EncodableVector);
    }

    static OutputStream createBEROctetOutputStream(OutputStream outputStream, int n, boolean bl, int n2) throws IOException {
        BEROctetStringGenerator bEROctetStringGenerator = new BEROctetStringGenerator(outputStream, n, bl);
        if (n2 != 0) {
            return bEROctetStringGenerator.getOctetOutputStream(new byte[n2]);
        }
        return bEROctetStringGenerator.getOctetOutputStream();
    }

    private static ContentInfo readContentInfo(ASN1InputStream aSN1InputStream) throws CMSException {
        try {
            ContentInfo contentInfo = ContentInfo.getInstance(aSN1InputStream.readObject());
            if (contentInfo == null) {
                throw new CMSException("No content found.");
            }
            return contentInfo;
        }
        catch (IOException iOException) {
            throw new CMSException("IOException reading content.", iOException);
        }
        catch (ClassCastException classCastException) {
            throw new CMSException("Malformed content.", classCastException);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new CMSException("Malformed content.", illegalArgumentException);
        }
    }

    public static byte[] streamToByteArray(InputStream inputStream) throws IOException {
        return Streams.readAll(inputStream);
    }

    public static byte[] streamToByteArray(InputStream inputStream, int n) throws IOException {
        return Streams.readAllLimited(inputStream, n);
    }

    static InputStream attachDigestsToInputStream(Collection collection, InputStream inputStream) {
        InputStream inputStream2 = inputStream;
        for (DigestCalculator digestCalculator : collection) {
            inputStream2 = new TeeInputStream(inputStream2, digestCalculator.getOutputStream());
        }
        return inputStream2;
    }

    static OutputStream attachSignersToOutputStream(Collection collection, OutputStream outputStream) {
        OutputStream outputStream2 = outputStream;
        for (SignerInfoGenerator signerInfoGenerator : collection) {
            outputStream2 = CMSUtils.getSafeTeeOutputStream(outputStream2, signerInfoGenerator.getCalculatingOutputStream());
        }
        return outputStream2;
    }

    static OutputStream getSafeOutputStream(OutputStream outputStream) {
        return outputStream == null ? new NullOutputStream() : outputStream;
    }

    static OutputStream getSafeTeeOutputStream(OutputStream outputStream, OutputStream outputStream2) {
        return outputStream == null ? CMSUtils.getSafeOutputStream(outputStream2) : (outputStream2 == null ? CMSUtils.getSafeOutputStream(outputStream) : new TeeOutputStream(outputStream, outputStream2));
    }

    static EncryptedContentInfo getEncryptedContentInfo(CMSTypedData cMSTypedData, OutputEncryptor outputEncryptor, byte[] byArray) {
        return CMSUtils.getEncryptedContentInfo(cMSTypedData.getContentType(), outputEncryptor.getAlgorithmIdentifier(), byArray);
    }

    static EncryptedContentInfo getEncryptedContentInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        BEROctetString bEROctetString = new BEROctetString(byArray);
        return new EncryptedContentInfo(aSN1ObjectIdentifier, algorithmIdentifier, bEROctetString);
    }

    static ASN1EncodableVector getRecipentInfos(GenericKey genericKey, List list) throws CMSException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (RecipientInfoGenerator recipientInfoGenerator : list) {
            aSN1EncodableVector.add(recipientInfoGenerator.generate(genericKey));
        }
        return aSN1EncodableVector;
    }

    static void addRecipientInfosToGenerator(ASN1EncodableVector aSN1EncodableVector, BERSequenceGenerator bERSequenceGenerator, boolean bl) throws IOException {
        if (bl) {
            bERSequenceGenerator.getRawOutputStream().write(new BERSet(aSN1EncodableVector).getEncoded());
        } else {
            bERSequenceGenerator.getRawOutputStream().write(new DERSet(aSN1EncodableVector).getEncoded());
        }
    }

    static void addOriginatorInfoToGenerator(BERSequenceGenerator bERSequenceGenerator, OriginatorInfo originatorInfo) throws IOException {
        if (originatorInfo != null) {
            bERSequenceGenerator.addObject(new DERTaggedObject(false, 0, (ASN1Encodable)originatorInfo));
        }
    }

    static void addAttriSetToGenerator(BERSequenceGenerator bERSequenceGenerator, CMSAttributeTableGenerator cMSAttributeTableGenerator, int n, Map map) throws IOException {
        if (cMSAttributeTableGenerator != null) {
            bERSequenceGenerator.addObject(new DERTaggedObject(false, n, (ASN1Encodable)new BERSet(cMSAttributeTableGenerator.getAttributes(map).toASN1EncodableVector())));
        }
    }

    static ASN1Set processAuthAttrSet(CMSAttributeTableGenerator cMSAttributeTableGenerator, OutputAEADEncryptor outputAEADEncryptor) throws IOException {
        DERSet dERSet = null;
        if (cMSAttributeTableGenerator != null) {
            AttributeTable attributeTable = cMSAttributeTableGenerator.getAttributes(Collections.EMPTY_MAP);
            dERSet = new DERSet(attributeTable.toASN1EncodableVector());
            outputAEADEncryptor.getAADStream().write(dERSet.getEncoded("DER"));
        }
        return dERSet;
    }

    static AttributeTable getAttributesTable(ASN1SetParser aSN1SetParser) throws IOException {
        if (aSN1SetParser != null) {
            ASN1Encodable aSN1Encodable;
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            while ((aSN1Encodable = aSN1SetParser.readObject()) != null) {
                ASN1SequenceParser aSN1SequenceParser = (ASN1SequenceParser)aSN1Encodable;
                aSN1EncodableVector.add(aSN1SequenceParser.toASN1Primitive());
            }
            return new AttributeTable(new DERSet(aSN1EncodableVector));
        }
        return null;
    }

    static ASN1Set getAttrDLSet(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        return cMSAttributeTableGenerator != null ? new DLSet(cMSAttributeTableGenerator.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector()) : null;
    }

    static ASN1Set getAttrBERSet(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        return cMSAttributeTableGenerator != null ? new BERSet(cMSAttributeTableGenerator.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector()) : null;
    }

    static byte[] encodeObj(ASN1Encodable aSN1Encodable) throws IOException {
        if (aSN1Encodable != null) {
            return aSN1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }

    static {
        des.add("DES");
        des.add("DESEDE");
        des.add(OIWObjectIdentifiers.desCBC.getId());
        des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
        mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
    }
}

