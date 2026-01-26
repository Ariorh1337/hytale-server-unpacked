/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.spec.KEMKDFSpec;
import org.bouncycastle.util.Arrays;

public class KTSParameterSpec
extends KEMKDFSpec
implements AlgorithmParameterSpec {
    private final AlgorithmParameterSpec parameterSpec;

    protected KTSParameterSpec(String string, int n, AlgorithmParameterSpec algorithmParameterSpec, AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        super(algorithmIdentifier, byArray, string, n);
        this.parameterSpec = algorithmParameterSpec;
    }

    public AlgorithmParameterSpec getParameterSpec() {
        return this.parameterSpec;
    }

    public static final class Builder {
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmParameterSpec parameterSpec;
        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;

        public Builder(String string, int n) {
            this(string, n, null);
        }

        public Builder(String string, int n, byte[] byArray) {
            this.algorithmName = string;
            this.keySizeInBits = n;
            this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
            this.otherInfo = byArray == null ? new byte[]{} : Arrays.clone(byArray);
        }

        public Builder withParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
            this.parameterSpec = algorithmParameterSpec;
            return this;
        }

        public Builder withNoKdf() {
            this.kdfAlgorithm = null;
            return this;
        }

        public Builder withKdfAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
            if (algorithmIdentifier == null) {
                throw new NullPointerException("kdfAlgorithm cannot be null");
            }
            this.kdfAlgorithm = algorithmIdentifier;
            return this;
        }

        public KTSParameterSpec build() {
            return new KTSParameterSpec(this.algorithmName, this.keySizeInBits, this.parameterSpec, this.kdfAlgorithm, this.otherInfo);
        }
    }
}

