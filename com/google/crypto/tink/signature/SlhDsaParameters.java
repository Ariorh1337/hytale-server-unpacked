/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.signature.SignatureParameters;
import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public class SlhDsaParameters
extends SignatureParameters {
    public static final int SLH_DSA_128_PRIVATE_KEY_SIZE_BYTES = 64;
    private final HashType hashType;
    private final SignatureType signatureType;
    private final Variant variant;
    private final int privateKeySize;

    public static SlhDsaParameters createSlhDsaWithSha2And128S(Variant variant) {
        return new SlhDsaParameters(HashType.SHA2, 64, SignatureType.SMALL_SIGNATURE, variant);
    }

    private SlhDsaParameters(HashType hashType, int privateKeySizeBytes, SignatureType signatureType, Variant variant) {
        this.hashType = hashType;
        this.privateKeySize = privateKeySizeBytes;
        this.signatureType = signatureType;
        this.variant = variant;
    }

    public HashType getHashType() {
        return this.hashType;
    }

    public SignatureType getSignatureType() {
        return this.signatureType;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public int getPrivateKeySize() {
        return this.privateKeySize;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SlhDsaParameters)) {
            return false;
        }
        SlhDsaParameters other = (SlhDsaParameters)o;
        return other.getHashType() == this.getHashType() && other.getSignatureType() == this.getSignatureType() && other.getVariant() == this.getVariant() && other.getPrivateKeySize() == this.getPrivateKeySize();
    }

    public int hashCode() {
        return Objects.hash(SlhDsaParameters.class, this.hashType, this.privateKeySize, this.signatureType, this.variant);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "SLH-DSA-" + this.hashType.toString() + "-" + this.privateKeySize * 2 + this.signatureType + " instance, variant: " + this.variant;
    }

    @Immutable
    public static final class HashType {
        public static final HashType SHA2 = new HashType("SHA2");
        public static final HashType SHAKE = new HashType("SHAKE");
        private final String name;

        private HashType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class SignatureType {
        public static final SignatureType FAST_SIGNING = new SignatureType("F");
        public static final SignatureType SMALL_SIGNATURE = new SignatureType("S");
        private final String name;

        private SignatureType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
        public static final Variant NO_PREFIX = new Variant("NO_PREFIX");
        private final String name;

        private Variant(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

