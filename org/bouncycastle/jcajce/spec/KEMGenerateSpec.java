/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.spec.KEMKDFSpec;
import org.bouncycastle.util.Arrays;

public class KEMGenerateSpec
extends KEMKDFSpec
implements AlgorithmParameterSpec {
    private static final byte[] EMPTY_OTHER_INFO = new byte[0];
    private static AlgorithmIdentifier DefKdf = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
    private final PublicKey publicKey;

    private KEMGenerateSpec(PublicKey publicKey, String string, int n, AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        super(algorithmIdentifier, byArray, string, n);
        this.publicKey = publicKey;
    }

    public KEMGenerateSpec(PublicKey publicKey, String string) {
        this(publicKey, string, 256, DefKdf, EMPTY_OTHER_INFO);
    }

    public KEMGenerateSpec(PublicKey publicKey, String string, int n) {
        this(publicKey, string, n, DefKdf, EMPTY_OTHER_INFO);
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public static final class Builder {
        private final PublicKey publicKey;
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;

        public Builder(PublicKey publicKey, String string, int n) {
            this.publicKey = publicKey;
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

        public KEMGenerateSpec build() {
            return new KEMGenerateSpec(this.publicKey, this.algorithmName, this.keySizeInBits, this.kdfAlgorithm, this.otherInfo);
        }
    }
}

