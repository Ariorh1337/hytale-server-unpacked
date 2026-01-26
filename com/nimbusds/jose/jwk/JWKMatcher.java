/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.CurveBasedJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Immutable
public class JWKMatcher {
    private final Set<KeyType> types;
    private final Set<KeyUse> uses;
    private final Set<KeyOperation> ops;
    private final Set<Algorithm> algs;
    private final Set<String> ids;
    private final boolean withUseOnly;
    private final boolean withIDOnly;
    private final boolean privateOnly;
    private final boolean publicOnly;
    private final boolean nonRevokedOnly;
    private final boolean revokedOnly;
    private final int minSizeBits;
    private final int maxSizeBits;
    private final Set<Integer> sizesBits;
    private final Set<Curve> curves;
    private final Set<Base64URL> x5tS256s;
    private final boolean withX5COnly;

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, 0, 0);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Curve> curves) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null, curves);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves) {
        this(types, uses, ops, algs, ids, false, false, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean withUseOnly, boolean withIDOnly, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves) {
        this(types, uses, ops, algs, ids, withUseOnly, withIDOnly, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, null);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean withUseOnly, boolean withIDOnly, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves, Set<Base64URL> x5tS256s) {
        this(types, uses, ops, algs, ids, withUseOnly, withIDOnly, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, x5tS256s, false);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean withUseOnly, boolean withIDOnly, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves, Set<Base64URL> x5tS256s, boolean withX5COnly) {
        this(types, uses, ops, algs, ids, withUseOnly, withIDOnly, privateOnly, publicOnly, false, false, minSizeBits, maxSizeBits, sizesBits, curves, x5tS256s, withX5COnly);
    }

    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean withUseOnly, boolean withIDOnly, boolean privateOnly, boolean publicOnly, boolean nonRevokedOnly, boolean revokedOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves, Set<Base64URL> x5tS256s, boolean withX5COnly) {
        this.types = types;
        this.uses = uses;
        this.ops = ops;
        this.algs = algs;
        this.ids = ids;
        this.withUseOnly = withUseOnly;
        this.withIDOnly = withIDOnly;
        this.privateOnly = privateOnly;
        this.publicOnly = publicOnly;
        this.nonRevokedOnly = nonRevokedOnly;
        this.revokedOnly = revokedOnly;
        this.minSizeBits = minSizeBits;
        this.maxSizeBits = maxSizeBits;
        this.sizesBits = sizesBits;
        this.curves = curves;
        this.x5tS256s = x5tS256s;
        this.withX5COnly = withX5COnly;
    }

    public static JWKMatcher forJWEHeader(JWEHeader jweHeader) {
        return new Builder().keyType(KeyType.forAlgorithm(jweHeader.getAlgorithm())).keyID(jweHeader.getKeyID()).keyUses(KeyUse.ENCRYPTION, null).algorithms(jweHeader.getAlgorithm(), null).build();
    }

    public static JWKMatcher forJWSHeader(JWSHeader jwsHeader) {
        JWSAlgorithm algorithm = jwsHeader.getAlgorithm();
        if (JWSAlgorithm.Family.RSA.contains(algorithm) || JWSAlgorithm.Family.EC.contains(algorithm)) {
            return new Builder().keyType(KeyType.forAlgorithm(algorithm)).keyID(jwsHeader.getKeyID()).keyUses(KeyUse.SIGNATURE, null).algorithms(algorithm, null).x509CertSHA256Thumbprint(jwsHeader.getX509CertSHA256Thumbprint()).build();
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
            return new Builder().keyType(KeyType.forAlgorithm(algorithm)).keyID(jwsHeader.getKeyID()).privateOnly(true).algorithms(algorithm, null).build();
        }
        if (JWSAlgorithm.Family.ED.contains(algorithm)) {
            return new Builder().keyType(KeyType.forAlgorithm(algorithm)).keyID(jwsHeader.getKeyID()).keyUses(KeyUse.SIGNATURE, null).algorithms(algorithm, null).curves(Curve.forJWSAlgorithm(algorithm)).build();
        }
        return null;
    }

    public Set<KeyType> getKeyTypes() {
        return this.types;
    }

    public Set<KeyUse> getKeyUses() {
        return this.uses;
    }

    public Set<KeyOperation> getKeyOperations() {
        return this.ops;
    }

    public Set<Algorithm> getAlgorithms() {
        return this.algs;
    }

    public Set<String> getKeyIDs() {
        return this.ids;
    }

    @Deprecated
    public boolean hasKeyUse() {
        return this.isWithKeyUseOnly();
    }

    public boolean isWithKeyUseOnly() {
        return this.withUseOnly;
    }

    @Deprecated
    public boolean hasKeyID() {
        return this.isWithKeyIDOnly();
    }

    public boolean isWithKeyIDOnly() {
        return this.withIDOnly;
    }

    public boolean isPrivateOnly() {
        return this.privateOnly;
    }

    public boolean isPublicOnly() {
        return this.publicOnly;
    }

    public boolean isNonRevokedOnly() {
        return this.nonRevokedOnly;
    }

    public boolean isRevokedOnly() {
        return this.revokedOnly;
    }

    @Deprecated
    public int getMinSize() {
        return this.getMinKeySize();
    }

    public int getMinKeySize() {
        return this.minSizeBits;
    }

    @Deprecated
    public int getMaxSize() {
        return this.getMaxKeySize();
    }

    public int getMaxKeySize() {
        return this.maxSizeBits;
    }

    public Set<Integer> getKeySizes() {
        return this.sizesBits;
    }

    public Set<Curve> getCurves() {
        return this.curves;
    }

    public Set<Base64URL> getX509CertSHA256Thumbprints() {
        return this.x5tS256s;
    }

    @Deprecated
    public boolean hasX509CertChain() {
        return this.isWithX509CertChainOnly();
    }

    public boolean isWithX509CertChainOnly() {
        return this.withX5COnly;
    }

    public boolean matches(JWK key) {
        if (this.withUseOnly && key.getKeyUse() == null) {
            return false;
        }
        if (this.withIDOnly && (key.getKeyID() == null || key.getKeyID().trim().isEmpty())) {
            return false;
        }
        if (this.privateOnly && !key.isPrivate()) {
            return false;
        }
        if (this.publicOnly && key.isPrivate()) {
            return false;
        }
        if (this.nonRevokedOnly && key.getKeyRevocation() != null) {
            return false;
        }
        if (this.revokedOnly && key.getKeyRevocation() == null) {
            return false;
        }
        if (this.types != null && !this.types.contains(key.getKeyType())) {
            return false;
        }
        if (this.uses != null && !this.uses.contains(key.getKeyUse())) {
            return false;
        }
        if (!(this.ops == null || this.ops.contains(null) && key.getKeyOperations() == null || key.getKeyOperations() != null && this.ops.containsAll(key.getKeyOperations()))) {
            return false;
        }
        if (this.algs != null && !this.algs.contains(key.getAlgorithm())) {
            return false;
        }
        if (this.ids != null && !this.ids.contains(key.getKeyID())) {
            return false;
        }
        if (this.minSizeBits > 0 && key.size() < this.minSizeBits) {
            return false;
        }
        if (this.maxSizeBits > 0 && key.size() > this.maxSizeBits) {
            return false;
        }
        if (this.sizesBits != null && !this.sizesBits.contains(key.size())) {
            return false;
        }
        if (this.curves != null) {
            if (!(key instanceof CurveBasedJWK)) {
                return false;
            }
            CurveBasedJWK curveBasedJWK = (CurveBasedJWK)((Object)key);
            if (!this.curves.contains(curveBasedJWK.getCurve())) {
                return false;
            }
        }
        if (this.x5tS256s != null) {
            boolean matchingCertFound = false;
            if (key.getX509CertChain() != null && !key.getX509CertChain().isEmpty()) {
                try {
                    X509Certificate cert = X509CertUtils.parseWithException(key.getX509CertChain().get(0).decode());
                    matchingCertFound = this.x5tS256s.contains(X509CertUtils.computeSHA256Thumbprint(cert));
                }
                catch (CertificateException cert) {
                    // empty catch block
                }
            }
            boolean matchingX5T256Found = this.x5tS256s.contains(key.getX509CertSHA256Thumbprint());
            if (!matchingCertFound && !matchingX5T256Found) {
                return false;
            }
        }
        if (this.withX5COnly) {
            return key.getX509CertChain() != null && !key.getX509CertChain().isEmpty();
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        JWKMatcher.append(sb, "kty", this.types);
        JWKMatcher.append(sb, "use", this.uses);
        JWKMatcher.append(sb, "key_ops", this.ops);
        JWKMatcher.append(sb, "alg", this.algs);
        JWKMatcher.append(sb, "kid", this.ids);
        if (this.withUseOnly) {
            sb.append("with_use_only=true ");
        }
        if (this.withIDOnly) {
            sb.append("with_id_only=true ");
        }
        if (this.privateOnly) {
            sb.append("private_only=true ");
        }
        if (this.publicOnly) {
            sb.append("public_only=true ");
        }
        if (this.nonRevokedOnly) {
            sb.append("non_revoked_only=true ");
        }
        if (this.revokedOnly) {
            sb.append("revoked_only=true ");
        }
        if (this.minSizeBits > 0) {
            sb.append("min_size=" + this.minSizeBits + " ");
        }
        if (this.maxSizeBits > 0) {
            sb.append("max_size=" + this.maxSizeBits + " ");
        }
        JWKMatcher.append(sb, "size", this.sizesBits);
        JWKMatcher.append(sb, "crv", this.curves);
        JWKMatcher.append(sb, "x5t#S256", this.x5tS256s);
        if (this.withX5COnly) {
            sb.append("with_x5c_only=true");
        }
        return sb.toString().trim();
    }

    private static void append(StringBuilder sb, String key, Set<?> values) {
        if (values != null) {
            sb.append(key);
            sb.append('=');
            if (values.size() == 1) {
                Object value = values.iterator().next();
                if (value == null) {
                    sb.append("ANY");
                } else {
                    sb.append(value.toString().trim());
                }
            } else {
                sb.append(values.toString().trim());
            }
            sb.append(' ');
        }
    }

    public static class Builder {
        private Set<KeyType> types;
        private Set<KeyUse> uses;
        private Set<KeyOperation> ops;
        private Set<Algorithm> algs;
        private Set<String> ids;
        private boolean withUseOnly = false;
        private boolean withIDOnly = false;
        private boolean privateOnly = false;
        private boolean publicOnly = false;
        private boolean nonRevokedOnly = false;
        private boolean revokedOnly = false;
        private int minSizeBits = 0;
        private int maxSizeBits = 0;
        private Set<Integer> sizesBits;
        private Set<Curve> curves;
        private Set<Base64URL> x5tS256s;
        private boolean withX5COnly = false;

        public Builder() {
        }

        public Builder(JWKMatcher jwkMatcher) {
            this.types = jwkMatcher.getKeyTypes();
            this.uses = jwkMatcher.getKeyUses();
            this.ops = jwkMatcher.getKeyOperations();
            this.algs = jwkMatcher.getAlgorithms();
            this.ids = jwkMatcher.getKeyIDs();
            this.withUseOnly = jwkMatcher.isWithKeyUseOnly();
            this.withIDOnly = jwkMatcher.isWithKeyIDOnly();
            this.privateOnly = jwkMatcher.isPrivateOnly();
            this.publicOnly = jwkMatcher.isPublicOnly();
            this.nonRevokedOnly = jwkMatcher.isNonRevokedOnly();
            this.revokedOnly = jwkMatcher.isNonRevokedOnly();
            this.minSizeBits = jwkMatcher.getMinKeySize();
            this.maxSizeBits = jwkMatcher.getMaxKeySize();
            this.sizesBits = jwkMatcher.getKeySizes();
            this.curves = jwkMatcher.getCurves();
            this.x5tS256s = jwkMatcher.getX509CertSHA256Thumbprints();
            this.withX5COnly = jwkMatcher.isWithX509CertChainOnly();
        }

        public Builder keyType(KeyType kty) {
            this.types = kty == null ? null : new HashSet<KeyType>(Collections.singletonList(kty));
            return this;
        }

        public Builder keyTypes(KeyType ... types) {
            this.keyTypes(new LinkedHashSet<KeyType>(Arrays.asList(types)));
            return this;
        }

        public Builder keyTypes(Set<KeyType> types) {
            this.types = types;
            return this;
        }

        public Builder keyUse(KeyUse use) {
            this.uses = use == null ? null : new HashSet<KeyUse>(Collections.singletonList(use));
            return this;
        }

        public Builder keyUses(KeyUse ... uses) {
            this.keyUses(new LinkedHashSet<KeyUse>(Arrays.asList(uses)));
            return this;
        }

        public Builder keyUses(Set<KeyUse> uses) {
            this.uses = uses;
            return this;
        }

        public Builder keyOperation(KeyOperation op) {
            this.ops = op == null ? null : new HashSet<KeyOperation>(Collections.singletonList(op));
            return this;
        }

        public Builder keyOperations(KeyOperation ... ops) {
            this.keyOperations(new LinkedHashSet<KeyOperation>(Arrays.asList(ops)));
            return this;
        }

        public Builder keyOperations(Set<KeyOperation> ops) {
            this.ops = ops;
            return this;
        }

        public Builder algorithm(Algorithm alg) {
            this.algs = alg == null ? null : new HashSet<Algorithm>(Collections.singletonList(alg));
            return this;
        }

        public Builder algorithms(Algorithm ... algs) {
            this.algorithms(new LinkedHashSet<Algorithm>(Arrays.asList(algs)));
            return this;
        }

        public Builder algorithms(Set<Algorithm> algs) {
            this.algs = algs;
            return this;
        }

        public Builder keyID(String id) {
            this.ids = id == null ? null : new HashSet<String>(Collections.singletonList(id));
            return this;
        }

        public Builder keyIDs(String ... ids) {
            this.keyIDs(new LinkedHashSet<String>(Arrays.asList(ids)));
            return this;
        }

        public Builder keyIDs(Set<String> ids) {
            this.ids = ids;
            return this;
        }

        @Deprecated
        public Builder hasKeyUse(boolean hasUse) {
            return this.withKeyUseOnly(hasUse);
        }

        public Builder withKeyUseOnly(boolean withUseOnly) {
            this.withUseOnly = withUseOnly;
            return this;
        }

        @Deprecated
        public Builder hasKeyID(boolean hasID) {
            return this.withKeyIDOnly(hasID);
        }

        public Builder withKeyIDOnly(boolean withIDOnly) {
            this.withIDOnly = withIDOnly;
            return this;
        }

        public Builder privateOnly(boolean privateOnly) {
            this.privateOnly = privateOnly;
            return this;
        }

        public Builder publicOnly(boolean publicOnly) {
            this.publicOnly = publicOnly;
            return this;
        }

        public Builder nonRevokedOnly(boolean nonRevokedOnly) {
            this.nonRevokedOnly = nonRevokedOnly;
            return this;
        }

        public Builder revokedOnly(boolean revokedOnly) {
            this.revokedOnly = revokedOnly;
            return this;
        }

        public Builder minKeySize(int minSizeBits) {
            this.minSizeBits = minSizeBits;
            return this;
        }

        public Builder maxKeySize(int maxSizeBits) {
            this.maxSizeBits = maxSizeBits;
            return this;
        }

        public Builder keySize(int keySizeBits) {
            this.sizesBits = keySizeBits <= 0 ? null : Collections.singleton(keySizeBits);
            return this;
        }

        public Builder keySizes(int ... keySizesBits) {
            LinkedHashSet<Integer> sizesSet = new LinkedHashSet<Integer>();
            for (int keySize : keySizesBits) {
                sizesSet.add(keySize);
            }
            this.keySizes(sizesSet);
            return this;
        }

        public Builder keySizes(Set<Integer> keySizesBits) {
            this.sizesBits = keySizesBits;
            return this;
        }

        public Builder curve(Curve curve) {
            this.curves = curve == null ? null : Collections.singleton(curve);
            return this;
        }

        public Builder curves(Curve ... curves) {
            this.curves(new LinkedHashSet<Curve>(Arrays.asList(curves)));
            return this;
        }

        public Builder curves(Set<Curve> curves) {
            this.curves = curves;
            return this;
        }

        public Builder x509CertSHA256Thumbprint(Base64URL x5tS256) {
            this.x5tS256s = x5tS256 == null ? null : Collections.singleton(x5tS256);
            return this;
        }

        public Builder x509CertSHA256Thumbprints(Base64URL ... x5tS256s) {
            return this.x509CertSHA256Thumbprints(new LinkedHashSet<Base64URL>(Arrays.asList(x5tS256s)));
        }

        public Builder x509CertSHA256Thumbprints(Set<Base64URL> x5tS256s) {
            this.x5tS256s = x5tS256s;
            return this;
        }

        @Deprecated
        public Builder hasX509CertChain(boolean hasX5C) {
            return this.withX509CertChainOnly(hasX5C);
        }

        public Builder withX509CertChainOnly(boolean withX5CONly) {
            this.withX5COnly = withX5CONly;
            return this;
        }

        public JWKMatcher build() {
            return new JWKMatcher(this.types, this.uses, this.ops, this.algs, this.ids, this.withUseOnly, this.withIDOnly, this.privateOnly, this.publicOnly, this.nonRevokedOnly, this.revokedOnly, this.minSizeBits, this.maxSizeBits, this.sizesBits, this.curves, this.x5tS256s, this.withX5COnly);
        }
    }
}

