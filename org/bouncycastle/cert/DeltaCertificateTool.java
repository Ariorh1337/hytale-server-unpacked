/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Validity;
import org.bouncycastle.cert.X509CertificateHolder;

public class DeltaCertificateTool {
    public static Extension makeDeltaCertificateExtension(boolean bl, Certificate certificate) throws IOException {
        DeltaCertificateDescriptor deltaCertificateDescriptor = new DeltaCertificateDescriptor(certificate.getSerialNumber(), certificate.getSignatureAlgorithm(), certificate.getIssuer(), certificate.getValidity(), certificate.getSubject(), certificate.getSubjectPublicKeyInfo(), certificate.getExtensions(), certificate.getSignature());
        DEROctetString dEROctetString = new DEROctetString(deltaCertificateDescriptor.getEncoded("DER"));
        return new Extension(Extension.deltaCertificateDescriptor, bl, (ASN1OctetString)dEROctetString);
    }

    public static Extension makeDeltaCertificateExtension(boolean bl, X509CertificateHolder x509CertificateHolder) throws IOException {
        return DeltaCertificateTool.makeDeltaCertificateExtension(bl, x509CertificateHolder.toASN1Structure());
    }

    public static Certificate extractDeltaCertificate(TBSCertificate tBSCertificate) {
        X500Name x500Name;
        Validity validity;
        X500Name x500Name2;
        Extensions extensions = tBSCertificate.getExtensions();
        Extension extension = extensions.getExtension(Extension.deltaCertificateDescriptor);
        if (extension == null) {
            throw new IllegalStateException("no deltaCertificateDescriptor present");
        }
        DeltaCertificateDescriptor deltaCertificateDescriptor = DeltaCertificateDescriptor.getInstance(extension.getParsedValue());
        ASN1Integer aSN1Integer = tBSCertificate.getVersion();
        ASN1Integer aSN1Integer2 = deltaCertificateDescriptor.getSerialNumber();
        AlgorithmIdentifier algorithmIdentifier = deltaCertificateDescriptor.getSignature();
        if (algorithmIdentifier == null) {
            algorithmIdentifier = tBSCertificate.getSignature();
        }
        if ((x500Name2 = deltaCertificateDescriptor.getIssuer()) == null) {
            x500Name2 = tBSCertificate.getIssuer();
        }
        if ((validity = deltaCertificateDescriptor.getValidityObject()) == null) {
            validity = tBSCertificate.getValidity();
        }
        if ((x500Name = deltaCertificateDescriptor.getSubject()) == null) {
            x500Name = tBSCertificate.getSubject();
        }
        SubjectPublicKeyInfo subjectPublicKeyInfo = deltaCertificateDescriptor.getSubjectPublicKeyInfo();
        Extensions extensions2 = DeltaCertificateTool.extractDeltaExtensions(deltaCertificateDescriptor.getExtensions(), extensions);
        TBSCertificate tBSCertificate2 = new TBSCertificate(aSN1Integer, aSN1Integer2, algorithmIdentifier, x500Name2, validity, x500Name, subjectPublicKeyInfo, null, null, extensions2);
        return new Certificate(tBSCertificate2, algorithmIdentifier, deltaCertificateDescriptor.getSignatureValue());
    }

    public static X509CertificateHolder extractDeltaCertificate(X509CertificateHolder x509CertificateHolder) {
        return new X509CertificateHolder(DeltaCertificateTool.extractDeltaCertificate(x509CertificateHolder.getTBSCertificate()));
    }

    public static DeltaCertificateDescriptor trimDeltaCertificateDescriptor(DeltaCertificateDescriptor deltaCertificateDescriptor, TBSCertificate tBSCertificate, Extensions extensions) {
        return deltaCertificateDescriptor.trimTo(tBSCertificate, extensions);
    }

    private static Extensions extractDeltaExtensions(Extensions extensions, Extensions extensions2) {
        Object object;
        ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
        Enumeration enumeration = extensions2.oids();
        while (enumeration.hasMoreElements()) {
            object = (ASN1ObjectIdentifier)enumeration.nextElement();
            if (Extension.deltaCertificateDescriptor.equals((ASN1Primitive)object)) continue;
            extensionsGenerator.addExtension(extensions2.getExtension((ASN1ObjectIdentifier)object));
        }
        if (extensions != null) {
            object = extensions.oids();
            while (object.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)object.nextElement();
                extensionsGenerator.replaceExtension(extensions.getExtension(aSN1ObjectIdentifier));
            }
        }
        return extensionsGenerator.isEmpty() ? null : extensionsGenerator.generate();
    }
}

