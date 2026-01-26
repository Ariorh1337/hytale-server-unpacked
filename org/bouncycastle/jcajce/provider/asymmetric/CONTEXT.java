/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;

public class CONTEXT {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.CONTEXT$";

    public static class ContextAlgorithmParametersSpi
    extends AlgorithmParametersSpi {
        private ContextParameterSpec contextParameterSpec;

        protected boolean isASN1FormatString(String string) {
            return string == null || string.equals("ASN.1");
        }

        protected AlgorithmParameterSpec engineGetParameterSpec(Class clazz) throws InvalidParameterSpecException {
            if (clazz == null) {
                throw new NullPointerException("argument to getParameterSpec must not be null");
            }
            if (clazz != ContextParameterSpec.class) {
                throw new IllegalArgumentException("argument to getParameterSpec must be ContextParameterSpec.class");
            }
            return this.contextParameterSpec;
        }

        @Override
        protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof ContextParameterSpec)) {
                throw new IllegalArgumentException("argument to engineInit must be a ContextParameterSpec");
            }
            this.contextParameterSpec = (ContextParameterSpec)algorithmParameterSpec;
        }

        @Override
        protected void engineInit(byte[] byArray) throws IOException {
            throw new IllegalStateException("not implemented");
        }

        @Override
        protected void engineInit(byte[] byArray, String string) throws IOException {
            throw new IllegalStateException("not implemented");
        }

        @Override
        protected byte[] engineGetEncoded() throws IOException {
            throw new IllegalStateException("not implemented");
        }

        @Override
        protected byte[] engineGetEncoded(String string) throws IOException {
            throw new IllegalStateException("not implemented");
        }

        @Override
        protected String engineToString() {
            return "ContextParameterSpec";
        }
    }

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.CONTEXT", "org.bouncycastle.jcajce.provider.asymmetric.CONTEXT$ContextAlgorithmParametersSpi");
        }
    }
}

