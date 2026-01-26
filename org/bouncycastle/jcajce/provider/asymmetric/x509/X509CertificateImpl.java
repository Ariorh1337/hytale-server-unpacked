/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.NetscapeCertType;
import org.bouncycastle.internal.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.internal.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

abstract class X509CertificateImpl
extends X509Certificate
implements BCX509Certificate {
    protected JcaJceHelper bcHelper;
    protected Certificate c;
    protected BasicConstraints basicConstraints;
    protected boolean[] keyUsage;
    protected String sigAlgName;
    protected byte[] sigAlgParams;

    X509CertificateImpl(JcaJceHelper jcaJceHelper, Certificate certificate, BasicConstraints basicConstraints, boolean[] blArray, String string, byte[] byArray) {
        this.bcHelper = jcaJceHelper;
        this.c = certificate;
        this.basicConstraints = basicConstraints;
        this.keyUsage = blArray;
        this.sigAlgName = string;
        this.sigAlgParams = byArray;
    }

    @Override
    public X500Name getIssuerX500Name() {
        return this.c.getIssuer();
    }

    @Override
    public TBSCertificate getTBSCertificateNative() {
        return this.c.getTBSCertificate();
    }

    @Override
    public X500Name getSubjectX500Name() {
        return this.c.getSubject();
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.checkValidity(new Date());
    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.getTime() > this.getNotAfter().getTime()) {
            throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
        }
        if (date.getTime() < this.getNotBefore().getTime()) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
        }
    }

    @Override
    public int getVersion() {
        return this.c.getVersionNumber();
    }

    @Override
    public BigInteger getSerialNumber() {
        return this.c.getSerialNumber().getValue();
    }

    @Override
    public Principal getIssuerDN() {
        return new X509Principal(this.c.getIssuer());
    }

    @Override
    public X500Principal getIssuerX500Principal() {
        try {
            byte[] byArray = this.c.getIssuer().getEncoded("DER");
            return new X500Principal(byArray);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override
    public Principal getSubjectDN() {
        return new X509Principal(this.c.getSubject());
    }

    @Override
    public X500Principal getSubjectX500Principal() {
        try {
            byte[] byArray = this.c.getSubject().getEncoded("DER");
            return new X500Principal(byArray);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("can't encode subject DN");
        }
    }

    @Override
    public Date getNotBefore() {
        return this.c.getStartDate().getDate();
    }

    @Override
    public Date getNotAfter() {
        return this.c.getEndDate().getDate();
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        try {
            return this.c.getTBSCertificate().getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new CertificateEncodingException(iOException.toString());
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
    public boolean[] getIssuerUniqueID() {
        ASN1BitString aSN1BitString = this.c.getTBSCertificate().getIssuerUniqueId();
        if (aSN1BitString != null) {
            byte[] byArray = aSN1BitString.getBytes();
            boolean[] blArray = new boolean[byArray.length * 8 - aSN1BitString.getPadBits()];
            for (int i = 0; i != blArray.length; ++i) {
                blArray[i] = (byArray[i / 8] & 128 >>> i % 8) != 0;
            }
            return blArray;
        }
        return null;
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        ASN1BitString aSN1BitString = this.c.getTBSCertificate().getSubjectUniqueId();
        if (aSN1BitString != null) {
            byte[] byArray = aSN1BitString.getBytes();
            boolean[] blArray = new boolean[byArray.length * 8 - aSN1BitString.getPadBits()];
            for (int i = 0; i != blArray.length; ++i) {
                blArray[i] = (byArray[i / 8] & 128 >>> i % 8) != 0;
            }
            return blArray;
        }
        return null;
    }

    @Override
    public boolean[] getKeyUsage() {
        return Arrays.clone(this.keyUsage);
    }

    public List getExtendedKeyUsage() throws CertificateParsingException {
        byte[] byArray = X509CertificateImpl.getExtensionOctets(this.c, Extension.extendedKeyUsage);
        if (null == byArray) {
            return null;
        }
        try {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(byArray);
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i != aSN1Sequence.size(); ++i) {
                arrayList.add(((ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(i)).getId());
            }
            return Collections.unmodifiableList(arrayList);
        }
        catch (Exception exception) {
            throw new CertificateParsingException("error processing extended key usage extension");
        }
    }

    @Override
    public int getBasicConstraints() {
        if (this.basicConstraints == null || !this.basicConstraints.isCA()) {
            return -1;
        }
        ASN1Integer aSN1Integer = this.basicConstraints.getPathLenConstraintInteger();
        if (aSN1Integer == null) {
            return Integer.MAX_VALUE;
        }
        return aSN1Integer.intPositiveValueExact();
    }

    public Collection getSubjectAlternativeNames() throws CertificateParsingException {
        return X509CertificateImpl.getAlternativeNames(this.c, Extension.subjectAlternativeName);
    }

    public Collection getIssuerAlternativeNames() throws CertificateParsingException {
        return X509CertificateImpl.getAlternativeNames(this.c, Extension.issuerAlternativeName);
    }

    public Set getCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            HashSet<String> hashSet = new HashSet<String>();
            Extensions extensions = this.c.getExtensions();
            if (extensions != null) {
                Enumeration enumeration = extensions.oids();
                while (enumeration.hasMoreElements()) {
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                    Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                    if (!extension.isCritical()) continue;
                    hashSet.add(aSN1ObjectIdentifier.getId());
                }
                return hashSet;
            }
        }
        return null;
    }

    @Override
    public byte[] getExtensionValue(String string) {
        return X509SignatureUtil.getExtensionValue(this.c.getExtensions(), string);
    }

    public Set getNonCriticalExtensionOIDs() {
        if (this.getVersion() == 3) {
            HashSet<String> hashSet = new HashSet<String>();
            Extensions extensions = this.c.getExtensions();
            if (extensions != null) {
                Enumeration enumeration = extensions.oids();
                while (enumeration.hasMoreElements()) {
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                    Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                    if (extension.isCritical()) continue;
                    hashSet.add(aSN1ObjectIdentifier.getId());
                }
                return hashSet;
            }
        }
        return null;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Extensions extensions;
        if (this.getVersion() == 3 && (extensions = this.c.getExtensions()) != null) {
            Enumeration enumeration = extensions.oids();
            while (enumeration.hasMoreElements()) {
                Extension extension;
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                if (Extension.keyUsage.equals(aSN1ObjectIdentifier) || Extension.certificatePolicies.equals(aSN1ObjectIdentifier) || Extension.policyMappings.equals(aSN1ObjectIdentifier) || Extension.inhibitAnyPolicy.equals(aSN1ObjectIdentifier) || Extension.cRLDistributionPoints.equals(aSN1ObjectIdentifier) || Extension.issuingDistributionPoint.equals(aSN1ObjectIdentifier) || Extension.deltaCRLIndicator.equals(aSN1ObjectIdentifier) || Extension.policyConstraints.equals(aSN1ObjectIdentifier) || Extension.basicConstraints.equals(aSN1ObjectIdentifier) || Extension.subjectAlternativeName.equals(aSN1ObjectIdentifier) || Extension.nameConstraints.equals(aSN1ObjectIdentifier) || !(extension = extensions.getExtension(aSN1ObjectIdentifier)).isCritical()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public PublicKey getPublicKey() {
        try {
            return BouncyCastleProvider.getPublicKey(this.c.getSubjectPublicKeyInfo());
        }
        catch (IOException iOException) {
            throw Exceptions.illegalStateException("failed to recover public key: " + iOException.getMessage(), iOException);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        stringBuilder.append("  [0]         Version: ").append(this.getVersion()).append(string);
        stringBuilder.append("         SerialNumber: ").append(this.getSerialNumber()).append(string);
        stringBuilder.append("             IssuerDN: ").append(this.getIssuerDN()).append(string);
        stringBuilder.append("           Start Date: ").append(this.getNotBefore()).append(string);
        stringBuilder.append("           Final Date: ").append(this.getNotAfter()).append(string);
        stringBuilder.append("            SubjectDN: ").append(this.getSubjectDN()).append(string);
        stringBuilder.append("           Public Key: ").append(this.getPublicKey()).append(string);
        stringBuilder.append("  Signature Algorithm: ").append(this.getSigAlgName()).append(string);
        X509SignatureUtil.prettyPrintSignature(this.getSignature(), stringBuilder, string);
        Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
            Enumeration enumeration = extensions.oids();
            if (enumeration.hasMoreElements()) {
                stringBuilder.append("       Extensions: \n");
            }
            while (enumeration.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    byte[] byArray = extension.getExtnValue().getOctets();
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(byArray);
                    stringBuilder.append("                       critical(").append(extension.isCritical()).append(") ");
                    try {
                        if (aSN1ObjectIdentifier.equals(Extension.basicConstraints)) {
                            stringBuilder.append(BasicConstraints.getInstance(aSN1InputStream.readObject())).append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(Extension.keyUsage)) {
                            stringBuilder.append(KeyUsage.getInstance(aSN1InputStream.readObject())).append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.netscapeCertType)) {
                            stringBuilder.append(new NetscapeCertType(ASN1BitString.getInstance(aSN1InputStream.readObject()))).append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                            stringBuilder.append(new NetscapeRevocationURL(ASN1IA5String.getInstance(aSN1InputStream.readObject()))).append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                            stringBuilder.append(new VerisignCzagExtension(ASN1IA5String.getInstance(aSN1InputStream.readObject()))).append(string);
                            continue;
                        }
                        stringBuilder.append(aSN1ObjectIdentifier.getId());
                        stringBuilder.append(" value = ").append(ASN1Dump.dumpAsString(aSN1InputStream.readObject())).append(string);
                    }
                    catch (Exception exception) {
                        stringBuilder.append(aSN1ObjectIdentifier.getId());
                        stringBuilder.append(" value = ").append("*****").append(string);
                    }
                    continue;
                }
                stringBuilder.append(string);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public final void verify(PublicKey publicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.doVerify(publicKey, new SignatureCreator(){

            @Override
            public Signature createSignature(String string) throws NoSuchAlgorithmException {
                try {
                    return X509CertificateImpl.this.bcHelper.createSignature(string);
                }
                catch (Exception exception) {
                    return Signature.getInstance(string);
                }
            }
        });
    }

    @Override
    public final void verify(PublicKey publicKey, final String string) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.doVerify(publicKey, new SignatureCreator(){
            final /* synthetic */ X509CertificateImpl this$0;
            {
                this.this$0 = x509CertificateImpl;
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
    public final void verify(PublicKey publicKey, final Provider provider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            this.doVerify(publicKey, new SignatureCreator(){
                final /* synthetic */ X509CertificateImpl this$0;
                {
                    this.this$0 = x509CertificateImpl;
                }

                @Override
                public Signature createSignature(String string) throws NoSuchAlgorithmException {
                    if (provider != null) {
                        return Signature.getInstance(string, provider);
                    }
                    return Signature.getInstance(string);
                }
            });
        }
        catch (NoSuchProviderException noSuchProviderException) {
            throw new NoSuchAlgorithmException("provider issue: " + noSuchProviderException.getMessage());
        }
    }

    private void doVerify(PublicKey publicKey, SignatureCreator signatureCreator) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
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
            if (publicKey instanceof CompositePublicKey && MiscObjectIdentifiers.id_composite_key.equals(((CompositePublicKey)publicKey).getAlgorithmIdentifier().getAlgorithm())) {
                List<PublicKey> list = ((CompositePublicKey)publicKey).getPublicKeys();
                for (int i = 0; i != list.size(); ++i) {
                    try {
                        this.checkSignature(list.get(i), signature, this.c.getSignatureAlgorithm().getParameters(), this.getSignature());
                        return;
                    }
                    catch (InvalidKeyException invalidKeyException) {
                        continue;
                    }
                }
                throw new InvalidKeyException("no matching signature found");
            }
            this.checkSignature(publicKey, signature, this.c.getSignatureAlgorithm().getParameters(), this.getSignature());
        }
    }

    private void checkSignature(PublicKey publicKey, Signature signature, ASN1Encodable aSN1Encodable, byte[] byArray) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        if (!X509SignatureUtil.areEquivalentAlgorithms(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature())) {
            throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
        }
        X509SignatureUtil.setSignatureParameters(signature, aSN1Encodable);
        signature.initVerify(publicKey);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(OutputStreamFactory.createStream(signature), 512);
            this.c.getTBSCertificate().encodeTo(bufferedOutputStream, "DER");
            ((OutputStream)bufferedOutputStream).close();
        }
        catch (IOException iOException) {
            throw new CertificateEncodingException(iOException.toString());
        }
        if (!signature.verify(byArray)) {
            throw new SignatureException("certificate does not verify with supplied key");
        }
    }

    private static Collection getAlternativeNames(Certificate certificate, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CertificateParsingException {
        byte[] byArray = X509CertificateImpl.getExtensionOctets(certificate, aSN1ObjectIdentifier);
        if (byArray == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            Enumeration enumeration = ASN1Sequence.getInstance(byArray).getObjects();
            block11: while (enumeration.hasMoreElements()) {
                GeneralName generalName = GeneralName.getInstance(enumeration.nextElement());
                ArrayList<Object> arrayList2 = new ArrayList<Object>();
                arrayList2.add(Integers.valueOf(generalName.getTagNo()));
                switch (generalName.getTagNo()) {
                    case 0: 
                    case 3: 
                    case 5: {
                        arrayList2.add(generalName.getEncoded());
                        break;
                    }
                    case 4: {
                        arrayList2.add(X500Name.getInstance(RFC4519Style.INSTANCE, generalName.getName()).toString());
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 6: {
                        arrayList2.add(((ASN1String)((Object)generalName.getName())).getString());
                        break;
                    }
                    case 8: {
                        arrayList2.add(ASN1ObjectIdentifier.getInstance(generalName.getName()).getId());
                        break;
                    }
                    case 7: {
                        String string;
                        byte[] byArray2 = DEROctetString.getInstance(generalName.getName()).getOctets();
                        try {
                            string = InetAddress.getByAddress(byArray2).getHostAddress();
                        }
                        catch (UnknownHostException unknownHostException) {
                            continue block11;
                        }
                        arrayList2.add(string);
                        break;
                    }
                    default: {
                        throw new IOException("Bad tag number: " + generalName.getTagNo());
                    }
                }
                arrayList.add(Collections.unmodifiableList(arrayList2));
            }
            if (arrayList.size() == 0) {
                return null;
            }
            return Collections.unmodifiableCollection(arrayList);
        }
        catch (Exception exception) {
            throw new CertificateParsingException(exception.getMessage());
        }
    }

    static byte[] getExtensionOctets(Certificate certificate, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        ASN1OctetString aSN1OctetString = Extensions.getExtensionValue(certificate.getExtensions(), aSN1ObjectIdentifier);
        return aSN1OctetString == null ? null : aSN1OctetString.getOctets();
    }
}

