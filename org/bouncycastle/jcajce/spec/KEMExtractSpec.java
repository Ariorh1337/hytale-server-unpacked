/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.spec.KEMKDFSpec;
import org.bouncycastle.util.Arrays;

public class KEMExtractSpec
extends KEMKDFSpec
implements AlgorithmParameterSpec {
    private static final byte[] EMPTY_OTHER_INFO = new byte[0];
    private static AlgorithmIdentifier DefKdf = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
    private final PrivateKey privateKey;
    private final byte[] encapsulation;

    private KEMExtractSpec(PrivateKey privateKey, byte[] byArray, String string, int n, AlgorithmIdentifier algorithmIdentifier, byte[] byArray2) {
        super(algorithmIdentifier, byArray2, string, n);
        this.privateKey = privateKey;
        this.encapsulation = Arrays.clone(byArray);
    }

    public KEMExtractSpec(PrivateKey privateKey, byte[] byArray, String string) {
        this(privateKey, byArray, string, 256);
    }

    public KEMExtractSpec(PrivateKey privateKey, byte[] byArray, String string, int n) {
        this(privateKey, byArray, string, n, DefKdf, EMPTY_OTHER_INFO);
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.encapsulation);
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public static final class Builder {
        private final PrivateKey privateKey;
        private final byte[] encapsulation;
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;

        public Builder(PrivateKey privateKey, byte[] byArray, String string, int n) {
            this.privateKey = privateKey;
            this.encapsulation = Arrays.clone(byArray);
            this.algorithmName = string;
            this.keySizeInBits = n;
            this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
            this.otherInfo = EMPTY_OTHER_INFO;
        }

        public Builder withNoKdf() {
            this.kdfAlgorithm = null;
            return this;
        }

        public Builder withKdfAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
            this.kdfAlgorithm = algorithmIdentifier;
            return this;
        }

        public Builder withOtherInfo(byte[] byArray) {
            this.otherInfo = byArray == null ? EMPTY_OTHER_INFO : Arrays.clone(byArray);
            return this;
        }

        public KEMExtractSpec build() {
            return new KEMExtractSpec(this.privateKey, this.encapsulation, this.algorithmName, this.keySizeInBits, this.kdfAlgorithm, this.otherInfo);
        }
    }
}

