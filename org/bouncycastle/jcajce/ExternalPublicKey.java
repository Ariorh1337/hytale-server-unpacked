/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.Arrays;

public class ExternalPublicKey
implements PublicKey {
    private final GeneralName location;
    private final AlgorithmIdentifier digestAlg;
    private final byte[] digest;

    public ExternalPublicKey(GeneralName generalName, AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        this.location = generalName;
        this.digestAlg = algorithmIdentifier;
        this.digest = Arrays.clone(byArray);
    }

    public ExternalPublicKey(PublicKey publicKey, GeneralName generalName, MessageDigest messageDigest) {
        this(generalName, MessageDigestUtils.getDigestAlgID(messageDigest.getAlgorithm()), messageDigest.digest(publicKey.getEncoded()));
    }

    public ExternalPublicKey(ExternalValue externalValue) {
        this(externalValue.getLocation(), externalValue.getHashAlg(), externalValue.getHashValue());
    }

    @Override
    public String getAlgorithm() {
        return "ExternalKey";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(BCObjectIdentifiers.external_value), new ExternalValue(this.location, this.digestAlg, this.digest)).getEncoded("DER");
        }
        catch (IOException iOException) {
            throw new IllegalStateException("unable to encode composite key: " + iOException.getMessage());
        }
    }
}

