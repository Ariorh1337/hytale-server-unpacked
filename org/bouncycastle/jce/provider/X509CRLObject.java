/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.ExtCRLException;
import org.bouncycastle.jce.provider.X509CRLEntryObject;
import org.bouncycastle.jce.provider.X509SignatureUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509CRLObject
extends X509CRL {
    private CertificateList c;
    private String sigAlgName;
    private byte[] sigAlgParams;
    private boolean isIndirect;
    private boolean isHashCodeSet = false;
    private int hashCodeValue;

    public static boolean isIndirectCRL(X509CRL x509CRL) throws CRLException {
        try {
            byte[] byArray = x509CRL.getExtensionValue(Extension.issuingDistributionPoint.getId());
            return byArray != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(byArray).getOctets()).isIndirectCRL();
        }
        catch (Exception exception) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", exception);
        }
    }

    public X509CRLObject(CertificateList certificateList) throws CRLException {
        this.c = certificateList;
        try {
            this.sigAlgName = X509SignatureUtil.getSignatureName(certificateList.getSignatureAlgorithm());
            this.sigAlgParams = (byte[])(certificateList.getSignatureAlgorithm().getParameters() != null ? certificateList.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER") : null);
            this.isIndirect = X509CRLObject.isIndirectCRL(this);
        }
        catch (Exception exception) {
            throw new CRLException("CRL contents invalid: " + exception);
        }
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Extensions extensions;
        if (this.getVersion() == 2 && (extensions = this.c.getExtensions()) != null) {
            Enumeration enumeration = extensions.oids();
            while (enumeration.hasMoreElements()) {
                Extension extension;
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                if (Extension.issuingDistributionPoint.equals(aSN1ObjectIdentifier) || Extension.deltaCRLIndicator.equals(aSN1ObjectIdentifier) || !(extension = extensions.getExtension(aSN1ObjectIdentifier)).isCritical()) continue;
                return true;
            }
        }
        return false;
    }

    private Set getExtensionOIDs(boolean bl) {
        Extensions extensions;
        if (this.getVersion() == 2 && (extensions = this.c.getExtensions()) != null) {
            HashSet<String> hashSet = new HashSet<String>();
            Enumeration enumeration = extensions.oids();
            while (enumeration.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                if (bl != extension.isCritical()) continue;
                hashSet.add(aSN1ObjectIdentifier.getId());
            }
            return hashSet;
        }
        return null;
    }

    public Set getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }

    public Set getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }

    @Override
    public byte[] getExtensionValue(String string) {
        return X509SignatureUtil.getExtensionValue(this.c.getExtensions(), string);
    }

    @Override
    public byte[] getEncoded() throws CRLException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new CRLException(iOException.toString());
        }
    }

    @Override
    public void verify(PublicKey publicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature;
        try {
            signature = Signature.getInstance(this.getSigAlgName(), "BC");
        }
        catch (Exception exception) {
            signature = Signature.getInstance(this.getSigAlgName());
        }
        this.doVerify(publicKey, signature);
    }

    @Override
    public void verify(PublicKey publicKey, String string) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature = string != null ? Signature.getInstance(this.getSigAlgName(), string) : Signature.getInstance(this.getSigAlgName());
        this.doVerify(publicKey, signature);
    }

    @Override
    public void verify(PublicKey publicKey, Provider provider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = provider != null ? Signature.getInstance(this.getSigAlgName(), provider) : Signature.getInstance(this.getSigAlgName());
        this.doVerify(publicKey, signature);
    }

    private void doVerify(PublicKey publicKey, Signature signature) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        signature.initVerify(publicKey);
        signature.update(this.getTBSCertList());
        if (!signature.verify(this.getSignature())) {
            throw new SignatureException("CRL does not verify with supplied public key.");
        }
    }

    @Override
    public int getVersion() {
        return this.c.getVersionNumber();
    }

    @Override
    public Principal getIssuerDN() {
        return new X509Principal(X500Name.getInstance(this.c.getIssuer().toASN1Primitive()));
    }

    @Override
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.c.getIssuer().getEncoded());
        }
        catch (IOException iOException) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override
    public Date getThisUpdate() {
        return this.c.getThisUpdate().getDate();
    }

    @Override
    public Date getNextUpdate() {
        Time time = this.c.getNextUpdate();
        return null == time ? null : time.getDate();
    }

    private Set loadCRLEntries() {
        HashSet<X509CRLEntryObject> hashSet = new HashSet<X509CRLEntryObject>();
        Enumeration enumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = null;
        while (enumeration.hasMoreElements()) {
            Extension extension;
            TBSCertList.CRLEntry cRLEntry = (TBSCertList.CRLEntry)enumeration.nextElement();
            X509CRLEntryObject x509CRLEntryObject = new X509CRLEntryObject(cRLEntry, this.isIndirect, x500Name);
            hashSet.add(x509CRLEntryObject);
            if (!this.isIndirect || !cRLEntry.hasExtensions() || (extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) == null) continue;
            x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
        }
        return hashSet;
    }

    @Override
    public X509CRLEntry getRevokedCertificate(BigInteger bigInteger) {
        Enumeration enumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = null;
        while (enumeration.hasMoreElements()) {
            Extension extension;
            TBSCertList.CRLEntry cRLEntry = (TBSCertList.CRLEntry)enumeration.nextElement();
            if (cRLEntry.getUserCertificate().hasValue(bigInteger)) {
                return new X509CRLEntryObject(cRLEntry, this.isIndirect, x500Name);
            }
            if (!this.isIndirect || !cRLEntry.hasExtensions() || (extension = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) == null) continue;
            x500Name = X500Name.getInstance(GeneralNames.getInstance(extension.getParsedValue()).getNames()[0].getName());
        }
        return null;
    }

    public Set getRevokedCertificates() {
        Set set = this.loadCRLEntries();
        if (!set.isEmpty()) {
            return Collections.unmodifiableSet(set);
        }
        return null;
    }

    @Override
    public byte[] getTBSCertList() throws CRLException {
        try {
            return this.c.getTBSCertList().getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new CRLException(iOException.toString());
        }
    }

    @Override
    public byte[] getSignature() {
        return this.c.getSignature().getOctets();
    }

    @Override
    public String getSigAlgName() {
        return this.sigAlgName;
    }

    @Override
    public String getSigAlgOID() {
        return this.c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override
    public byte[] getSigAlgParams() {
        return Arrays.clone(this.sigAlgParams);
    }

    @Override
    public String toString() {
        Object object;
        Object object2;
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        stringBuilder.append("              Version: ").append(this.getVersion()).append(string);
        stringBuilder.append("             IssuerDN: ").append(this.getIssuerDN()).append(string);
        stringBuilder.append("          This update: ").append(this.getThisUpdate()).append(string);
        stringBuilder.append("          Next update: ").append(this.getNextUpdate()).append(string);
        stringBuilder.append("  Signature Algorithm: ").append(this.getSigAlgName()).append(string);
        byte[] byArray = this.getSignature();
        stringBuilder.append("            Signature: ").append(new String(Hex.encode(byArray, 0, 20))).append(string);
        for (int i = 20; i < byArray.length; i += 20) {
            if (i < byArray.length - 20) {
                stringBuilder.append("                       ").append(new String(Hex.encode(byArray, i, 20))).append(string);
                continue;
            }
            stringBuilder.append("                       ").append(new String(Hex.encode(byArray, i, byArray.length - i))).append(string);
        }
        Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
            object2 = extensions.oids();
            if (object2.hasMoreElements()) {
                stringBuilder.append("           Extensions: ").append(string);
            }
            while (object2.hasMoreElements()) {
                object = (ASN1ObjectIdentifier)object2.nextElement();
                Extension extension = extensions.getExtension((ASN1ObjectIdentifier)object);
                if (extension.getExtnValue() != null) {
                    byte[] byArray2 = extension.getExtnValue().getOctets();
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(byArray2);
                    stringBuilder.append("                       critical(").append(extension.isCritical()).append(") ");
                    try {
                        if (((ASN1Primitive)object).equals(Extension.cRLNumber)) {
                            stringBuilder.append(new CRLNumber(ASN1Integer.getInstance(aSN1InputStream.readObject()).getPositiveValue())).append(string);
                            continue;
                        }
                        if (((ASN1Primitive)object).equals(Extension.deltaCRLIndicator)) {
                            stringBuilder.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(aSN1InputStream.readObject()).getPositiveValue())).append(string);
                            continue;
                        }
                        if (((ASN1Primitive)object).equals(Extension.issuingDistributionPoint)) {
                            stringBuilder.append(IssuingDistributionPoint.getInstance(aSN1InputStream.readObject())).append(string);
                            continue;
                        }
                        if (((ASN1Primitive)object).equals(Extension.cRLDistributionPoints)) {
                            stringBuilder.append(CRLDistPoint.getInstance(aSN1InputStream.readObject())).append(string);
                            continue;
                        }
                        if (((ASN1Primitive)object).equals(Extension.freshestCRL)) {
                            stringBuilder.append(CRLDistPoint.getInstance(aSN1InputStream.readObject())).append(string);
                            continue;
                        }
                        stringBuilder.append(((ASN1ObjectIdentifier)object).getId());
                        stringBuilder.append(" value = ").append(ASN1Dump.dumpAsString(aSN1InputStream.readObject())).append(string);
                    }
                    catch (Exception exception) {
                        stringBuilder.append(((ASN1ObjectIdentifier)object).getId());
                        stringBuilder.append(" value = ").append("*****").append(string);
                    }
                    continue;
                }
                stringBuilder.append(string);
            }
        }
        if ((object2 = this.getRevokedCertificates()) != null) {
            object = object2.iterator();
            while (object.hasNext()) {
                stringBuilder.append(object.next());
                stringBuilder.append(string);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean isRevoked(java.security.cert.Certificate certificate) {
        if (!certificate.getType().equals("X.509")) {
            throw new RuntimeException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration enumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = this.c.getIssuer();
        if (enumeration != null) {
            BigInteger bigInteger = ((X509Certificate)certificate).getSerialNumber();
            while (enumeration.hasMoreElements()) {
                ASN1Object aSN1Object;
                TBSCertList.CRLEntry cRLEntry = TBSCertList.CRLEntry.getInstance(enumeration.nextElement());
                if (this.isIndirect && cRLEntry.hasExtensions() && (aSN1Object = cRLEntry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                    x500Name = X500Name.getInstance(GeneralNames.getInstance(aSN1Object.getParsedValue()).getNames()[0].getName());
                }
                if (!cRLEntry.getUserCertificate().hasValue(bigInteger)) continue;
                if (certificate instanceof X509Certificate) {
                    aSN1Object = X500Name.getInstance(((X509Certificate)certificate).getIssuerX500Principal().getEncoded());
                } else {
                    try {
                        aSN1Object = Certificate.getInstance(certificate.getEncoded()).getIssuer();
                    }
                    catch (CertificateEncodingException certificateEncodingException) {
                        throw new RuntimeException("Cannot process certificate");
                    }
                }
                return x500Name.equals(aSN1Object);
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof X509CRL)) {
            return false;
        }
        if (object instanceof X509CRLObject) {
            boolean bl;
            X509CRLObject x509CRLObject = (X509CRLObject)object;
            if (this.isHashCodeSet && (bl = x509CRLObject.isHashCodeSet) && x509CRLObject.hashCodeValue != this.hashCodeValue) {
                return false;
            }
            return this.c.equals(x509CRLObject.c);
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.isHashCodeSet = true;
            this.hashCodeValue = super.hashCode();
        }
        return this.hashCodeValue;
    }
}

