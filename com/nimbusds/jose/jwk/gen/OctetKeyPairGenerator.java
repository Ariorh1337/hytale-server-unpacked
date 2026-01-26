/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.gen;

import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.X25519;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import com.nimbusds.jose.util.Base64URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class OctetKeyPairGenerator
extends JWKGenerator<OctetKeyPair> {
    private final Curve crv;
    public static final Set<Curve> SUPPORTED_CURVES;

    public OctetKeyPairGenerator(Curve crv) {
        if (!SUPPORTED_CURVES.contains(Objects.requireNonNull(crv))) {
            throw new IllegalArgumentException("Curve not supported for OKP generation");
        }
        this.crv = crv;
    }

    @Override
    public OctetKeyPair generate() throws JOSEException {
        Base64URL publicKey;
        Base64URL privateKey;
        if (this.crv.equals(Curve.X25519)) {
            byte[] publicKeyBytes;
            byte[] privateKeyBytes;
            try {
                privateKeyBytes = X25519.generatePrivateKey();
                publicKeyBytes = X25519.publicFromPrivate(privateKeyBytes);
            }
            catch (InvalidKeyException e) {
                throw new JOSEException(e.getMessage(), e);
            }
            privateKey = Base64URL.encode(privateKeyBytes);
            publicKey = Base64URL.encode(publicKeyBytes);
        } else if (this.crv.equals(Curve.Ed25519)) {
            Ed25519Sign.KeyPair tinkKeyPair;
            try {
                if (this.secureRandom != null) {
                    byte[] seed = new byte[32];
                    this.secureRandom.nextBytes(seed);
                    tinkKeyPair = Ed25519Sign.KeyPair.newKeyPairFromSeed(seed);
                } else {
                    tinkKeyPair = Ed25519Sign.KeyPair.newKeyPair();
                }
            }
            catch (GeneralSecurityException e) {
                throw new JOSEException(e.getMessage(), e);
            }
            privateKey = Base64URL.encode(tinkKeyPair.getPrivateKey());
            publicKey = Base64URL.encode(tinkKeyPair.getPublicKey());
        } else {
            throw new JOSEException("Curve not supported");
        }
        OctetKeyPair.Builder builder = new OctetKeyPair.Builder(this.crv, publicKey).d(privateKey).keyUse(this.use).keyOperations(this.ops).algorithm(this.alg).expirationTime(this.exp).notBeforeTime(this.nbf).issueTime(this.iat);
        if (this.tprKid) {
            builder.keyIDFromThumbprint();
        } else {
            builder.keyID(this.kid);
        }
        return builder.build();
    }

    static {
        LinkedHashSet<Curve> curves = new LinkedHashSet<Curve>();
        curves.add(Curve.X25519);
        curves.add(Curve.Ed25519);
        SUPPORTED_CURVES = Collections.unmodifiableSet(curves);
    }
}

