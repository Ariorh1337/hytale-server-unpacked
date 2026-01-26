/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.HardwareUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.auth.IAuthCredentialStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bson.BsonDocument;

public class EncryptedAuthCredentialStore
implements IAuthCredentialStore {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int KEY_LENGTH = 256;
    private static final int PBKDF2_ITERATIONS = 100000;
    private static final byte[] SALT = "HytaleAuthCredentialStore".getBytes(StandardCharsets.UTF_8);
    private static final BuilderCodec<StoredCredentials> CREDENTIALS_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StoredCredentials.class, StoredCredentials::new).append(new KeyedCodec<String>("AccessToken", Codec.STRING), (o, v) -> {
        o.accessToken = v;
    }, o -> o.accessToken).add()).append(new KeyedCodec<String>("RefreshToken", Codec.STRING), (o, v) -> {
        o.refreshToken = v;
    }, o -> o.refreshToken).add()).append(new KeyedCodec("ExpiresAt", Codec.INSTANT), (o, v) -> {
        o.expiresAt = v;
    }, o -> o.expiresAt).add()).append(new KeyedCodec("ProfileUuid", Codec.UUID_STRING), (o, v) -> {
        o.profileUuid = v;
    }, o -> o.profileUuid).add()).build();
    private final Path path;
    @Nullable
    private final SecretKey encryptionKey;
    private IAuthCredentialStore.OAuthTokens tokens = new IAuthCredentialStore.OAuthTokens(null, null, null);
    @Nullable
    private UUID profile;

    public EncryptedAuthCredentialStore(@Nonnull Path path) {
        this.path = path;
        this.encryptionKey = EncryptedAuthCredentialStore.deriveKey();
        if (this.encryptionKey == null) {
            LOGGER.at(Level.WARNING).log("Cannot derive encryption key - encrypted storage will not persist credentials");
        } else {
            this.load();
        }
    }

    @Nullable
    private static SecretKey deriveKey() {
        UUID hardwareId = HardwareUtil.getUUID();
        if (hardwareId == null) {
            return null;
        }
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(hardwareId.toString().toCharArray(), SALT, 100000, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to derive encryption key");
            return null;
        }
    }

    private void load() {
        if (this.encryptionKey == null || !Files.exists(this.path, new LinkOption[0])) {
            return;
        }
        try {
            byte[] encrypted = Files.readAllBytes(this.path);
            byte[] decrypted = this.decrypt(encrypted);
            if (decrypted == null) {
                LOGGER.at(Level.WARNING).log("Failed to decrypt credentials from %s - file may be corrupted or from different hardware", this.path);
                return;
            }
            BsonDocument doc = BsonUtil.readFromBytes(decrypted);
            if (doc == null) {
                LOGGER.at(Level.WARNING).log("Failed to parse credentials from %s", this.path);
                return;
            }
            StoredCredentials stored = (StoredCredentials)CREDENTIALS_CODEC.decode(doc);
            if (stored != null) {
                this.tokens = new IAuthCredentialStore.OAuthTokens(stored.accessToken, stored.refreshToken, stored.expiresAt);
                this.profile = stored.profileUuid;
            }
            LOGGER.at(Level.INFO).log("Loaded encrypted credentials from %s", this.path);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to load encrypted credentials from %s", this.path);
        }
    }

    private void save() {
        if (this.encryptionKey == null) {
            LOGGER.at(Level.WARNING).log("Cannot save credentials - no encryption key available");
            return;
        }
        try {
            StoredCredentials stored = new StoredCredentials();
            stored.accessToken = this.tokens.accessToken();
            stored.refreshToken = this.tokens.refreshToken();
            stored.expiresAt = this.tokens.accessTokenExpiresAt();
            stored.profileUuid = this.profile;
            BsonDocument doc = (BsonDocument)CREDENTIALS_CODEC.encode(stored);
            byte[] plaintext = BsonUtil.writeToBytes(doc);
            byte[] encrypted = this.encrypt(plaintext);
            if (encrypted == null) {
                LOGGER.at(Level.SEVERE).log("Failed to encrypt credentials");
                return;
            }
            Files.write(this.path, encrypted, new OpenOption[0]);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to save encrypted credentials to %s", this.path);
        }
    }

    @Nullable
    private byte[] encrypt(@Nonnull byte[] plaintext) {
        if (this.encryptionKey == null) {
            return null;
        }
        try {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, (Key)this.encryptionKey, new GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(plaintext);
            ByteBuffer result = ByteBuffer.allocate(iv.length + ciphertext.length);
            result.put(iv);
            result.put(ciphertext);
            return result.array();
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Encryption failed");
            return null;
        }
    }

    @Nullable
    private byte[] decrypt(@Nonnull byte[] encrypted) {
        if (this.encryptionKey == null || encrypted.length < 12) {
            return null;
        }
        try {
            ByteBuffer buffer = ByteBuffer.wrap(encrypted);
            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, (Key)this.encryptionKey, new GCMParameterSpec(128, iv));
            return cipher.doFinal(ciphertext);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Decryption failed");
            return null;
        }
    }

    @Override
    public void setTokens(@Nonnull IAuthCredentialStore.OAuthTokens tokens) {
        this.tokens = tokens;
        this.save();
    }

    @Override
    @Nonnull
    public IAuthCredentialStore.OAuthTokens getTokens() {
        return this.tokens;
    }

    @Override
    public void setProfile(@Nullable UUID uuid) {
        this.profile = uuid;
        this.save();
    }

    @Override
    @Nullable
    public UUID getProfile() {
        return this.profile;
    }

    @Override
    public void clear() {
        this.tokens = new IAuthCredentialStore.OAuthTokens(null, null, null);
        this.profile = null;
        try {
            Files.deleteIfExists(this.path);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to delete encrypted credentials file %s", this.path);
        }
    }

    private static class StoredCredentials {
        @Nullable
        String accessToken;
        @Nullable
        String refreshToken;
        @Nullable
        Instant expiresAt;
        @Nullable
        UUID profileUuid;

        private StoredCredentials() {
        }
    }
}

