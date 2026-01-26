/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.gen;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Objects;

public class ECKeyGenerator
extends JWKGenerator<ECKey> {
    private final Curve crv;

    public ECKeyGenerator(Curve crv) {
        this.crv = Objects.requireNonNull(crv);
    }

    @Override
    public ECKey generate() throws JOSEException {
        KeyPairGenerator generator;
        ECParameterSpec ecSpec = this.crv.toECParameterSpec();
        try {
            generator = this.keyStore != null ? KeyPairGenerator.getInstance("EC", this.keyStore.getProvider()) : (this.provider != null ? KeyPairGenerator.getInstance("EC", this.provider) : KeyPairGenerator.getInstance("EC"));
            if (this.secureRandom != null) {
                generator.initialize(ecSpec, this.secureRandom);
            } else {
                generator.initialize(ecSpec);
            }
        }
        catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        KeyPair kp = generator.generateKeyPair();
        ECKey.Builder builder = new ECKey.Builder(this.crv, (ECPublicKey)kp.getPublic()).privateKey(kp.getPrivate()).keyUse(this.use).keyOperations(this.ops).algorithm(this.alg).expirationTime(this.exp).notBeforeTime(this.nbf).issueTime(this.iat).keyStore(this.keyStore);
        if (this.tprKid) {
            builder.keyIDFromThumbprint();
        } else {
            builder.keyID(this.kid);
        }
        return builder.build();
    }
}

