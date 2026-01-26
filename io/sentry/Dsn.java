/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.util.Objects;
import java.net.URI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class Dsn {
    @NotNull
    private final String projectId;
    @Nullable
    private final String path;
    @Nullable
    private final String secretKey;
    @NotNull
    private final String publicKey;
    @NotNull
    private final URI sentryUri;

    @NotNull
    public String getProjectId() {
        return this.projectId;
    }

    @Nullable
    public String getPath() {
        return this.path;
    }

    @Nullable
    public String getSecretKey() {
        return this.secretKey;
    }

    @NotNull
    public String getPublicKey() {
        return this.publicKey;
    }

    @NotNull
    URI getSentryUri() {
        return this.sentryUri;
    }

    Dsn(@Nullable String dsn) throws IllegalArgumentException {
        try {
            int projectIdStart;
            String path;
            Objects.requireNonNull(dsn, "The DSN is required.");
            URI uri = new URI(dsn).normalize();
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new IllegalArgumentException("Invalid DSN scheme: " + scheme);
            }
            String userInfo = uri.getUserInfo();
            if (userInfo == null || userInfo.isEmpty()) {
                throw new IllegalArgumentException("Invalid DSN: No public key provided.");
            }
            String[] keys = userInfo.split(":", -1);
            this.publicKey = keys[0];
            if (this.publicKey == null || this.publicKey.isEmpty()) {
                throw new IllegalArgumentException("Invalid DSN: No public key provided.");
            }
            this.secretKey = keys.length > 1 ? keys[1] : null;
            String uriPath = uri.getPath();
            if (uriPath.endsWith("/")) {
                uriPath = uriPath.substring(0, uriPath.length() - 1);
            }
            if (!(path = uriPath.substring(0, projectIdStart = uriPath.lastIndexOf("/") + 1)).endsWith("/")) {
                path = path + "/";
            }
            this.path = path;
            this.projectId = uriPath.substring(projectIdStart);
            if (this.projectId.isEmpty()) {
                throw new IllegalArgumentException("Invalid DSN: A Project Id is required.");
            }
            this.sentryUri = new URI(scheme, null, uri.getHost(), uri.getPort(), path + "api/" + this.projectId, null, null);
        }
        catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }
}

