/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class NoSig {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.NoSig$";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Signature." + X509ObjectIdentifiers.id_alg_noSignature, "org.bouncycastle.jcajce.provider.asymmetric.NoSig$SigSpi");
            configurableProvider.addAlgorithm("Signature." + X509ObjectIdentifiers.id_alg_unsigned, "org.bouncycastle.jcajce.provider.asymmetric.NoSig$SigSpi");
        }
    }

    public static class SigSpi
    extends SignatureSpi {
        @Override
        protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
            throw new InvalidKeyException("attempt to pass public key to NoSig");
        }

        @Override
        protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
            throw new InvalidKeyException("attempt to pass private key to NoSig");
        }

        @Override
        protected void engineUpdate(byte by) throws SignatureException {
        }

        @Override
        protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        }

        @Override
        protected byte[] engineSign() throws SignatureException {
            return new byte[0];
        }

        @Override
        protected boolean engineVerify(byte[] byArray) throws SignatureException {
            return false;
        }

        @Override
        protected void engineSetParameter(String string, Object object) throws InvalidParameterException {
        }

        @Override
        protected Object engineGetParameter(String string) throws InvalidParameterException {
            return null;
        }
    }
}

