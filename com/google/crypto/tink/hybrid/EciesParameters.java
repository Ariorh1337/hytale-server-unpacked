/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.aead.AesCtrHmacAeadParameters;
import com.google.crypto.tink.aead.AesGcmParameters;
import com.google.crypto.tink.aead.XChaCha20Poly1305Parameters;
import com.google.crypto.tink.daead.AesSivParameters;
import com.google.crypto.tink.hybrid.HybridParameters;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public final class EciesParameters
extends HybridParameters {
    private static final Set<Parameters> acceptedDemParameters = TinkBugException.exceptionIsBug(() -> EciesParameters.listAcceptedDemParameters());
    private final CurveType curveType;
    private final HashType hashType;
    @Nullable
    private final PointFormat nistCurvePointFormat;
    private final Variant variant;
    private final Parameters demParameters;
    @Nullable
    private final Bytes salt;

    private static Set<Parameters> listAcceptedDemParameters() throws GeneralSecurityException {
        HashSet<Parameters> acceptedDemParameters = new HashSet<Parameters>();
        acceptedDemParameters.add(AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build());
        acceptedDemParameters.add(AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build());
        acceptedDemParameters.add(AesCtrHmacAeadParameters.builder().setAesKeySizeBytes(16).setHmacKeySizeBytes(32).setTagSizeBytes(16).setIvSizeBytes(16).setHashType(AesCtrHmacAeadParameters.HashType.SHA256).setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX).build());
        acceptedDemParameters.add(AesCtrHmacAeadParameters.builder().setAesKeySizeBytes(32).setHmacKeySizeBytes(32).setTagSizeBytes(32).setIvSizeBytes(16).setHashType(AesCtrHmacAeadParameters.HashType.SHA256).setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX).build());
        acceptedDemParameters.add(XChaCha20Poly1305Parameters.create());
        acceptedDemParameters.add(AesSivParameters.builder().setKeySizeBytes(64).setVariant(AesSivParameters.Variant.NO_PREFIX).build());
        return Collections.unmodifiableSet(acceptedDemParameters);
    }

    private EciesParameters(CurveType curveType, HashType hashType, @Nullable PointFormat pointFormat, Parameters demParameters, Variant variant, Bytes salt) {
        this.curveType = curveType;
        this.hashType = hashType;
        this.nistCurvePointFormat = pointFormat;
        this.demParameters = demParameters;
        this.variant = variant;
        this.salt = salt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CurveType getCurveType() {
        return this.curveType;
    }

    public HashType getHashType() {
        return this.hashType;
    }

    @Nullable
    public PointFormat getNistCurvePointFormat() {
        return this.nistCurvePointFormat;
    }

    public Parameters getDemParameters() {
        return this.demParameters;
    }

    public Variant getVariant() {
        return this.variant;
    }

    @Nullable
    public Bytes getSalt() {
        return this.salt;
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public boolean equals(Object o) {
        if (!(o instanceof EciesParameters)) {
            return false;
        }
        EciesParameters that = (EciesParameters)o;
        return Objects.equals(that.getCurveType(), this.getCurveType()) && Objects.equals(that.getHashType(), this.getHashType()) && Objects.equals(that.getNistCurvePointFormat(), this.getNistCurvePointFormat()) && Objects.equals(that.getDemParameters(), this.getDemParameters()) && Objects.equals(that.getVariant(), this.getVariant()) && Objects.equals(that.getSalt(), this.getSalt());
    }

    public int hashCode() {
        return Objects.hash(EciesParameters.class, this.curveType, this.hashType, this.nistCurvePointFormat, this.demParameters, this.variant, this.salt);
    }

    public String toString() {
        return String.format("EciesParameters(curveType=%s, hashType=%s, pointFormat=%s, demParameters=%s, variant=%s, salt=%s)", this.curveType, this.hashType, this.nistCurvePointFormat, this.demParameters, this.variant, this.salt);
    }

    @Immutable
    public static final class CurveType {
        public static final CurveType NIST_P256 = new CurveType("NIST_P256");
        public static final CurveType NIST_P384 = new CurveType("NIST_P384");
        public static final CurveType NIST_P521 = new CurveType("NIST_P521");
        public static final CurveType X25519 = new CurveType("X25519");
        private final String name;

        private CurveType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class HashType {
        public static final HashType SHA1 = new HashType("SHA1");
        public static final HashType SHA224 = new HashType("SHA224");
        public static final HashType SHA256 = new HashType("SHA256");
        public static final HashType SHA384 = new HashType("SHA384");
        public static final HashType SHA512 = new HashType("SHA512");
        private final String name;

        private HashType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class PointFormat {
        public static final PointFormat COMPRESSED = new PointFormat("COMPRESSED");
        public static final PointFormat UNCOMPRESSED = new PointFormat("UNCOMPRESSED");
        public static final PointFormat LEGACY_UNCOMPRESSED = new PointFormat("LEGACY_UNCOMPRESSED");
        private final String name;

        private PointFormat(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
        public static final Variant CRUNCHY = new Variant("CRUNCHY");
        public static final Variant NO_PREFIX = new Variant("NO_PREFIX");
        private final String name;

        private Variant(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static final class Builder {
        private CurveType curveType = null;
        private HashType hashType = null;
        private PointFormat nistCurvePointFormat = null;
        private Parameters demParameters = null;
        private Variant variant = Variant.NO_PREFIX;
        @Nullable
        private Bytes salt = null;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setCurveType(CurveType curveType) {
            this.curveType = curveType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHashType(HashType hashType) {
            this.hashType = hashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setNistCurvePointFormat(PointFormat pointFormat) {
            this.nistCurvePointFormat = pointFormat;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setDemParameters(Parameters demParameters) throws GeneralSecurityException {
            if (!acceptedDemParameters.contains(demParameters)) {
                throw new GeneralSecurityException("Invalid DEM parameters " + demParameters + "; only AES128_GCM_RAW, AES256_GCM_RAW, AES128_CTR_HMAC_SHA256_RAW, AES256_CTR_HMAC_SHA256_RAW XCHACHA20_POLY1305_RAW and AES256_SIV_RAW are currently supported.");
            }
            this.demParameters = demParameters;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setVariant(Variant variant) {
            this.variant = variant;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setSalt(Bytes salt) {
            if (salt.size() == 0) {
                this.salt = null;
                return this;
            }
            this.salt = salt;
            return this;
        }

        public EciesParameters build() throws GeneralSecurityException {
            if (this.curveType == null) {
                throw new GeneralSecurityException("Elliptic curve type is not set");
            }
            if (this.hashType == null) {
                throw new GeneralSecurityException("Hash type is not set");
            }
            if (this.demParameters == null) {
                throw new GeneralSecurityException("DEM parameters are not set");
            }
            if (this.variant == null) {
                throw new GeneralSecurityException("Variant is not set");
            }
            if (this.curveType != CurveType.X25519 && this.nistCurvePointFormat == null) {
                throw new GeneralSecurityException("Point format is not set");
            }
            if (this.curveType == CurveType.X25519 && this.nistCurvePointFormat != null) {
                throw new GeneralSecurityException("For Curve25519 point format must not be set");
            }
            return new EciesParameters(this.curveType, this.hashType, this.nistCurvePointFormat, this.demParameters, this.variant, this.salt);
        }
    }
}

