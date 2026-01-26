/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.jcajce.JsseHostnameAuthorizer;
import org.bouncycastle.util.IPAddress;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class JsseDefaultHostnameAuthorizer
implements JsseHostnameAuthorizer {
    private static Logger LOG = Logger.getLogger(JsseDefaultHostnameAuthorizer.class.getName());
    private final Set<String> knownSuffixes;

    public JsseDefaultHostnameAuthorizer(Set<String> set) {
        this.knownSuffixes = set;
    }

    @Override
    public boolean verified(String string, SSLSession sSLSession) throws IOException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            X509Certificate x509Certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(sSLSession.getPeerCertificates()[0].getEncoded()));
            return this.verify(string, x509Certificate);
        }
        catch (Exception exception) {
            if (exception instanceof ESTException) {
                throw (ESTException)exception;
            }
            throw new ESTException(exception.getMessage(), exception);
        }
    }

    public boolean verify(String string, X509Certificate x509Certificate) throws IOException {
        Object object;
        int n;
        AttributeTypeAndValue[] attributeTypeAndValueArray;
        Object object2;
        Object object3;
        if (string == null) {
            throw new NullPointerException("'name' cannot be null");
        }
        boolean bl = false;
        boolean bl2 = IPAddress.isValidIPv4(string);
        boolean bl3 = !bl2 && IPAddress.isValidIPv6(string);
        boolean bl4 = bl2 || bl3;
        try {
            object3 = x509Certificate.getSubjectAlternativeNames();
            if (object3 != null) {
                object2 = null;
                Iterator<List<?>> iterator = object3.iterator();
                block8: while (iterator.hasNext()) {
                    attributeTypeAndValueArray = iterator.next();
                    n = (Integer)attributeTypeAndValueArray.get(0);
                    switch (n) {
                        case 2: {
                            if (!bl4 && JsseDefaultHostnameAuthorizer.isValidNameMatch(string, (String)attributeTypeAndValueArray.get(1), this.knownSuffixes)) {
                                return true;
                            }
                            bl = true;
                            break;
                        }
                        case 7: {
                            if (!bl4) continue block8;
                            object = (String)attributeTypeAndValueArray.get(1);
                            if (string.equalsIgnoreCase((String)object)) {
                                return true;
                            }
                            if (!bl3 || !IPAddress.isValidIPv6((String)object)) continue block8;
                            try {
                                if (object2 == null) {
                                    object2 = InetAddress.getByName(string);
                                }
                                if (!object2.equals(InetAddress.getByName((String)object))) continue block8;
                                return true;
                            }
                            catch (UnknownHostException unknownHostException) {
                                break;
                            }
                        }
                        default: {
                            if (!LOG.isLoggable(Level.INFO)) continue block8;
                            object = attributeTypeAndValueArray.get(1) instanceof byte[] ? Hex.toHexString((byte[])attributeTypeAndValueArray.get(1)) : attributeTypeAndValueArray.get(1).toString();
                            LOG.log(Level.INFO, "ignoring type " + n + " value = " + (String)object);
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            throw new ESTException(exception.getMessage(), exception);
        }
        if (bl4 || bl) {
            return false;
        }
        object3 = x509Certificate.getSubjectX500Principal();
        if (object3 == null) {
            return false;
        }
        object2 = X500Name.getInstance(((X500Principal)object3).getEncoded()).getRDNs();
        for (int i = ((RDN[])object2).length - 1; i >= 0; --i) {
            attributeTypeAndValueArray = object2[i].getTypesAndValues();
            for (n = 0; n != attributeTypeAndValueArray.length; ++n) {
                object = attributeTypeAndValueArray[n];
                if (!BCStyle.CN.equals(((AttributeTypeAndValue)object).getType())) continue;
                ASN1Primitive aSN1Primitive = ((AttributeTypeAndValue)object).getValue().toASN1Primitive();
                return aSN1Primitive instanceof ASN1String && JsseDefaultHostnameAuthorizer.isValidNameMatch(string, ((ASN1String)((Object)aSN1Primitive)).getString(), this.knownSuffixes);
            }
        }
        return false;
    }

    public static boolean isValidNameMatch(String string, String string2, Set<String> set) throws IOException {
        if (string2.contains("*")) {
            int n = string2.indexOf(42);
            if (n == string2.lastIndexOf("*")) {
                if (string2.contains("..") || string2.charAt(string2.length() - 1) == '*') {
                    return false;
                }
                int n2 = string2.indexOf(46, n);
                if (set != null && set.contains(Strings.toLowerCase(string2.substring(n2)))) {
                    throw new IOException("Wildcard `" + string2 + "` matches known public suffix.");
                }
                String string3 = Strings.toLowerCase(string2.substring(n + 1));
                String string4 = Strings.toLowerCase(string);
                if (string4.equals(string3)) {
                    return false;
                }
                if (string3.length() > string4.length()) {
                    return false;
                }
                if (n > 0) {
                    if (string4.startsWith(string2.substring(0, n)) && string4.endsWith(string3)) {
                        return string4.substring(n, string4.length() - string3.length()).indexOf(46) < 0;
                    }
                    return false;
                }
                String string5 = string4.substring(0, string4.length() - string3.length());
                if (string5.indexOf(46) > 0) {
                    return false;
                }
                return string4.endsWith(string3);
            }
            return false;
        }
        return string.equalsIgnoreCase(string2);
    }
}

