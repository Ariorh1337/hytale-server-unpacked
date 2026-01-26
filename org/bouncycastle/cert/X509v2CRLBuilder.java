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
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V2TBSCertListGenerator;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.CertUtils;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

public class X509v2CRLBuilder {
    private V2TBSCertListGenerator tbsGen = new V2TBSCertListGenerator();
    private ExtensionsGenerator extGenerator;

    public X509v2CRLBuilder(X500Name x500Name, Date date) {
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(x500Name);
        this.tbsGen.setThisUpdate(new Time(date));
    }

    public X509v2CRLBuilder(X500Name x500Name, Date date, Locale locale) {
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(x500Name);
        this.tbsGen.setThisUpdate(new Time(date, locale));
    }

    public X509v2CRLBuilder(X500Name x500Name, Time time) {
        this.extGenerator = new ExtensionsGenerator();
        this.tbsGen.setIssuer(x500Name);
        this.tbsGen.setThisUpdate(time);
    }

    public X509v2CRLBuilder(X509CRLHolder x509CRLHolder) {
        this.tbsGen.setIssuer(x509CRLHolder.getIssuer());
        this.tbsGen.setThisUpdate(new Time(x509CRLHolder.getThisUpdate()));
        Date date = x509CRLHolder.getNextUpdate();
        if (date != null) {
            this.tbsGen.setNextUpdate(new Time(date));
        }
        this.addCRL(x509CRLHolder);
        this.extGenerator = new ExtensionsGenerator();
        Extensions extensions = x509CRLHolder.getExtensions();
        if (extensions != null) {
            Enumeration enumeration = extensions.oids();
            while (enumeration.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                if (Extension.altSignatureAlgorithm.equals(aSN1ObjectIdentifier) || Extension.altSignatureValue.equals(aSN1ObjectIdentifier)) continue;
                this.extGenerator.addExtension(extensions.getExtension(aSN1ObjectIdentifier));
            }
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

    public X509v2CRLBuilder setThisUpdate(Date date) {
        return this.setThisUpdate(new Time(date));
    }

    public X509v2CRLBuilder setThisUpdate(Date date, Locale locale) {
        return this.setThisUpdate(new Time(date, locale));
    }

    public X509v2CRLBuilder setThisUpdate(Time time) {
        this.tbsGen.setThisUpdate(time);
        return this;
    }

    public X509v2CRLBuilder setNextUpdate(Date date) {
        return this.setNextUpdate(new Time(date));
    }

    public X509v2CRLBuilder setNextUpdate(Date date, Locale locale) {
        return this.setNextUpdate(new Time(date, locale));
    }

    public X509v2CRLBuilder setNextUpdate(Time time) {
        this.tbsGen.setNextUpdate(time);
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, int n) {
        this.tbsGen.addCRLEntry(new ASN1Integer(bigInteger), new Time(date), n);
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, int n, Date date2) {
        this.tbsGen.addCRLEntry(new ASN1Integer(bigInteger), new Time(date), n, new ASN1GeneralizedTime(date2));
        return this;
    }

    public X509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, Extensions extensions) {
        this.tbsGen.addCRLEntry(new ASN1Integer(bigInteger), new Time(date), extensions);
        return this;
    }

    public X509v2CRLBuilder addCRL(X509CRLHolder x509CRLHolder) {
        TBSCertList tBSCertList = x509CRLHolder.toASN1Structure().getTBSCertList();
        if (tBSCertList != null) {
            Enumeration enumeration = tBSCertList.getRevokedCertificateEnumeration();
            while (enumeration.hasMoreElements()) {
                this.tbsGen.addCRLEntry(ASN1Sequence.getInstance(((ASN1Encodable)enumeration.nextElement()).toASN1Primitive()));
            }
        }
        return this;
    }

    public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, aSN1ObjectIdentifier, bl, aSN1Encodable);
        return this;
    }

    public X509v2CRLBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) throws CertIOException {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, byArray);
        return this;
    }

    public X509v2CRLBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v2CRLBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, bl, (ASN1OctetString)new DEROctetString(aSN1Encodable)));
        }
        catch (IOException iOException) {
            throw new CertIOException("cannot encode extension: " + iOException.getMessage(), iOException);
        }
        return this;
    }

    public X509v2CRLBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v2CRLBuilder replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(aSN1ObjectIdentifier, bl, byArray));
        return this;
    }

    public X509v2CRLBuilder removeExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, aSN1ObjectIdentifier);
        return this;
    }

    public X509CRLHolder build(ContentSigner contentSigner) {
        this.tbsGen.setSignature(contentSigner.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return X509v2CRLBuilder.generateFullCRL(contentSigner, this.tbsGen.generateTBSCertList());
    }

    public X509CRLHolder build(ContentSigner contentSigner, boolean bl, ContentSigner contentSigner2) {
        this.tbsGen.setSignature(null);
        try {
            this.extGenerator.addExtension(Extension.altSignatureAlgorithm, bl, contentSigner2.getAlgorithmIdentifier());
        }
        catch (IOException iOException) {
            throw Exceptions.illegalStateException("cannot add altSignatureAlgorithm extension", iOException);
        }
        this.tbsGen.setExtensions(this.extGenerator.generate());
        try {
            this.extGenerator.addExtension(Extension.altSignatureValue, bl, new DERBitString(X509v2CRLBuilder.generateSig(contentSigner2, this.tbsGen.generatePreTBSCertList())));
            this.tbsGen.setSignature(contentSigner.getAlgorithmIdentifier());
            this.tbsGen.setExtensions(this.extGenerator.generate());
            TBSCertList tBSCertList = this.tbsGen.generateTBSCertList();
            return new X509CRLHolder(X509v2CRLBuilder.generateCRLStructure(tBSCertList, contentSigner.getAlgorithmIdentifier(), X509v2CRLBuilder.generateSig(contentSigner, tBSCertList)));
        }
        catch (IOException iOException) {
            throw Exceptions.illegalArgumentException("cannot produce certificate signature", iOException);
        }
    }

    private static X509CRLHolder generateFullCRL(ContentSigner contentSigner, TBSCertList tBSCertList) {
        try {
            return new X509CRLHolder(X509v2CRLBuilder.generateCRLStructure(tBSCertList, contentSigner.getAlgorithmIdentifier(), X509v2CRLBuilder.generateSig(contentSigner, tBSCertList)));
        }
        catch (IOException iOException) {
            throw Exceptions.illegalStateException("cannot produce certificate signature", iOException);
        }
    }

    private static CertificateList generateCRLStructure(TBSCertList tBSCertList, AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(tBSCertList);
        aSN1EncodableVector.add(algorithmIdentifier);
        aSN1EncodableVector.add(new DERBitString(byArray));
        return CertificateList.getInstance(new DERSequence(aSN1EncodableVector));
    }

    private static byte[] generateSig(ContentSigner contentSigner, ASN1Object aSN1Object) throws IOException {
        OutputStream outputStream = contentSigner.getOutputStream();
        aSN1Object.encodeTo(outputStream, "DER");
        outputStream.close();
        return contentSigner.getSignature();
    }
}

