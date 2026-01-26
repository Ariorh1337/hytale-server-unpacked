/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509SignatureUtil;
import org.bouncycastle.util.Strings;

class X509CRLEntryObject
extends X509CRLEntry {
    private TBSCertList.CRLEntry c;
    private X500Name certificateIssuer;
    private volatile boolean hashValueSet;
    private volatile int hashValue;

    protected X509CRLEntryObject(TBSCertList.CRLEntry cRLEntry) {
        this.c = cRLEntry;
        this.certificateIssuer = null;
    }

    protected X509CRLEntryObject(TBSCertList.CRLEntry cRLEntry, boolean bl, X500Name x500Name) {
        this.c = cRLEntry;
        this.certificateIssuer = this.loadCertificateIssuer(bl, x500Name);
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Extensions extensions = this.c.getExtensions();
        return extensions != null && extensions.hasAnyCriticalExtensions();
    }

    private X500Name loadCertificateIssuer(boolean bl, X500Name x500Name) {
        if (!bl) {
            return null;
        }
        ASN1OctetString aSN1OctetString = Extensions.getExtensionValue(this.c.getExtensions(), Extension.certificateIssuer);
        if (aSN1OctetString == null) {
            return x500Name;
        }
        try {
            GeneralName[] generalNameArray = GeneralNames.getInstance(aSN1OctetString.getOctets()).getNames();
            for (int i = 0; i < generalNameArray.length; ++i) {
                if (generalNameArray[i].getTagNo() != 4) continue;
                return X500Name.getInstance(generalNameArray[i].getName());
            }
            return null;
        }
        catch (Exception exception) {
            return null;
        }
    }

    @Override
    public X500Principal getCertificateIssuer() {
        if (this.certificateIssuer == null) {
            return null;
        }
        try {
            return new X500Principal(this.certificateIssuer.getEncoded());
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private Set getExtensionOIDs(boolean bl) {
        Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
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
    public int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = super.hashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof X509CRLEntryObject) {
            X509CRLEntryObject x509CRLEntryObject = (X509CRLEntryObject)object;
            if (this.hashValueSet && x509CRLEntryObject.hashValueSet && this.hashValue != x509CRLEntryObject.hashValue) {
                return false;
            }
            return this.c.equals(x509CRLEntryObject.c);
        }
        return super.equals(this);
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
    public BigInteger getSerialNumber() {
        return this.c.getUserCertificate().getValue();
    }

    @Override
    public Date getRevocationDate() {
        return this.c.getRevocationDate().getDate();
    }

    @Override
    public boolean hasExtensions() {
        return this.c.getExtensions() != null;
    }

    @Override
    public String toString() {
        Enumeration enumeration;
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        stringBuilder.append("      userCertificate: ").append(this.getSerialNumber()).append(string);
        stringBuilder.append("       revocationDate: ").append(this.getRevocationDate()).append(string);
        stringBuilder.append("       certificateIssuer: ").append(this.getCertificateIssuer()).append(string);
        Extensions extensions = this.c.getExtensions();
        if (extensions != null && (enumeration = extensions.oids()).hasMoreElements()) {
            stringBuilder.append("   crlEntryExtensions:").append(string);
            while (enumeration.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    byte[] byArray = extension.getExtnValue().getOctets();
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(byArray);
                    stringBuilder.append("                       critical(").append(extension.isCritical()).append(") ");
                    try {
                        if (aSN1ObjectIdentifier.equals(Extension.reasonCode)) {
                            stringBuilder.append(CRLReason.getInstance(ASN1Enumerated.getInstance(aSN1InputStream.readObject()))).append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(Extension.certificateIssuer)) {
                            stringBuilder.append("Certificate issuer: ").append(GeneralNames.getInstance(aSN1InputStream.readObject())).append(string);
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
}

