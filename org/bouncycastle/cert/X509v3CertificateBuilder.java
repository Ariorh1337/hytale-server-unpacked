/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.DeltaCertificateTool;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

public class X509v3CertificateBuilder {
    private V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
    private ExtensionsGenerator extGenerator;

    public X509v3CertificateBuilder(X500Name x500Name, BigInteger bigInteger, Date date, Date date2, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date), new Time(date2), x500Name2, subjectPublicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name x500Name, BigInteger bigInteger, Date date, Date date2, Locale locale, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date, locale), new Time(date2, locale), x500Name2, subjectPublicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name x500Name, BigInteger bigInteger, Time time, Time time2, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.tbsGen.setSerialNumber(new ASN1Integer(bigInteger));
        this.tbsGen.setIssuer(x500Name);
        this.tbsGen.setStartDate(time);
        this.tbsGen.setEndDate(time2);
        this.tbsGen.setSubject(x500Name2);
        this.tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);
        this.extGenerator = new ExtensionsGenerator();
    }

    public X509v3CertificateBuilder(X509CertificateHolder x509CertificateHolder) {
        this.tbsGen.setSerialNumber(new ASN1Integer(x509CertificateHolder.getSerialNumber()));
        this.tbsGen.setIssuer(x509CertificateHolder.getIssuer());
        this.tbsGen.setStartDate(new Time(x509CertificateHolder.getNotBefore()));
        this.tbsGen.setEndDate(new Time(x509CertificateHolder.getNotAfter()));
        this.tbsGen.setSubject(x509CertificateHolder.getSubject());
        this.tbsGen.setSubjectPublicKeyInfo(x509CertificateHolder.getSubjectPublicKeyInfo());
        this.extGenerator = new ExtensionsGenerator();
        Extensions extensions = x509CertificateHolder.getExtensions();
        Enumeration enumeration = extensions.oids();
        while (enumeration.hasMoreElements()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
            if (Extension.subjectAltPublicKeyInfo.equals(aSN1ObjectIdentifier) || Extension.altSignatureAlgorithm.equals(aSN1ObjectIdentifier) || Extension.altSignatureValue.equals(aSN1ObjectIdentifier)) continue;
            this.extGenerator.addExtension(extensions.getExtension(aSN1ObjectIdentifier));
        }
    }

    public boolean hasExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.doGetExtension(aSN1ObjectIdentifier) != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.doGetExtension(aSN1ObjectIdentifier);
    }

    private Extension doGetExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (this.extGenerator.isEmpty()) {
            return null;
        }
        Extensions extensions = this.extGenerator.generate();
        return extensions.getExtension(aSN1ObjectIdentifier);
    }

    public X509v3CertificateBuilder setSubjectUniqueID(boolean[] blArray) {
        this.tbsGen.setSubjectUniqueID(X509v3CertificateBuilder.booleanToBitString(blArray));
        return this;
    }

    public X509v3CertificateBuilder setIssuerUniqueID(boolean[] blArray) {
        this.tbsGen.setIssuerUniqueID(X509v3CertificateBuilder.booleanToBitString(blArray));
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        try {
            this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, aSN1Encodable);
        }
        catch (IOException iOException) {
            throw new CertIOException("cannot encode extension: " + iOException.getMessage(), iOException);
        }
        return this;
    }

    public X509v3CertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) throws CertIOException {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, byArray);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, bl, (ASN1OctetString)new DEROctetString(aSN1Encodable)));
        }
        catch (IOException iOException) {
            throw new CertIOException("cannot encode extension: " + iOException.getMessage(), iOException);
        }
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, bl, byArray));
        return this;
    }

    public X509v3CertificateBuilder removeExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, aSN1ObjectIdentifier);
        return this;
    }

    public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, X509CertificateHolder x509CertificateHolder) {
        Certificate certificate = x509CertificateHolder.toASN1Structure();
        Extension extension = certificate.getTBSCertificate().getExtensions().getExtension(aSN1ObjectIdentifier);
        if (extension == null) {
            throw new NullPointerException("extension " + aSN1ObjectIdentifier + " not present");
        }
        this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, extension.getExtnValue().getOctets());
        return this;
    }

    public X509CertificateHolder build(ContentSigner contentSigner) {
        Object object;
        ASN1Object aSN1Object;
        AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
        this.tbsGen.setSignature(algorithmIdentifier);
        if (!this.extGenerator.isEmpty()) {
            aSN1Object = this.extGenerator.getExtension(Extension.deltaCertificateDescriptor);
            if (aSN1Object != null) {
                object = DeltaCertificateTool.trimDeltaCertificateDescriptor(DeltaCertificateDescriptor.getInstance(((Extension)aSN1Object).getParsedValue()), this.tbsGen.generateTBSCertificate(), this.extGenerator.generate());
                try {
                    this.extGenerator.replaceExtension(Extension.deltaCertificateDescriptor, ((Extension)aSN1Object).isCritical(), (ASN1Encodable)object);
                }
                catch (IOException iOException) {
                    throw new IllegalStateException("unable to replace deltaCertificateDescriptor: " + iOException.getMessage());
                }
            }
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        try {
            aSN1Object = this.tbsGen.generateTBSCertificate();
            object = X509v3CertificateBuilder.generateSig(contentSigner, aSN1Object);
            return new X509CertificateHolder(X509v3CertificateBuilder.generateStructure((TBSCertificate)aSN1Object, algorithmIdentifier, object));
        }
        catch (IOException iOException) {
            throw Exceptions.illegalArgumentException("cannot produce certificate signature", iOException);
        }
    }

    public X509CertificateHolder build(ContentSigner contentSigner, boolean bl, ContentSigner contentSigner2) {
        ASN1Object aSN1Object;
        Object object;
        AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
        AlgorithmIdentifier algorithmIdentifier2 = contentSigner2.getAlgorithmIdentifier();
        try {
            this.extGenerator.addExtension(Extension.altSignatureAlgorithm, bl, algorithmIdentifier2);
        }
        catch (IOException iOException) {
            throw Exceptions.illegalStateException("cannot add altSignatureAlgorithm extension", iOException);
        }
        Extension extension = this.extGenerator.getExtension(Extension.deltaCertificateDescriptor);
        if (extension != null) {
            this.tbsGen.setSignature(algorithmIdentifier);
            try {
                object = new ExtensionsGenerator();
                ((ExtensionsGenerator)object).addExtensions(this.extGenerator.generate());
                ((ExtensionsGenerator)object).addExtension(Extension.altSignatureValue, false, DERNull.INSTANCE);
                aSN1Object = DeltaCertificateTool.trimDeltaCertificateDescriptor(DeltaCertificateDescriptor.getInstance(extension.getParsedValue()), this.tbsGen.generateTBSCertificate(), ((ExtensionsGenerator)object).generate());
                this.extGenerator.replaceExtension(Extension.deltaCertificateDescriptor, extension.isCritical(), aSN1Object);
            }
            catch (IOException iOException) {
                throw new IllegalStateException("unable to replace deltaCertificateDescriptor: " + iOException.getMessage());
            }
        }
        this.tbsGen.setSignature(null);
        this.tbsGen.setExtensions(this.extGenerator.generate());
        try {
            object = X509v3CertificateBuilder.generateSig(contentSigner2, this.tbsGen.generatePreTBSCertificate());
            this.extGenerator.addExtension(Extension.altSignatureValue, bl, new DERBitString((byte[])object));
            this.tbsGen.setSignature(algorithmIdentifier);
            this.tbsGen.setExtensions(this.extGenerator.generate());
            aSN1Object = this.tbsGen.generateTBSCertificate();
            byte[] byArray = X509v3CertificateBuilder.generateSig(contentSigner, aSN1Object);
            return new X509CertificateHolder(X509v3CertificateBuilder.generateStructure((TBSCertificate)aSN1Object, algorithmIdentifier, byArray));
        }
        catch (IOException iOException) {
            throw Exceptions.illegalArgumentException("cannot produce certificate signature", iOException);
        }
    }

    private static byte[] generateSig(ContentSigner contentSigner, ASN1Object aSN1Object) throws IOException {
        OutputStream outputStream = contentSigner.getOutputStream();
        aSN1Object.encodeTo(outputStream, "DER");
        outputStream.close();
        return contentSigner.getSignature();
    }

    private static Certificate generateStructure(TBSCertificate tBSCertificate, AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(tBSCertificate);
        aSN1EncodableVector.add(algorithmIdentifier);
        aSN1EncodableVector.add(new DERBitString(byArray));
        return Certificate.getInstance(new DERSequence(aSN1EncodableVector));
    }

    static DERBitString booleanToBitString(boolean[] blArray) {
        byte[] byArray = new byte[(blArray.length + 7) / 8];
        for (int i = 0; i != blArray.length; ++i) {
            int n = i >>> 3;
            byArray[n] = (byte)(byArray[n] | (blArray[i] ? (byte)(128 >> (i & 7)) : (byte)0));
        }
        return new DERBitString(byArray, 8 - blArray.length & 7);
    }
}

