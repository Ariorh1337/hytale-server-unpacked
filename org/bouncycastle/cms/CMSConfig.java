/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSSignedHelper;

public class CMSConfig {
    public static void setSigningEncryptionAlgorithmMapping(String string, String string2) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(string);
        CMSSignedHelper.INSTANCE.setSigningEncryptionAlgorithmMapping(aSN1ObjectIdentifier, string2);
    }

    public static void setSigningDigestAlgorithmMapping(String string, String string2) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(string);
    }
}

