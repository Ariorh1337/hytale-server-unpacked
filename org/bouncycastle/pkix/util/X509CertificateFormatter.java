/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util;

import java.io.FileReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509CertificateFormatter {
    private static Map<ASN1ObjectIdentifier, String> oidMap = new HashMap<ASN1ObjectIdentifier, String>();
    private static Map<ASN1ObjectIdentifier, String> keyAlgMap = new HashMap<ASN1ObjectIdentifier, String>();
    private static Map<KeyPurposeId, String> extUsageMap = new HashMap<KeyPurposeId, String>();
    private static Map<Integer, String> usageMap = new HashMap<Integer, String>();
    private static final String spaceStr = "                                                              ";

    private static String oidToLabel(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = oidMap.get(aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        return aSN1ObjectIdentifier.getId();
    }

    private static String keyAlgToLabel(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = keyAlgMap.get(aSN1ObjectIdentifier);
        if (string != null) {
            return string;
        }
        return aSN1ObjectIdentifier.getId();
    }

    private static String spaces(int n) {
        return spaceStr.substring(0, n);
    }

    private static String indent(String string, String string2, String string3) {
        int n;
        StringBuilder stringBuilder = new StringBuilder();
        int n2 = 0;
        string2 = string2.substring(0, string2.length() - string3.length());
        while ((n = string2.indexOf(string3)) > 0) {
            stringBuilder.append(string2.substring(n2, n));
            stringBuilder.append(string3);
            stringBuilder.append(string);
            if (n2 >= string2.length()) continue;
            string2 = string2.substring(n + string3.length());
        }
        if (stringBuilder.length() == 0) {
            return string2;
        }
        stringBuilder.append(string2);
        return stringBuilder.toString();
    }

    static void prettyPrintData(byte[] byArray, StringBuilder stringBuilder, String string) {
        if (byArray.length > 20) {
            stringBuilder.append(Hex.toHexString(byArray, 0, 20)).append(string);
            X509CertificateFormatter.format(stringBuilder, byArray, string);
        } else {
            stringBuilder.append(Hex.toHexString(byArray)).append(string);
        }
    }

    static void format(StringBuilder stringBuilder, byte[] byArray, String string) {
        for (int i = 20; i < byArray.length; i += 20) {
            if (i < byArray.length - 20) {
                stringBuilder.append("                       ").append(Hex.toHexString(byArray, i, 20)).append(string);
                continue;
            }
            stringBuilder.append("                       ").append(Hex.toHexString(byArray, i, byArray.length - i)).append(string);
        }
    }

    public static String asString(X509CertificateHolder x509CertificateHolder) {
        StringBuilder stringBuilder = new StringBuilder();
        String string = Strings.lineSeparator();
        String string2 = new DefaultSignatureNameFinder().getAlgorithmName(x509CertificateHolder.getSignatureAlgorithm());
        string2 = string2.replace("WITH", "with");
        String string3 = X509CertificateFormatter.keyAlgToLabel(x509CertificateHolder.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm());
        stringBuilder.append("  [0]         Version: ").append(x509CertificateHolder.getVersionNumber()).append(string);
        stringBuilder.append("         SerialNumber: ").append(x509CertificateHolder.getSerialNumber()).append(string);
        stringBuilder.append("             IssuerDN: ").append(x509CertificateHolder.getIssuer()).append(string);
        stringBuilder.append("           Start Date: ").append(x509CertificateHolder.getNotBefore()).append(string);
        stringBuilder.append("           Final Date: ").append(x509CertificateHolder.getNotAfter()).append(string);
        stringBuilder.append("            SubjectDN: ").append(x509CertificateHolder.getSubject()).append(string);
        stringBuilder.append("           Public Key: ").append(string3).append(string);
        stringBuilder.append("                       ");
        X509CertificateFormatter.prettyPrintData(x509CertificateHolder.getSubjectPublicKeyInfo().getPublicKeyData().getOctets(), stringBuilder, string);
        Extensions extensions = x509CertificateHolder.getExtensions();
        if (extensions != null) {
            Enumeration enumeration = extensions.oids();
            if (enumeration.hasMoreElements()) {
                stringBuilder.append("           Extensions: ").append(string);
            }
            while (enumeration.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
                if (extension.getExtnValue() != null) {
                    byte[] byArray = extension.getExtnValue().getOctets();
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(byArray);
                    String string4 = "                       ";
                    try {
                        boolean bl;
                        ASN1Object aSN1Object;
                        String string5 = X509CertificateFormatter.oidToLabel(aSN1ObjectIdentifier);
                        stringBuilder.append(string4).append(string5);
                        stringBuilder.append(": critical(").append(extension.isCritical()).append(") ").append(string);
                        string4 = string4 + X509CertificateFormatter.spaces(2 + string5.length());
                        if (aSN1ObjectIdentifier.equals(Extension.basicConstraints)) {
                            aSN1Object = BasicConstraints.getInstance(aSN1InputStream.readObject());
                            stringBuilder.append(string4).append("isCA : " + ((BasicConstraints)aSN1Object).isCA()).append(string);
                            if (!((BasicConstraints)aSN1Object).isCA()) continue;
                            stringBuilder.append(X509CertificateFormatter.spaces(2 + string5.length()));
                            stringBuilder.append("pathLenConstraint : " + ((BasicConstraints)aSN1Object).getPathLenConstraint()).append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(Extension.keyUsage)) {
                            aSN1Object = KeyUsage.getInstance(aSN1InputStream.readObject());
                            stringBuilder.append(string4);
                            bl = true;
                            Iterator<Object> iterator = usageMap.keySet().iterator();
                            while (iterator.hasNext()) {
                                int n = iterator.next();
                                if (!((KeyUsage)aSN1Object).hasUsages(n)) continue;
                                if (!bl) {
                                    stringBuilder.append(", ");
                                } else {
                                    bl = false;
                                }
                                stringBuilder.append(usageMap.get(n));
                            }
                            stringBuilder.append(string);
                            continue;
                        }
                        if (aSN1ObjectIdentifier.equals(Extension.extendedKeyUsage)) {
                            aSN1Object = ExtendedKeyUsage.getInstance(aSN1InputStream.readObject());
                            stringBuilder.append(string4);
                            bl = true;
                            for (KeyPurposeId keyPurposeId : extUsageMap.keySet()) {
                                if (!((ExtendedKeyUsage)aSN1Object).hasKeyPurposeId(keyPurposeId)) continue;
                                if (!bl) {
                                    stringBuilder.append(", ");
                                } else {
                                    bl = false;
                                }
                                stringBuilder.append(extUsageMap.get(keyPurposeId));
                            }
                            stringBuilder.append(string);
                            continue;
                        }
                        stringBuilder.append(string4).append("value = ").append(X509CertificateFormatter.indent(string4 + X509CertificateFormatter.spaces(8), ASN1Dump.dumpAsString(aSN1InputStream.readObject()), string)).append(string);
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
        stringBuilder.append("  Signature Algorithm: ").append(string2).append(string);
        stringBuilder.append("            Signature: ");
        X509CertificateFormatter.prettyPrintData(x509CertificateHolder.getSignature(), stringBuilder, string);
        return stringBuilder.toString();
    }

    public static void main(String[] stringArray) throws Exception {
        PEMParser pEMParser = new PEMParser(new FileReader(stringArray[0]));
        System.out.println(X509CertificateFormatter.asString((X509CertificateHolder)pEMParser.readObject()));
    }

    static {
        oidMap.put(Extension.subjectDirectoryAttributes, "subjectDirectoryAttributes");
        oidMap.put(Extension.subjectKeyIdentifier, "subjectKeyIdentifier");
        oidMap.put(Extension.keyUsage, "keyUsage");
        oidMap.put(Extension.privateKeyUsagePeriod, "privateKeyUsagePeriod");
        oidMap.put(Extension.subjectAlternativeName, "subjectAlternativeName");
        oidMap.put(Extension.issuerAlternativeName, "issuerAlternativeName");
        oidMap.put(Extension.basicConstraints, "basicConstraints");
        oidMap.put(Extension.cRLNumber, "cRLNumber");
        oidMap.put(Extension.reasonCode, "reasonCode");
        oidMap.put(Extension.instructionCode, "instructionCode");
        oidMap.put(Extension.invalidityDate, "invalidityDate");
        oidMap.put(Extension.deltaCRLIndicator, "deltaCRLIndicator");
        oidMap.put(Extension.issuingDistributionPoint, "issuingDistributionPoint");
        oidMap.put(Extension.certificateIssuer, "certificateIssuer");
        oidMap.put(Extension.nameConstraints, "nameConstraints");
        oidMap.put(Extension.cRLDistributionPoints, "cRLDistributionPoints");
        oidMap.put(Extension.certificatePolicies, "certificatePolicies");
        oidMap.put(Extension.policyMappings, "policyMappings");
        oidMap.put(Extension.authorityKeyIdentifier, "authorityKeyIdentifier");
        oidMap.put(Extension.policyConstraints, "policyConstraints");
        oidMap.put(Extension.extendedKeyUsage, "extendedKeyUsage");
        oidMap.put(Extension.freshestCRL, "freshestCRL");
        oidMap.put(Extension.inhibitAnyPolicy, "inhibitAnyPolicy");
        oidMap.put(Extension.authorityInfoAccess, "authorityInfoAccess");
        oidMap.put(Extension.subjectInfoAccess, "subjectInfoAccess");
        oidMap.put(Extension.logoType, "logoType");
        oidMap.put(Extension.biometricInfo, "biometricInfo");
        oidMap.put(Extension.qCStatements, "qCStatements");
        oidMap.put(Extension.auditIdentity, "auditIdentity");
        oidMap.put(Extension.noRevAvail, "noRevAvail");
        oidMap.put(Extension.targetInformation, "targetInformation");
        oidMap.put(Extension.expiredCertsOnCRL, "expiredCertsOnCRL");
        usageMap.put(128, "digitalSignature");
        usageMap.put(64, "nonRepudiation");
        usageMap.put(32, "keyEncipherment");
        usageMap.put(16, "dataEncipherment");
        usageMap.put(8, "keyAgreement");
        usageMap.put(4, "keyCertSign");
        usageMap.put(2, "cRLSign");
        usageMap.put(1, "encipherOnly");
        usageMap.put(32768, "decipherOnly");
        extUsageMap.put(KeyPurposeId.anyExtendedKeyUsage, "anyExtendedKeyUsage");
        extUsageMap.put(KeyPurposeId.id_kp_serverAuth, "id_kp_serverAuth");
        extUsageMap.put(KeyPurposeId.id_kp_clientAuth, "id_kp_clientAuth");
        extUsageMap.put(KeyPurposeId.id_kp_codeSigning, "id_kp_codeSigning");
        extUsageMap.put(KeyPurposeId.id_kp_emailProtection, "id_kp_emailProtection");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecEndSystem, "id_kp_ipsecEndSystem");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecTunnel, "id_kp_ipsecTunnel");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecUser, "id_kp_ipsecUser");
        extUsageMap.put(KeyPurposeId.id_kp_timeStamping, "id_kp_timeStamping");
        extUsageMap.put(KeyPurposeId.id_kp_OCSPSigning, "id_kp_OCSPSigning");
        extUsageMap.put(KeyPurposeId.id_kp_dvcs, "id_kp_dvcs");
        extUsageMap.put(KeyPurposeId.id_kp_sbgpCertAAServerAuth, "id_kp_sbgpCertAAServerAuth");
        extUsageMap.put(KeyPurposeId.id_kp_scvp_responder, "id_kp_scvp_responder");
        extUsageMap.put(KeyPurposeId.id_kp_eapOverPPP, "id_kp_eapOverPPP");
        extUsageMap.put(KeyPurposeId.id_kp_eapOverLAN, "id_kp_eapOverLAN");
        extUsageMap.put(KeyPurposeId.id_kp_scvpServer, "id_kp_scvpServer");
        extUsageMap.put(KeyPurposeId.id_kp_scvpClient, "id_kp_scvpClient");
        extUsageMap.put(KeyPurposeId.id_kp_ipsecIKE, "id_kp_ipsecIKE");
        extUsageMap.put(KeyPurposeId.id_kp_capwapAC, "id_kp_capwapAC");
        extUsageMap.put(KeyPurposeId.id_kp_capwapWTP, "id_kp_capwapWTP");
        extUsageMap.put(KeyPurposeId.id_kp_cmcCA, "id_kp_cmcCA");
        extUsageMap.put(KeyPurposeId.id_kp_cmcRA, "id_kp_cmcRA");
        extUsageMap.put(KeyPurposeId.id_kp_cmKGA, "id_kp_cmKGA");
        extUsageMap.put(KeyPurposeId.id_kp_smartcardlogon, "id_kp_smartcardlogon");
        extUsageMap.put(KeyPurposeId.id_kp_macAddress, "id_kp_macAddress");
        extUsageMap.put(KeyPurposeId.id_kp_msSGC, "id_kp_msSGC");
        extUsageMap.put(KeyPurposeId.id_kp_nsSGC, "id_kp_nsSGC");
        keyAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "rsaEncryption");
        keyAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "id_ecPublicKey");
        keyAlgMap.put(EdECObjectIdentifiers.id_Ed25519, "id_Ed25519");
        keyAlgMap.put(EdECObjectIdentifiers.id_Ed448, "id_Ed448");
    }
}

