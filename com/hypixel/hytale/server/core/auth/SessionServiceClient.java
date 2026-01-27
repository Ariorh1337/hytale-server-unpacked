/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.auth.AuthConfig;
import com.hypixel.hytale.server.core.util.ServiceHttpClientFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SessionServiceClient {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final ExecutorService HTTP_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private final HttpClient httpClient;
    private final String sessionServiceUrl;

    public SessionServiceClient(@Nonnull String sessionServiceUrl) {
        if (sessionServiceUrl == null || sessionServiceUrl.isEmpty()) {
            throw new IllegalArgumentException("Session Service URL cannot be null or empty");
        }
        this.sessionServiceUrl = sessionServiceUrl.endsWith("/") ? sessionServiceUrl.substring(0, sessionServiceUrl.length() - 1) : sessionServiceUrl;
        this.httpClient = ServiceHttpClientFactory.create(AuthConfig.HTTP_TIMEOUT);
        LOGGER.at(Level.INFO).log("Session Service client initialized for: %s", this.sessionServiceUrl);
    }

    public CompletableFuture<String> requestAuthorizationGrantAsync(@Nonnull String identityToken, @Nonnull String serverAudience, @Nonnull String bearerToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = String.format("{\"identityToken\":\"%s\",\"aud\":\"%s\"}", SessionServiceClient.escapeJsonString(identityToken), SessionServiceClient.escapeJsonString(serverAudience));
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.sessionServiceUrl + "/server-join/auth-grant")).header("Content-Type", "application/json").header("Accept", "application/json").header("Authorization", "Bearer " + bearerToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
                LOGGER.at(Level.INFO).log("Requesting authorization grant with identity token, aud='%s'", serverAudience);
                HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    LOGGER.at(Level.WARNING).log("Failed to request authorization grant: HTTP %d - %s", response.statusCode(), (Object)response.body());
                    return null;
                }
                AuthGrantResponse authGrantResponse = AuthGrantResponse.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
                if (authGrantResponse == null || authGrantResponse.authorizationGrant == null) {
                    LOGGER.at(Level.WARNING).log("Session Service response missing authorizationGrant field");
                    return null;
                }
                LOGGER.at(Level.INFO).log("Successfully obtained authorization grant");
                return authGrantResponse.authorizationGrant;
            }
            catch (IOException e) {
                LOGGER.at(Level.WARNING).log("IO error while requesting authorization grant: %s", e.getMessage());
                return null;
            }
            catch (InterruptedException e) {
                LOGGER.at(Level.WARNING).log("Request interrupted while obtaining authorization grant");
                Thread.currentThread().interrupt();
                return null;
            }
            catch (Exception e) {
                LOGGER.at(Level.WARNING).log("Unexpected error requesting authorization grant: %s", e.getMessage());
                return null;
            }
        }, HTTP_EXECUTOR);
    }

    public CompletableFuture<String> exchangeAuthGrantForTokenAsync(@Nonnull String authorizationGrant, @Nonnull String x509Fingerprint, @Nonnull String bearerToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = String.format("{\"authorizationGrant\":\"%s\",\"x509Fingerprint\":\"%s\"}", SessionServiceClient.escapeJsonString(authorizationGrant), SessionServiceClient.escapeJsonString(x509Fingerprint));
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.sessionServiceUrl + "/server-join/auth-token")).header("Content-Type", "application/json").header("Accept", "application/json").header("Authorization", "Bearer " + bearerToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
                LOGGER.at(Level.INFO).log("Exchanging authorization grant for access token");
                LOGGER.at(Level.FINE).log("Using bearer token (first 20 chars): %s...", bearerToken.length() > 20 ? bearerToken.substring(0, 20) : bearerToken);
                LOGGER.at(Level.FINE).log("Request body: %s", jsonBody);
                HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    LOGGER.at(Level.WARNING).log("Failed to exchange auth grant: HTTP %d - %s", response.statusCode(), (Object)response.body());
                    return null;
                }
                AccessTokenResponse tokenResponse = AccessTokenResponse.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
                if (tokenResponse == null || tokenResponse.accessToken == null) {
                    LOGGER.at(Level.WARNING).log("Session Service response missing accessToken field");
                    return null;
                }
                LOGGER.at(Level.INFO).log("Successfully obtained access token");
                return tokenResponse.accessToken;
            }
            catch (IOException e) {
                LOGGER.at(Level.WARNING).log("IO error while exchanging auth grant: %s", e.getMessage());
                return null;
            }
            catch (InterruptedException e) {
                LOGGER.at(Level.WARNING).log("Request interrupted while exchanging auth grant");
                Thread.currentThread().interrupt();
                return null;
            }
            catch (Exception e) {
                LOGGER.at(Level.WARNING).log("Unexpected error exchanging auth grant: %s", e.getMessage());
                return null;
            }
        }, HTTP_EXECUTOR);
    }

    @Nullable
    public JwksResponse getJwks() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.sessionServiceUrl + "/.well-known/jwks.json")).header("Accept", "application/json").header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).GET().build();
            LOGGER.at(Level.FINE).log("Fetching JWKS from Session Service");
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.at(Level.WARNING).log("Failed to fetch JWKS: HTTP %d - %s", response.statusCode(), (Object)response.body());
                return null;
            }
            JwksResponse jwks = JwksResponse.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
            if (jwks == null || jwks.keys == null || jwks.keys.length == 0) {
                LOGGER.at(Level.WARNING).log("Session Service returned invalid JWKS (no keys)");
                return null;
            }
            LOGGER.at(Level.INFO).log("Successfully fetched JWKS with %d keys", jwks.keys.length);
            return jwks;
        }
        catch (IOException e) {
            LOGGER.at(Level.WARNING).log("IO error while fetching JWKS: %s", e.getMessage());
            return null;
        }
        catch (InterruptedException e) {
            LOGGER.at(Level.WARNING).log("Request interrupted while fetching JWKS");
            Thread.currentThread().interrupt();
            return null;
        }
        catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Unexpected error fetching JWKS: %s", e.getMessage());
            return null;
        }
    }

    @Nullable
    public GameProfile[] getGameProfiles(@Nonnull String oauthAccessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://account-data.hytale.com/my-account/get-profiles")).header("Accept", "application/json").header("Authorization", "Bearer " + oauthAccessToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).GET().build();
            LOGGER.at(Level.INFO).log("Fetching game profiles...");
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.at(Level.WARNING).log("Failed to fetch profiles: HTTP %d - %s", response.statusCode(), (Object)response.body());
                return null;
            }
            LauncherDataResponse data = LauncherDataResponse.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
            if (data == null || data.profiles == null) {
                LOGGER.at(Level.WARNING).log("Account Data returned invalid response");
                return null;
            }
            LOGGER.at(Level.INFO).log("Found %d game profile(s)", data.profiles.length);
            return data.profiles;
        }
        catch (IOException e) {
            LOGGER.at(Level.WARNING).log("IO error while fetching profiles: %s", e.getMessage());
            return null;
        }
        catch (InterruptedException e) {
            LOGGER.at(Level.WARNING).log("Request interrupted while fetching profiles");
            Thread.currentThread().interrupt();
            return null;
        }
        catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Unexpected error fetching profiles: %s", e.getMessage());
            return null;
        }
    }

    public GameSessionResponse createGameSession(@Nonnull String oauthAccessToken, @Nonnull UUID profileUuid) {
        try {
            String body = String.format("{\"uuid\":\"%s\"}", profileUuid.toString());
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.sessionServiceUrl + "/game-session/new")).header("Content-Type", "application/json").header("Authorization", "Bearer " + oauthAccessToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).POST(HttpRequest.BodyPublishers.ofString(body)).build();
            LOGGER.at(Level.INFO).log("Creating game session...");
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                LOGGER.at(Level.WARNING).log("Failed to create game session: HTTP %d - %s", response.statusCode(), (Object)response.body());
                return null;
            }
            GameSessionResponse sessionResponse = GameSessionResponse.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
            if (sessionResponse == null || sessionResponse.identityToken == null) {
                LOGGER.at(Level.WARNING).log("Session Service returned invalid response");
                return null;
            }
            LOGGER.at(Level.INFO).log("Successfully created game session");
            return sessionResponse;
        }
        catch (IOException e) {
            LOGGER.at(Level.WARNING).log("IO error while creating session: %s", e.getMessage());
            return null;
        }
        catch (InterruptedException e) {
            LOGGER.at(Level.WARNING).log("Request interrupted while creating session");
            Thread.currentThread().interrupt();
            return null;
        }
        catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Unexpected error creating session: %s", e.getMessage());
            return null;
        }
    }

    public CompletableFuture<GameSessionResponse> refreshSessionAsync(@Nonnull String sessionToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.sessionServiceUrl + "/game-session/refresh")).header("Accept", "application/json").header("Authorization", "Bearer " + sessionToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).POST(HttpRequest.BodyPublishers.noBody()).build();
                LOGGER.at(Level.INFO).log("Refreshing game session...");
                HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    LOGGER.at(Level.WARNING).log("Failed to refresh session: HTTP %d - %s", response.statusCode(), (Object)response.body());
                    return null;
                }
                GameSessionResponse sessionResponse = GameSessionResponse.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
                if (sessionResponse == null || sessionResponse.identityToken == null) {
                    LOGGER.at(Level.WARNING).log("Session Service returned invalid response (missing identity token)");
                    return null;
                }
                LOGGER.at(Level.INFO).log("Successfully refreshed game session");
                return sessionResponse;
            }
            catch (IOException e) {
                LOGGER.at(Level.WARNING).log("IO error while refreshing session: %s", e.getMessage());
                return null;
            }
            catch (InterruptedException e) {
                LOGGER.at(Level.WARNING).log("Request interrupted while refreshing session");
                Thread.currentThread().interrupt();
                return null;
            }
            catch (Exception e) {
                LOGGER.at(Level.WARNING).log("Unexpected error refreshing session: %s", e.getMessage());
                return null;
            }
        }, HTTP_EXECUTOR);
    }

    public void terminateSession(@Nonnull String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.sessionServiceUrl + "/game-session")).header("Authorization", "Bearer " + sessionToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).DELETE().build();
            LOGGER.at(Level.INFO).log("Terminating game session...");
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                LOGGER.at(Level.INFO).log("Game session terminated");
            } else {
                LOGGER.at(Level.WARNING).log("Failed to terminate session: HTTP %d - %s", response.statusCode(), (Object)response.body());
            }
        }
        catch (IOException e) {
            LOGGER.at(Level.WARNING).log("IO error while terminating session: %s", e.getMessage());
        }
        catch (InterruptedException e) {
            LOGGER.at(Level.WARNING).log("Request interrupted while terminating session");
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Error terminating session: %s", e.getMessage());
        }
    }

    private static String escapeJsonString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static <T> KeyedCodec<T> externalKey(String key, Codec<T> codec) {
        return new KeyedCodec<T>(key, codec, false, true);
    }

    public static class JwksResponse {
        public JwkKey[] keys;
        public static final BuilderCodec<JwksResponse> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(JwksResponse.class, JwksResponse::new).append(SessionServiceClient.externalKey("keys", new ArrayCodec<JwkKey>(JwkKey.CODEC, JwkKey[]::new)), (r, v) -> {
            r.keys = v;
        }, r -> r.keys).add()).build();
    }

    public static class JwkKey {
        public String kty;
        public String alg;
        public String use;
        public String kid;
        public String crv;
        public String x;
        public String y;
        public String n;
        public String e;
        public static final BuilderCodec<JwkKey> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(JwkKey.class, JwkKey::new).append(SessionServiceClient.externalKey("kty", Codec.STRING), (k, v) -> {
            k.kty = v;
        }, k -> k.kty).add()).append(SessionServiceClient.externalKey("alg", Codec.STRING), (k, v) -> {
            k.alg = v;
        }, k -> k.alg).add()).append(SessionServiceClient.externalKey("use", Codec.STRING), (k, v) -> {
            k.use = v;
        }, k -> k.use).add()).append(SessionServiceClient.externalKey("kid", Codec.STRING), (k, v) -> {
            k.kid = v;
        }, k -> k.kid).add()).append(SessionServiceClient.externalKey("crv", Codec.STRING), (k, v) -> {
            k.crv = v;
        }, k -> k.crv).add()).append(SessionServiceClient.externalKey("x", Codec.STRING), (k, v) -> {
            k.x = v;
        }, k -> k.x).add()).append(SessionServiceClient.externalKey("y", Codec.STRING), (k, v) -> {
            k.y = v;
        }, k -> k.y).add()).append(SessionServiceClient.externalKey("n", Codec.STRING), (k, v) -> {
            k.n = v;
        }, k -> k.n).add()).append(SessionServiceClient.externalKey("e", Codec.STRING), (k, v) -> {
            k.e = v;
        }, k -> k.e).add()).build();
    }

    public static class LauncherDataResponse {
        public UUID owner;
        public GameProfile[] profiles;
        public static final BuilderCodec<LauncherDataResponse> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LauncherDataResponse.class, LauncherDataResponse::new).append(SessionServiceClient.externalKey("owner", Codec.UUID_STRING), (r, v) -> {
            r.owner = v;
        }, r -> r.owner).add()).append(SessionServiceClient.externalKey("profiles", new ArrayCodec<GameProfile>(GameProfile.CODEC, GameProfile[]::new)), (r, v) -> {
            r.profiles = v;
        }, r -> r.profiles).add()).build();
    }

    public static class GameProfile {
        public UUID uuid;
        public String username;
        public static final BuilderCodec<GameProfile> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GameProfile.class, GameProfile::new).append(SessionServiceClient.externalKey("uuid", Codec.UUID_STRING), (p, v) -> {
            p.uuid = v;
        }, p -> p.uuid).add()).append(SessionServiceClient.externalKey("username", Codec.STRING), (p, v) -> {
            p.username = v;
        }, p -> p.username).add()).build();
    }

    public static class GameSessionResponse {
        public String sessionToken;
        public String identityToken;
        public String expiresAt;
        public static final BuilderCodec<GameSessionResponse> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GameSessionResponse.class, GameSessionResponse::new).append(SessionServiceClient.externalKey("sessionToken", Codec.STRING), (r, v) -> {
            r.sessionToken = v;
        }, r -> r.sessionToken).add()).append(SessionServiceClient.externalKey("identityToken", Codec.STRING), (r, v) -> {
            r.identityToken = v;
        }, r -> r.identityToken).add()).append(SessionServiceClient.externalKey("expiresAt", Codec.STRING), (r, v) -> {
            r.expiresAt = v;
        }, r -> r.expiresAt).add()).build();

        public Instant getExpiresAtInstant() {
            if (this.expiresAt == null) {
                return null;
            }
            try {
                return Instant.parse(this.expiresAt);
            }
            catch (Exception e) {
                return null;
            }
        }
    }

    public static class AccessTokenResponse {
        public String accessToken;
        public static final BuilderCodec<AccessTokenResponse> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(AccessTokenResponse.class, AccessTokenResponse::new).append(SessionServiceClient.externalKey("accessToken", Codec.STRING), (r, v) -> {
            r.accessToken = v;
        }, r -> r.accessToken).add()).build();
    }

    public static class AuthGrantResponse {
        public String authorizationGrant;
        public static final BuilderCodec<AuthGrantResponse> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(AuthGrantResponse.class, AuthGrantResponse::new).append(SessionServiceClient.externalKey("authorizationGrant", Codec.STRING), (r, v) -> {
            r.authorizationGrant = v;
        }, r -> r.authorizationGrant).add()).build();
    }
}

