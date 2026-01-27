/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.auth.AuthConfig;
import com.hypixel.hytale.server.core.util.ServiceHttpClientFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProfileServiceClient {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final HttpClient httpClient;
    private final String profileServiceUrl;

    public ProfileServiceClient(@Nonnull String profileServiceUrl) {
        if (profileServiceUrl == null || profileServiceUrl.isEmpty()) {
            throw new IllegalArgumentException("Profile Service URL cannot be null or empty");
        }
        this.profileServiceUrl = profileServiceUrl.endsWith("/") ? profileServiceUrl.substring(0, profileServiceUrl.length() - 1) : profileServiceUrl;
        this.httpClient = ServiceHttpClientFactory.create(AuthConfig.HTTP_TIMEOUT);
        LOGGER.at(Level.INFO).log("Profile Service client initialized for: %s", this.profileServiceUrl);
    }

    @Nullable
    public PublicGameProfile getProfileByUuid(@Nonnull UUID uuid, @Nonnull String bearerToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.profileServiceUrl + "/profile/uuid/" + uuid.toString())).header("Accept", "application/json").header("Authorization", "Bearer " + bearerToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).GET().build();
            LOGGER.at(Level.FINE).log("Fetching profile by UUID: %s", uuid);
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.at(Level.WARNING).log("Failed to fetch profile by UUID: HTTP %d - %s", response.statusCode(), (Object)response.body());
                return null;
            }
            PublicGameProfile profile = PublicGameProfile.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
            if (profile == null) {
                LOGGER.at(Level.WARNING).log("Profile Service returned invalid response for UUID: %s", uuid);
                return null;
            }
            LOGGER.at(Level.FINE).log("Successfully fetched profile: %s (%s)", (Object)profile.getUsername(), (Object)profile.getUuid());
            return profile;
        }
        catch (IOException e) {
            LOGGER.at(Level.WARNING).log("IO error while fetching profile by UUID: %s", e.getMessage());
            return null;
        }
        catch (InterruptedException e) {
            LOGGER.at(Level.WARNING).log("Request interrupted while fetching profile by UUID");
            Thread.currentThread().interrupt();
            return null;
        }
        catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Unexpected error fetching profile by UUID: %s", e.getMessage());
            return null;
        }
    }

    public CompletableFuture<PublicGameProfile> getProfileByUuidAsync(@Nonnull UUID uuid, @Nonnull String bearerToken) {
        return CompletableFuture.supplyAsync(() -> this.getProfileByUuid(uuid, bearerToken));
    }

    @Nullable
    public PublicGameProfile getProfileByUsername(@Nonnull String username, @Nonnull String bearerToken) {
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.profileServiceUrl + "/profile/username/" + encodedUsername)).header("Accept", "application/json").header("Authorization", "Bearer " + bearerToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(AuthConfig.HTTP_TIMEOUT).GET().build();
            LOGGER.at(Level.FINE).log("Fetching profile by username: %s", username);
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.at(Level.WARNING).log("Failed to fetch profile by username: HTTP %d - %s", response.statusCode(), (Object)response.body());
                return null;
            }
            PublicGameProfile profile = PublicGameProfile.CODEC.decodeJson(new RawJsonReader(response.body().toCharArray()), EmptyExtraInfo.EMPTY);
            if (profile == null) {
                LOGGER.at(Level.WARNING).log("Profile Service returned invalid response for username: %s", username);
                return null;
            }
            LOGGER.at(Level.FINE).log("Successfully fetched profile: %s (%s)", (Object)profile.getUsername(), (Object)profile.getUuid());
            return profile;
        }
        catch (IOException e) {
            LOGGER.at(Level.WARNING).log("IO error while fetching profile by username: %s", e.getMessage());
            return null;
        }
        catch (InterruptedException e) {
            LOGGER.at(Level.WARNING).log("Request interrupted while fetching profile by username");
            Thread.currentThread().interrupt();
            return null;
        }
        catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Unexpected error fetching profile by username: %s", e.getMessage());
            return null;
        }
    }

    public CompletableFuture<PublicGameProfile> getProfileByUsernameAsync(@Nonnull String username, @Nonnull String bearerToken) {
        return CompletableFuture.supplyAsync(() -> this.getProfileByUsername(username, bearerToken));
    }

    private static <T> KeyedCodec<T> externalKey(String key, Codec<T> codec) {
        return new KeyedCodec<T>(key, codec, false, true);
    }

    public static class PublicGameProfile {
        public static final BuilderCodec<PublicGameProfile> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PublicGameProfile.class, PublicGameProfile::new).append(ProfileServiceClient.externalKey("uuid", Codec.UUID_STRING), (p, v) -> {
            p.uuid = v;
        }, PublicGameProfile::getUuid).add()).append(ProfileServiceClient.externalKey("username", Codec.STRING), (p, v) -> {
            p.username = v;
        }, PublicGameProfile::getUsername).add()).build();
        private UUID uuid;
        private String username;

        public PublicGameProfile() {
        }

        public PublicGameProfile(UUID uuid, String username) {
            this.uuid = uuid;
            this.username = username;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getUsername() {
            return this.username;
        }
    }
}

