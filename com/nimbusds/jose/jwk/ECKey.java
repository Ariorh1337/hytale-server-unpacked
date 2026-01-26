/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.CurveBasedJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMetadata;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyRevocation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.BigIntegerUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.math.BigInteger;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

@Immutable
public final class ECKey
extends JWK
implements AsymmetricJWK,
CurveBasedJWK {
    private static final long serialVersionUID = 1L;
    public static final Set<Curve> SUPPORTED_CURVES = Collections.unmodifiableSet(new HashSet<Curve>(Arrays.asList(Curve.P_256, Curve.SECP256K1, Curve.P_384, Curve.P_521)));
    private final Curve crv;
    private final Base64URL x;
    private final Base64URL y;
    private final Base64URL d;
    private final PrivateKey privateKey;

    public static Base64URL encodeCoordinate(int fieldSize, BigInteger coordinate) {
        int bytesToOutput;
        byte[] notPadded = BigIntegerUtils.toBytesUnsigned(coordinate);
        if (notPadded.length >= (bytesToOutput = (fieldSize + 7) / 8)) {
            return Base64URL.encode(notPadded);
        }
        byte[] padded = new byte[bytesToOutput];
        System.arraycopy(notPadded, 0, padded, bytesToOutput - notPadded.length, notPadded.length);
        return Base64URL.encode(padded);
    }

    private static void ensurePublicCoordinatesOnCurve(Curve crv, Base64URL x, Base64URL y) {
        if (!SUPPORTED_CURVES.contains(crv)) {
            throw new IllegalArgumentException("Unknown / unsupported curve: " + crv);
        }
        if (!ECChecks.isPointOnCurve(x.decodeToBigInteger(), y.decodeToBigInteger(), crv.toECParameterSpec())) {
            throw new IllegalArgumentException("Invalid EC JWK: The 'x' and 'y' public coordinates are not on the " + crv + " curve");
        }
    }

    @Deprecated
    public ECKey(Curve crv, Base64URL x, Base64URL y, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(crv, x, y, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
    }

    @Deprecated
    public ECKey(Curve crv, Base64URL x, Base64URL y, Base64URL d, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(crv, x, y, d, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
    }

    @Deprecated
    public ECKey(Curve crv, Base64URL x, Base64URL y, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(crv, x, y, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
    }

    @Deprecated
    public ECKey(Curve crv, ECPublicKey pub, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
    }

    @Deprecated
    public ECKey(Curve crv, ECPublicKey pub, ECPrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), ECKey.encodeCoordinate(priv.getParams().getCurve().getField().getFieldSize(), priv.getS()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
    }

    @Deprecated
    public ECKey(Curve crv, ECPublicKey pub, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
    }

    @Deprecated
    public ECKey(Curve crv, Base64URL x, Base64URL y, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyStore ks) {
        this(crv, x, y, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
    }

    public ECKey(Curve crv, Base64URL x, Base64URL y, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyRevocation revocation, KeyStore ks) {
        super(KeyType.EC, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
        this.crv = Objects.requireNonNull(crv, "The curve must not be null");
        this.x = Objects.requireNonNull(x, "The x coordinate must not be null");
        this.y = Objects.requireNonNull(y, "The y coordinate must not be null");
        ECKey.ensurePublicCoordinatesOnCurve(crv, x, y);
        this.ensureMatches(this.getParsedX509CertChain());
        this.d = null;
        this.privateKey = null;
    }

    @Deprecated
    public ECKey(Curve crv, Base64URL x, Base64URL y, Base64URL d, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyStore ks) {
        this(crv, x, y, d, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
    }

    public ECKey(Curve crv, Base64URL x, Base64URL y, Base64URL d, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyRevocation revocation, KeyStore ks) {
        super(KeyType.EC, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
        this.crv = Objects.requireNonNull(crv, "The curve must not be null");
        this.x = Objects.requireNonNull(x, "The x coordinate must not be null");
        this.y = Objects.requireNonNull(y, "The y coordinate must not be null");
        ECKey.ensurePublicCoordinatesOnCurve(crv, x, y);
        this.ensureMatches(this.getParsedX509CertChain());
        this.d = Objects.requireNonNull(d, "The d coordinate must not be null");
        this.privateKey = null;
    }

    @Deprecated
    public ECKey(Curve crv, Base64URL x, Base64URL y, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyStore ks) {
        this(crv, x, y, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
    }

    public ECKey(Curve crv, Base64URL x, Base64URL y, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyRevocation revocation, KeyStore ks) {
        super(KeyType.EC, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
        this.crv = Objects.requireNonNull(crv, "The curve must not be null");
        this.x = Objects.requireNonNull(x, "The x coordinate must not be null");
        this.y = Objects.requireNonNull(y, "The y coordinate must not be null");
        ECKey.ensurePublicCoordinatesOnCurve(crv, x, y);
        this.ensureMatches(this.getParsedX509CertChain());
        this.d = null;
        this.privateKey = priv;
    }

    @Deprecated
    public ECKey(Curve crv, ECPublicKey pub, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyStore ks) {
        this(crv, pub, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
    }

    public ECKey(Curve crv, ECPublicKey pub, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyRevocation revocation, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
    }

    @Deprecated
    public ECKey(Curve crv, ECPublicKey pub, ECPrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyStore ks) {
        this(crv, pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
    }

    public ECKey(Curve crv, ECPublicKey pub, ECPrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyRevocation revocation, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), ECKey.encodeCoordinate(priv.getParams().getCurve().getField().getFieldSize(), priv.getS()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
    }

    @Deprecated
    public ECKey(Curve crv, ECPublicKey pub, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, ks);
    }

    public ECKey(Curve crv, ECPublicKey pub, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, Date exp, Date nbf, Date iat, KeyRevocation revocation, KeyStore ks) {
        this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()), priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
    }

    @Override
    public Curve getCurve() {
        return this.crv;
    }

    public Base64URL getX() {
        return this.x;
    }

    public Base64URL getY() {
        return this.y;
    }

    public Base64URL getD() {
        return this.d;
    }

    public ECPublicKey toECPublicKey() throws JOSEException {
        return this.toECPublicKey(null);
    }

    public ECPublicKey toECPublicKey(Provider provider) throws JOSEException {
        ECParameterSpec spec = this.crv.toECParameterSpec();
        if (spec == null) {
            throw new JOSEException("Couldn't get EC parameter spec for curve " + this.crv);
        }
        ECPoint w = new ECPoint(this.x.decodeToBigInteger(), this.y.decodeToBigInteger());
        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(w, spec);
        try {
            KeyFactory keyFactory = provider == null ? KeyFactory.getInstance("EC") : KeyFactory.getInstance("EC", provider);
            return (ECPublicKey)keyFactory.generatePublic(publicKeySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public ECPrivateKey toECPrivateKey() throws JOSEException {
        return this.toECPrivateKey(null);
    }

    public ECPrivateKey toECPrivateKey(Provider provider) throws JOSEException {
        if (this.d == null) {
            return null;
        }
        ECParameterSpec spec = this.crv.toECParameterSpec();
        if (spec == null) {
            throw new JOSEException("Couldn't get EC parameter spec for curve " + this.crv);
        }
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(this.d.decodeToBigInteger(), spec);
        try {
            KeyFactory keyFactory = provider == null ? KeyFactory.getInstance("EC") : KeyFactory.getInstance("EC", provider);
            return (ECPrivateKey)keyFactory.generatePrivate(privateKeySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    @Override
    public PublicKey toPublicKey() throws JOSEException {
        return this.toECPublicKey();
    }

    @Override
    public PrivateKey toPrivateKey() throws JOSEException {
        ECPrivateKey prv = this.toECPrivateKey();
        if (prv != null) {
            return prv;
        }
        return this.privateKey;
    }

    @Override
    public KeyPair toKeyPair() throws JOSEException {
        return this.toKeyPair(null);
    }

    public KeyPair toKeyPair(Provider provider) throws JOSEException {
        if (this.privateKey != null) {
            return new KeyPair(this.toECPublicKey(provider), this.privateKey);
        }
        return new KeyPair(this.toECPublicKey(provider), this.toECPrivateKey(provider));
    }

    @Override
    public ECKey toRevokedJWK(KeyRevocation keyRevocation) {
        if (this.getKeyRevocation() != null) {
            throw new IllegalStateException("Already revoked");
        }
        return new Builder(this).keyRevocation(Objects.requireNonNull(keyRevocation)).build();
    }

    @Override
    public boolean matches(X509Certificate cert) {
        ECPublicKey certECKey;
        try {
            certECKey = (ECPublicKey)this.getParsedX509CertChain().get(0).getPublicKey();
        }
        catch (ClassCastException ex) {
            return false;
        }
        if (!this.getX().decodeToBigInteger().equals(certECKey.getW().getAffineX())) {
            return false;
        }
        return this.getY().decodeToBigInteger().equals(certECKey.getW().getAffineY());
    }

    private void ensureMatches(List<X509Certificate> chain) {
        if (chain == null) {
            return;
        }
        if (!this.matches(chain.get(0))) {
            throw new IllegalArgumentException("The public subject key info of the first X.509 certificate in the chain must match the JWK type and public parameters");
        }
    }

    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("crv", this.crv.toString());
        requiredParams.put("kty", this.getKeyType().getValue());
        requiredParams.put("x", this.x.toString());
        requiredParams.put("y", this.y.toString());
        return requiredParams;
    }

    @Override
    public boolean isPrivate() {
        return this.d != null || this.privateKey != null;
    }

    @Override
    public int size() {
        ECParameterSpec ecParameterSpec = this.crv.toECParameterSpec();
        if (ecParameterSpec == null) {
            throw new UnsupportedOperationException("Couldn't determine field size for curve " + this.crv.getName());
        }
        return ecParameterSpec.getCurve().getField().getFieldSize();
    }

    @Override
    public ECKey toPublicJWK() {
        return new ECKey(this.getCurve(), this.getX(), this.getY(), this.getKeyUse(), this.getKeyOperations(), this.getAlgorithm(), this.getKeyID(), this.getX509CertURL(), this.getX509CertThumbprint(), this.getX509CertSHA256Thumbprint(), this.getX509CertChain(), this.getExpirationTime(), this.getNotBeforeTime(), this.getIssueTime(), this.getKeyRevocation(), this.getKeyStore());
    }

    @Override
    public Map<String, Object> toJSONObject() {
        Map<String, Object> o = super.toJSONObject();
        o.put("crv", this.crv.toString());
        o.put("x", this.x.toString());
        o.put("y", this.y.toString());
        if (this.d != null) {
            o.put("d", this.d.toString());
        }
        return o;
    }

    public static ECKey parse(String s) throws ParseException {
        return ECKey.parse(JSONObjectUtils.parse(s));
    }

    public static ECKey parse(Map<String, Object> jsonObject) throws ParseException {
        Curve crv;
        if (!KeyType.EC.equals(JWKMetadata.parseKeyType(jsonObject))) {
            throw new ParseException("The key type \"kty\" must be EC", 0);
        }
        try {
            crv = Curve.parse(JSONObjectUtils.getString(jsonObject, "crv"));
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        Base64URL x = JSONObjectUtils.getBase64URL(jsonObject, "x");
        Base64URL y = JSONObjectUtils.getBase64URL(jsonObject, "y");
        Base64URL d = JSONObjectUtils.getBase64URL(jsonObject, "d");
        try {
            if (d == null) {
                return new ECKey(crv, x, y, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), JWKMetadata.parseExpirationTime(jsonObject), JWKMetadata.parseNotBeforeTime(jsonObject), JWKMetadata.parseIssueTime(jsonObject), JWKMetadata.parseKeyRevocation(jsonObject), null);
            }
            return new ECKey(crv, x, y, d, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), JWKMetadata.parseExpirationTime(jsonObject), JWKMetadata.parseNotBeforeTime(jsonObject), JWKMetadata.parseIssueTime(jsonObject), JWKMetadata.parseKeyRevocation(jsonObject), null);
        }
        catch (Exception ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

    public static ECKey parse(X509Certificate cert) throws JOSEException {
        if (!(cert.getPublicKey() instanceof ECPublicKey)) {
            throw new JOSEException("The public key of the X.509 certificate is not EC");
        }
        ECPublicKey publicKey = (ECPublicKey)cert.getPublicKey();
        try {
            JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
            String oid = certHolder.getSubjectPublicKeyInfo().getAlgorithm().getParameters().toString();
            Curve crv = Curve.forOID(oid);
            if (crv == null) {
                throw new JOSEException("Couldn't determine EC JWK curve for OID " + oid);
            }
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return new Builder(crv, publicKey).keyUse(KeyUse.from(cert)).keyID(cert.getSerialNumber().toString(10)).x509CertChain(Collections.singletonList(Base64.encode(cert.getEncoded()))).x509CertSHA256Thumbprint(Base64URL.encode(sha256.digest(cert.getEncoded()))).expirationTime(cert.getNotAfter()).notBeforeTime(cert.getNotBefore()).build();
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't encode x5t parameter: " + e.getMessage(), e);
        }
        catch (CertificateEncodingException e) {
            throw new JOSEException("Couldn't encode x5c parameter: " + e.getMessage(), e);
        }
    }

    public static ECKey load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
        Key key;
        Certificate cert = keyStore.getCertificate(alias);
        if (!(cert instanceof X509Certificate)) {
            return null;
        }
        X509Certificate x509Cert = (X509Certificate)cert;
        if (!(x509Cert.getPublicKey() instanceof ECPublicKey)) {
            throw new JOSEException("Couldn't load EC JWK: The key algorithm is not EC");
        }
        ECKey ecJWK = ECKey.parse(x509Cert);
        ecJWK = new Builder(ecJWK).keyID(alias).keyStore(keyStore).build();
        try {
            key = keyStore.getKey(alias, pin);
        }
        catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new JOSEException("Couldn't retrieve private EC key (bad pin?): " + e.getMessage(), e);
        }
        if (key instanceof ECPrivateKey) {
            return new Builder(ecJWK).privateKey((ECPrivateKey)key).build();
        }
        if (key instanceof PrivateKey && "EC".equalsIgnoreCase(key.getAlgorithm())) {
            return new Builder(ecJWK).privateKey((PrivateKey)key).build();
        }
        return ecJWK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ECKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ECKey ecKey = (ECKey)o;
        return Objects.equals(this.crv, ecKey.crv) && Objects.equals(this.x, ecKey.x) && Objects.equals(this.y, ecKey.y) && Objects.equals(this.d, ecKey.d) && Objects.equals(this.privateKey, ecKey.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.crv, this.x, this.y, this.d, this.privateKey);
    }

    public static class Builder {
        private final Curve crv;
        private final Base64URL x;
        private final Base64URL y;
        private Base64URL d;
        private PrivateKey priv;
        private KeyUse use;
        private Set<KeyOperation> ops;
        private Algorithm alg;
        private String kid;
        private URI x5u;
        @Deprecated
        private Base64URL x5t;
        private Base64URL x5t256;
        private List<Base64> x5c;
        private Date exp;
        private Date nbf;
        private Date iat;
        private KeyRevocation revocation;
        private KeyStore ks;

        public Builder(Curve crv, Base64URL x, Base64URL y) {
            this.crv = Objects.requireNonNull(crv, "The curve must not be null");
            this.x = Objects.requireNonNull(x, "The x coordinate must not be null");
            this.y = Objects.requireNonNull(y, "The y coordinate must not be null");
        }

        public Builder(Curve crv, ECPublicKey pub) {
            this(crv, ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineX()), ECKey.encodeCoordinate(pub.getParams().getCurve().getField().getFieldSize(), pub.getW().getAffineY()));
        }

        public Builder(ECKey ecJWK) {
            this.crv = ecJWK.crv;
            this.x = ecJWK.x;
            this.y = ecJWK.y;
            this.d = ecJWK.d;
            this.priv = ecJWK.privateKey;
            this.use = ecJWK.getKeyUse();
            this.ops = ecJWK.getKeyOperations();
            this.alg = ecJWK.getAlgorithm();
            this.kid = ecJWK.getKeyID();
            this.x5u = ecJWK.getX509CertURL();
            this.x5t = ecJWK.getX509CertThumbprint();
            this.x5t256 = ecJWK.getX509CertSHA256Thumbprint();
            this.x5c = ecJWK.getX509CertChain();
            this.exp = ecJWK.getExpirationTime();
            this.nbf = ecJWK.getNotBeforeTime();
            this.iat = ecJWK.getIssueTime();
            this.revocation = ecJWK.getKeyRevocation();
            this.ks = ecJWK.getKeyStore();
        }

        public Builder d(Base64URL d) {
            this.d = d;
            return this;
        }

        public Builder privateKey(ECPrivateKey priv) {
            this.d = priv != null ? ECKey.encodeCoordinate(priv.getParams().getCurve().getField().getFieldSize(), priv.getS()) : null;
            return this;
        }

        public Builder privateKey(PrivateKey priv) {
            if (priv instanceof ECPrivateKey) {
                return this.privateKey((ECPrivateKey)priv);
            }
            if (priv != null && !"EC".equalsIgnoreCase(priv.getAlgorithm())) {
                throw new IllegalArgumentException("The private key algorithm must be EC");
            }
            this.priv = priv;
            return this;
        }

        public Builder keyUse(KeyUse use) {
            this.use = use;
            return this;
        }

        public Builder keyOperations(Set<KeyOperation> ops) {
            this.ops = ops;
            return this;
        }

        public Builder algorithm(Algorithm alg) {
            this.alg = alg;
            return this;
        }

        public Builder keyID(String kid) {
            this.kid = kid;
            return this;
        }

        public Builder keyIDFromThumbprint() throws JOSEException {
            return this.keyIDFromThumbprint("SHA-256");
        }

        public Builder keyIDFromThumbprint(String hashAlg) throws JOSEException {
            LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
            requiredParams.put("crv", this.crv.toString());
            requiredParams.put("kty", KeyType.EC.getValue());
            requiredParams.put("x", this.x.toString());
            requiredParams.put("y", this.y.toString());
            this.kid = ThumbprintUtils.compute(hashAlg, requiredParams).toString();
            return this;
        }

        public Builder x509CertURL(URI x5u) {
            this.x5u = x5u;
            return this;
        }

        @Deprecated
        public Builder x509CertThumbprint(Base64URL x5t) {
            this.x5t = x5t;
            return this;
        }

        public Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
            this.x5t256 = x5t256;
            return this;
        }

        public Builder x509CertChain(List<Base64> x5c) {
            this.x5c = x5c;
            return this;
        }

        public Builder expirationTime(Date exp) {
            this.exp = exp;
            return this;
        }

        public Builder notBeforeTime(Date nbf) {
            this.nbf = nbf;
            return this;
        }

        public Builder issueTime(Date iat) {
            this.iat = iat;
            return this;
        }

        public Builder keyRevocation(KeyRevocation revocation) {
            this.revocation = revocation;
            return this;
        }

        public Builder keyStore(KeyStore keyStore) {
            this.ks = keyStore;
            return this;
        }

        public ECKey build() {
            try {
                if (this.d == null && this.priv == null) {
                    return new ECKey(this.crv, this.x, this.y, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.exp, this.nbf, this.iat, this.revocation, this.ks);
                }
                if (this.priv != null) {
                    return new ECKey(this.crv, this.x, this.y, this.priv, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.exp, this.nbf, this.iat, this.revocation, this.ks);
                }
                return new ECKey(this.crv, this.x, this.y, this.d, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.exp, this.nbf, this.iat, this.revocation, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}

