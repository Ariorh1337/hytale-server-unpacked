/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.auth.CertificateUtil;
import com.hypixel.hytale.server.core.auth.SessionServiceClient;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JWTValidator {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final long CLOCK_SKEW_SECONDS = 300L;
    private static final JWSAlgorithm SUPPORTED_ALGORITHM = JWSAlgorithm.EdDSA;
    private final SessionServiceClient sessionServiceClient;
    private final String expectedIssuer;
    private final String expectedAudience;
    private volatile JWKSet cachedJwkSet;
    private volatile long jwksCacheExpiry;
    private final long jwksCacheDurationMs = TimeUnit.HOURS.toMillis(1L);
    private final ReentrantLock jwksFetchLock = new ReentrantLock();
    private volatile CompletableFuture<JWKSet> pendingFetch = null;

    public JWTValidator(@Nonnull SessionServiceClient sessionServiceClient, @Nonnull String expectedIssuer, @Nonnull String expectedAudience) {
        this.sessionServiceClient = sessionServiceClient;
        this.expectedIssuer = expectedIssuer;
        this.expectedAudience = expectedAudience;
    }

    @Nullable
    public JWTClaims validateToken(@Nonnull String accessToken, @Nullable X509Certificate clientCert) {
        if (accessToken.isEmpty()) {
            LOGGER.at(Level.WARNING).log("Access token is empty");
            return null;
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
                LOGGER.at(Level.WARNING).log("Unsupported JWT algorithm: %s (expected EdDSA)", algorithm);
                return null;
            }
            if (!this.verifySignatureWithRetry(signedJWT)) {
                LOGGER.at(Level.WARNING).log("JWT signature verification failed");
                return null;
            }
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            JWTClaims claims = new JWTClaims();
            claims.issuer = claimsSet.getIssuer();
            claims.audience = claimsSet.getAudience() != null && !claimsSet.getAudience().isEmpty() ? claimsSet.getAudience().get(0) : null;
            claims.subject = claimsSet.getSubject();
            claims.username = claimsSet.getStringClaim("username");
            claims.ipAddress = claimsSet.getStringClaim("ip");
            claims.issuedAt = claimsSet.getIssueTime() != null ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
            claims.expiresAt = claimsSet.getExpirationTime() != null ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
            claims.notBefore = claimsSet.getNotBeforeTime() != null ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
            Map<String, Object> cnfClaim = claimsSet.getJSONObjectClaim("cnf");
            if (cnfClaim != null) {
                claims.certificateFingerprint = (String)cnfClaim.get("x5t#S256");
            }
            if (!this.expectedIssuer.equals(claims.issuer)) {
                LOGGER.at(Level.WARNING).log("Invalid issuer: expected %s, got %s", (Object)this.expectedIssuer, (Object)claims.issuer);
                return null;
            }
            if (!this.expectedAudience.equals(claims.audience)) {
                LOGGER.at(Level.WARNING).log("Invalid audience: expected %s, got %s", (Object)this.expectedAudience, (Object)claims.audience);
                return null;
            }
            long nowSeconds = Instant.now().getEpochSecond();
            if (claims.expiresAt != null && nowSeconds >= claims.expiresAt + 300L) {
                LOGGER.at(Level.WARNING).log("Token expired (exp: %d, now: %d)", (Object)claims.expiresAt, nowSeconds);
                return null;
            }
            if (claims.notBefore != null && nowSeconds < claims.notBefore - 300L) {
                LOGGER.at(Level.WARNING).log("Token not yet valid (nbf: %d, now: %d)", (Object)claims.notBefore, nowSeconds);
                return null;
            }
            if (claims.issuedAt != null && claims.issuedAt > nowSeconds + 300L) {
                LOGGER.at(Level.WARNING).log("Token issued in the future (iat: %d, now: %d)", (Object)claims.issuedAt, nowSeconds);
                return null;
            }
            if (!CertificateUtil.validateCertificateBinding(claims.certificateFingerprint, clientCert)) {
                LOGGER.at(Level.WARNING).log("Certificate binding validation failed");
                return null;
            }
            LOGGER.at(Level.INFO).log("JWT validated successfully for user %s (UUID: %s)", (Object)claims.username, (Object)claims.subject);
            return claims;
        }
        catch (ParseException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse JWT");
            return null;
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("JWT validation error");
            return null;
        }
    }

    private boolean verifySignature(SignedJWT signedJWT, JWKSet jwkSet) {
        try {
            String keyId = signedJWT.getHeader().getKeyID();
            OctetKeyPair ed25519Key = null;
            for (JWK jwk : jwkSet.getKeys()) {
                if (!(jwk instanceof OctetKeyPair)) continue;
                OctetKeyPair okp = (OctetKeyPair)jwk;
                if (keyId != null && !keyId.equals(jwk.getKeyID())) continue;
                ed25519Key = okp;
                break;
            }
            if (ed25519Key == null) {
                LOGGER.at(Level.WARNING).log("No Ed25519 key found for kid=%s", keyId);
                return false;
            }
            Ed25519Verifier verifier = new Ed25519Verifier(ed25519Key);
            boolean valid = signedJWT.verify(verifier);
            if (valid) {
                LOGGER.at(Level.FINE).log("JWT signature verified with key kid=%s", keyId);
            }
            return valid;
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("JWT signature verification failed");
            return false;
        }
    }

    @Nullable
    private JWKSet getJwkSet() {
        return this.getJwkSet(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private JWKSet getJwkSet(boolean forceRefresh) {
        block11: {
            long now = System.currentTimeMillis();
            if (!forceRefresh && this.cachedJwkSet != null && now < this.jwksCacheExpiry) {
                return this.cachedJwkSet;
            }
            this.jwksFetchLock.lock();
            if (!forceRefresh && this.cachedJwkSet != null && now < this.jwksCacheExpiry) {
                JWKSet jWKSet = this.cachedJwkSet;
                return jWKSet;
            }
            CompletableFuture<JWKSet> existing = this.pendingFetch;
            if (existing != null && !existing.isDone()) {
                this.jwksFetchLock.unlock();
                try {
                    JWKSet jWKSet = existing.join();
                    return jWKSet;
                }
                finally {
                    this.jwksFetchLock.lock();
                }
            }
            if (!forceRefresh) break block11;
            LOGGER.at(Level.INFO).log("Force refreshing JWKS cache (key rotation or verification failure)");
        }
        this.pendingFetch = CompletableFuture.supplyAsync(this::fetchJwksFromService);
        return this.pendingFetch.join();
        finally {
            this.jwksFetchLock.unlock();
        }
    }

    @Nullable
    private JWKSet fetchJwksFromService() {
        SessionServiceClient.JwksResponse jwksResponse = this.sessionServiceClient.getJwks();
        if (jwksResponse == null || jwksResponse.keys == null || jwksResponse.keys.length == 0) {
            LOGGER.at(Level.WARNING).log("Failed to fetch JWKS or no keys available");
            return this.cachedJwkSet;
        }
        try {
            JWKSet newSet;
            ArrayList<JWK> jwkList = new ArrayList<JWK>();
            for (SessionServiceClient.JwkKey key : jwksResponse.keys) {
                JWK jwk = this.convertToJWK(key);
                if (jwk == null) continue;
                jwkList.add(jwk);
            }
            if (jwkList.isEmpty()) {
                LOGGER.at(Level.WARNING).log("No valid JWKs found in JWKS response");
                return this.cachedJwkSet;
            }
            this.cachedJwkSet = newSet = new JWKSet(jwkList);
            this.jwksCacheExpiry = System.currentTimeMillis() + this.jwksCacheDurationMs;
            LOGGER.at(Level.INFO).log("JWKS loaded with %d keys", jwkList.size());
            return newSet;
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse JWKS");
            return this.cachedJwkSet;
        }
    }

    private boolean verifySignatureWithRetry(SignedJWT signedJWT) {
        JWKSet jwkSet = this.getJwkSet();
        if (jwkSet == null) {
            return false;
        }
        if (this.verifySignature(signedJWT, jwkSet)) {
            return true;
        }
        LOGGER.at(Level.INFO).log("Signature verification failed with cached JWKS, retrying with fresh keys");
        JWKSet freshJwkSet = this.getJwkSet(true);
        if (freshJwkSet == null || freshJwkSet == jwkSet) {
            return false;
        }
        return this.verifySignature(signedJWT, freshJwkSet);
    }

    @Nullable
    private JWK convertToJWK(SessionServiceClient.JwkKey key) {
        if (!"OKP".equals(key.kty)) {
            LOGGER.at(Level.WARNING).log("Unsupported key type: %s (expected OKP)", key.kty);
            return null;
        }
        try {
            String json = String.format("{\"kty\":\"OKP\",\"crv\":\"%s\",\"x\":\"%s\",\"kid\":\"%s\",\"alg\":\"EdDSA\"}", key.crv, key.x, key.kid);
            return JWK.parse(json);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse Ed25519 key");
            return null;
        }
    }

    public void invalidateJwksCache() {
        this.jwksFetchLock.lock();
        try {
            this.cachedJwkSet = null;
            this.jwksCacheExpiry = 0L;
            this.pendingFetch = null;
        }
        finally {
            this.jwksFetchLock.unlock();
        }
    }

    @Nullable
    public IdentityTokenClaims validateIdentityToken(@Nonnull String identityToken) {
        if (identityToken.isEmpty()) {
            LOGGER.at(Level.WARNING).log("Identity token is empty");
            return null;
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(identityToken);
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
                LOGGER.at(Level.WARNING).log("Unsupported identity token algorithm: %s (expected EdDSA)", algorithm);
                return null;
            }
            if (!this.verifySignatureWithRetry(signedJWT)) {
                LOGGER.at(Level.WARNING).log("Identity token signature verification failed");
                return null;
            }
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            IdentityTokenClaims claims = new IdentityTokenClaims();
            claims.issuer = claimsSet.getIssuer();
            claims.subject = claimsSet.getSubject();
            claims.username = claimsSet.getStringClaim("username");
            claims.issuedAt = claimsSet.getIssueTime() != null ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
            claims.expiresAt = claimsSet.getExpirationTime() != null ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
            claims.notBefore = claimsSet.getNotBeforeTime() != null ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
            claims.scope = claimsSet.getStringClaim("scope");
            if (!this.expectedIssuer.equals(claims.issuer)) {
                LOGGER.at(Level.WARNING).log("Invalid identity token issuer: expected %s, got %s", (Object)this.expectedIssuer, (Object)claims.issuer);
                return null;
            }
            long nowSeconds = Instant.now().getEpochSecond();
            if (claims.expiresAt == null) {
                LOGGER.at(Level.WARNING).log("Identity token missing expiration claim");
                return null;
            }
            if (nowSeconds >= claims.expiresAt + 300L) {
                LOGGER.at(Level.WARNING).log("Identity token expired (exp: %d, now: %d)", (Object)claims.expiresAt, nowSeconds);
                return null;
            }
            if (claims.notBefore != null && nowSeconds < claims.notBefore - 300L) {
                LOGGER.at(Level.WARNING).log("Identity token not yet valid (nbf: %d, now: %d)", (Object)claims.notBefore, nowSeconds);
                return null;
            }
            if (claims.issuedAt != null && claims.issuedAt > nowSeconds + 300L) {
                LOGGER.at(Level.WARNING).log("Identity token issued in the future (iat: %d, now: %d)", (Object)claims.issuedAt, nowSeconds);
                return null;
            }
            if (claims.getSubjectAsUUID() == null) {
                LOGGER.at(Level.WARNING).log("Identity token has invalid or missing subject UUID");
                return null;
            }
            LOGGER.at(Level.INFO).log("Identity token validated successfully for user %s (UUID: %s)", (Object)claims.username, (Object)claims.subject);
            return claims;
        }
        catch (ParseException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse identity token");
            return null;
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Identity token validation error");
            return null;
        }
    }

    @Nullable
    public SessionTokenClaims validateSessionToken(@Nonnull String sessionToken) {
        if (sessionToken.isEmpty()) {
            LOGGER.at(Level.WARNING).log("Session token is empty");
            return null;
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(sessionToken);
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
                LOGGER.at(Level.WARNING).log("Unsupported session token algorithm: %s (expected EdDSA)", algorithm);
                return null;
            }
            if (!this.verifySignatureWithRetry(signedJWT)) {
                LOGGER.at(Level.WARNING).log("Session token signature verification failed");
                return null;
            }
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            SessionTokenClaims claims = new SessionTokenClaims();
            claims.issuer = claimsSet.getIssuer();
            claims.subject = claimsSet.getSubject();
            claims.issuedAt = claimsSet.getIssueTime() != null ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
            claims.expiresAt = claimsSet.getExpirationTime() != null ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
            Long l = claims.notBefore = claimsSet.getNotBeforeTime() != null ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
            if (!this.expectedIssuer.equals(claims.issuer)) {
                LOGGER.at(Level.WARNING).log("Invalid session token issuer: expected %s, got %s", (Object)this.expectedIssuer, (Object)claims.issuer);
                return null;
            }
            long nowSeconds = Instant.now().getEpochSecond();
            if (claims.expiresAt == null) {
                LOGGER.at(Level.WARNING).log("Session token missing expiration claim");
                return null;
            }
            if (nowSeconds >= claims.expiresAt + 300L) {
                LOGGER.at(Level.WARNING).log("Session token expired (exp: %d, now: %d)", (Object)claims.expiresAt, nowSeconds);
                return null;
            }
            if (claims.notBefore != null && nowSeconds < claims.notBefore - 300L) {
                LOGGER.at(Level.WARNING).log("Session token not yet valid (nbf: %d, now: %d)", (Object)claims.notBefore, nowSeconds);
                return null;
            }
            if (claims.issuedAt != null && claims.issuedAt > nowSeconds + 300L) {
                LOGGER.at(Level.WARNING).log("Session token issued in the future (iat: %d, now: %d)", (Object)claims.issuedAt, nowSeconds);
                return null;
            }
            LOGGER.at(Level.INFO).log("Session token validated successfully");
            return claims;
        }
        catch (ParseException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse session token");
            return null;
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Session token validation error");
            return null;
        }
    }

    public static class JWTClaims {
        public String issuer;
        public String audience;
        public String subject;
        public String username;
        public String ipAddress;
        public Long issuedAt;
        public Long expiresAt;
        public Long notBefore;
        public String certificateFingerprint;

        @Nullable
        public UUID getSubjectAsUUID() {
            if (this.subject == null) {
                return null;
            }
            try {
                return UUID.fromString(this.subject);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static class IdentityTokenClaims {
        public String issuer;
        public String subject;
        public String username;
        public Long issuedAt;
        public Long expiresAt;
        public Long notBefore;
        public String scope;

        @Nullable
        public UUID getSubjectAsUUID() {
            if (this.subject == null) {
                return null;
            }
            try {
                return UUID.fromString(this.subject);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Nonnull
        public String[] getScopes() {
            if (this.scope == null || this.scope.isEmpty()) {
                return new String[0];
            }
            return this.scope.split(" ");
        }

        public boolean hasScope(@Nonnull String targetScope) {
            for (String s : this.getScopes()) {
                if (!s.equals(targetScope)) continue;
                return true;
            }
            return false;
        }
    }

    public static class SessionTokenClaims {
        public String issuer;
        public String subject;
        public Long issuedAt;
        public Long expiresAt;
        public Long notBefore;
    }
}

