/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
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
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLEntryObject;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

abstract class X509CRLImpl
extends X509CRL {
    protected JcaJceHelper bcHelper;
    protected CertificateList c;
    protected String sigAlgName;
    protected byte[] sigAlgParams;
    protected boolean isIndirect;

    X509CRLImpl(JcaJceHelper jcaJceHelper, CertificateList certificateList, String string, byte[] byArray, boolean bl) {
        this.bcHelper = jcaJceHelper;
        this.c = certificateList;
        this.sigAlgName = string;
        this.sigAlgParams = byArray;
        this.isIndirect = bl;
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
    public void verify(PublicKey publicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.doVerify(publicKey, new SignatureCreator(){

            @Override
            public Signature createSignature(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
                try {
                    return X509CRLImpl.this.bcHelper.createSignature(string);
                }
                catch (Exception exception) {
                    return Signature.getInstance(string);
                }
            }
        });
    }

    @Override
    public void verify(PublicKey publicKey, final String string) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.doVerify(publicKey, new SignatureCreator(){
            final /* synthetic */ X509CRLImpl this$0;
            {
                this.this$0 = x509CRLImpl;
            }

            @Override
            public Signature createSignature(String string2) throws NoSuchAlgorithmException, NoSuchProviderException {
                if (string != null) {
                    return Signature.getInstance(string2, string);
                }
                return Signature.getInstance(string2);
            }
        });
    }

    @Override
    public void verify(PublicKey publicKey, final Provider provider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            this.doVerify(publicKey, new SignatureCreator(){
                final /* synthetic */ X509CRLImpl this$0;
                {
                    this.this$0 = x509CRLImpl;
                }

                @Override
                public Signature createSignature(String string) throws NoSuchAlgorithmException, NoSuchProviderException {
                    if (provider != null) {
                        return Signature.getInstance(this.this$0.getSigAlgName(), provider);
                    }
                    return Signature.getInstance(this.this$0.getSigAlgName());
                }
            });
        }
        catch (NoSuchProviderException noSuchProviderException) {
            throw new NoSuchAlgorithmException("provider issue: " + noSuchProviderException.getMessage());
        }
    }

    private void doVerify(PublicKey publicKey, SignatureCreator signatureCreator) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        if (publicKey instanceof CompositePublicKey && X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            List<PublicKey> list = ((CompositePublicKey)publicKey).getPublicKeys();
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
            boolean bl = false;
            for (int i = 0; i != list.size(); ++i) {
                if (list.get(i) == null) continue;
                AlgorithmIdentifier algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i));
                String string = X509SignatureUtil.getSignatureName(algorithmIdentifier);
                Signature signature = signatureCreator.createSignature(string);
                SignatureException signatureException = null;
                try {
                    this.checkSignature(list.get(i), signature, algorithmIdentifier.getParameters(), ASN1BitString.getInstance(aSN1Sequence2.getObjectAt(i)).getOctets());
                    bl = true;
                }
                catch (SignatureException signatureException2) {
                    signatureException = signatureException2;
                }
                if (signatureException == null) continue;
                throw signatureException;
            }
            if (!bl) {
                throw new InvalidKeyException("no matching key found");
            }
        } else if (X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence aSN1Sequence3 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
            boolean bl = false;
            for (int i = 0; i != aSN1Sequence3.size(); ++i) {
                AlgorithmIdentifier algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(i));
                String string = X509SignatureUtil.getSignatureName(algorithmIdentifier);
                SignatureException signatureException = null;
                try {
                    Signature signature = signatureCreator.createSignature(string);
                    this.checkSignature(publicKey, signature, algorithmIdentifier.getParameters(), ASN1BitString.getInstance(aSN1Sequence3.getObjectAt(i)).getOctets());
                    bl = true;
                }
                catch (InvalidKeyException invalidKeyException) {
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                }
                catch (SignatureException signatureException3) {
                    signatureException = signatureException3;
                }
                if (signatureException == null) continue;
                throw signatureException;
            }
            if (!bl) {
                throw new InvalidKeyException("no matching key found");
            }
        } else {
            Signature signature = signatureCreator.createSignature(this.getSigAlgName());
            if (this.sigAlgParams == null) {
                this.checkSignature(publicKey, signature, null, this.getSignature());
            } else {
                try {
                    this.checkSignature(publicKey, signature, ASN1Primitive.fromByteArray(this.sigAlgParams), this.getSignature());
                }
                catch (IOException iOException) {
                    throw new SignatureException("cannot decode signature parameters: " + iOException.getMessage());
                }
            }
        }
    }

    private void checkSignature(PublicKey publicKey, Signature signature, ASN1Encodable aSN1Encodable, byte[] byArray) throws CRLException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        if (!X509SignatureUtil.areEquivalentAlgorithms(this.c.getSignatureAlgorithm(), this.c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TbsCertList.");
        }
        X509SignatureUtil.setSignatureParameters(signature, aSN1Encodable);
        signature.initVerify(publicKey);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(OutputStreamFactory.createStream(signature), 512);
            this.c.getTBSCertList().encodeTo(bufferedOutputStream, "DER");
            ((OutputStream)bufferedOutputStream).close();
        }
        catch (IOException iOException) {
            throw new CRLException(iOException.toString());
        }
        if (!signature.verify(byArray)) {
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
        X509SignatureUtil.prettyPrintSignature(this.getSignature(), stringBuilder, string);
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
                    byte[] byArray = extension.getExtnValue().getOctets();
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(byArray);
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
            throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration enumeration = this.c.getRevokedCertificateEnumeration();
        X500Name x500Name = this.c.getIssuer();
        if (enumeration.hasMoreElements()) {
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
                        throw new IllegalArgumentException("Cannot process certificate: " + certificateEncodingException.getMessage());
                    }
                }
                return x500Name.equals(aSN1Object);
            }
        }
        return false;
    }

    static byte[] getExtensionOctets(CertificateList certificateList, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        ASN1OctetString aSN1OctetString = Extensions.getExtensionValue(certificateList.getExtensions(), aSN1ObjectIdentifier);
        return aSN1OctetString == null ? null : aSN1OctetString.getOctets();
    }
}

