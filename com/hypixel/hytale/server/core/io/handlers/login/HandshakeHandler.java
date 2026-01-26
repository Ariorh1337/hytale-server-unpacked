/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.handlers.login;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.HostAddress;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.auth.AuthGrant;
import com.hypixel.hytale.protocol.packets.auth.AuthToken;
import com.hypixel.hytale.protocol.packets.auth.ServerAuthToken;
import com.hypixel.hytale.protocol.packets.connection.ClientType;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.auth.AuthConfig;
import com.hypixel.hytale.server.core.auth.JWTValidator;
import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.auth.SessionServiceClient;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import com.hypixel.hytale.server.core.io.handlers.GenericConnectionPacketHandler;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.io.transport.QUICTransport;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import io.netty.channel.Channel;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class HandshakeHandler
extends GenericConnectionPacketHandler {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static volatile SessionServiceClient sessionServiceClient;
    private static volatile JWTValidator jwtValidator;
    private volatile AuthState authState = AuthState.REQUESTING_AUTH_GRANT;
    private volatile boolean authTokenPacketReceived = false;
    private volatile String authenticatedUsername;
    private final ClientType clientType;
    private final String identityToken;
    private final UUID playerUuid;
    private final String username;
    private final byte[] referralData;
    private final HostAddress referralSource;

    public HandshakeHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, @Nonnull String language, @Nonnull ClientType clientType, @Nonnull String identityToken, @Nonnull UUID playerUuid, @Nonnull String username, @Nullable byte[] referralData, @Nullable HostAddress referralSource) {
        super(channel, protocolVersion, language);
        this.clientType = clientType;
        this.identityToken = identityToken;
        this.playerUuid = playerUuid;
        this.username = username;
        this.referralData = referralData;
        this.referralSource = referralSource;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static SessionServiceClient getSessionServiceClient() {
        if (sessionServiceClient != null) return sessionServiceClient;
        Class<HandshakeHandler> clazz = HandshakeHandler.class;
        synchronized (HandshakeHandler.class) {
            if (sessionServiceClient != null) return sessionServiceClient;
            sessionServiceClient = new SessionServiceClient("https://sessions.hytale.com");
            // ** MonitorExit[var0] (shouldn't be in output)
            return sessionServiceClient;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static JWTValidator getJwtValidator() {
        if (jwtValidator != null) return jwtValidator;
        Class<HandshakeHandler> clazz = HandshakeHandler.class;
        synchronized (HandshakeHandler.class) {
            if (jwtValidator != null) return jwtValidator;
            jwtValidator = new JWTValidator(HandshakeHandler.getSessionServiceClient(), "https://sessions.hytale.com", AuthConfig.getServerAudience());
            // ** MonitorExit[var0] (shouldn't be in output)
            return jwtValidator;
        }
    }

    @Override
    public void accept(@Nonnull Packet packet) {
        switch (packet.getId()) {
            case 1: {
                this.handle((Disconnect)packet);
                break;
            }
            case 12: {
                this.handle((AuthToken)packet);
                break;
            }
            default: {
                this.disconnect("Protocol error: unexpected packet " + packet.getId());
            }
        }
    }

    @Override
    public void registered0(PacketHandler oldHandler) {
        String requiredScope;
        HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
        this.enterStage("auth", timeouts.getAuth());
        JWTValidator.IdentityTokenClaims identityClaims = HandshakeHandler.getJwtValidator().validateIdentityToken(this.identityToken);
        if (identityClaims == null) {
            LOGGER.at(Level.WARNING).log("Identity token validation failed for %s from %s", (Object)this.username, (Object)NettyUtil.formatRemoteAddress(this.channel));
            this.disconnect("Invalid or expired identity token");
            return;
        }
        UUID tokenUuid = identityClaims.getSubjectAsUUID();
        if (tokenUuid == null || !tokenUuid.equals(this.playerUuid)) {
            LOGGER.at(Level.WARNING).log("Identity token UUID mismatch for %s from %s (expected: %s, got: %s)", this.username, NettyUtil.formatRemoteAddress(this.channel), this.playerUuid, tokenUuid);
            this.disconnect("Invalid identity token: UUID mismatch");
            return;
        }
        String string = requiredScope = this.clientType == ClientType.Editor ? "hytale:editor" : "hytale:client";
        if (!identityClaims.hasScope(requiredScope)) {
            LOGGER.at(Level.WARNING).log("Identity token missing required scope for %s from %s (clientType: %s, required: %s, actual: %s)", this.username, NettyUtil.formatRemoteAddress(this.channel), (Object)this.clientType, requiredScope, identityClaims.scope);
            this.disconnect("Invalid identity token: missing " + requiredScope + " scope");
            return;
        }
        LOGGER.at(Level.INFO).log("Identity token validated for %s (UUID: %s, scope: %s) from %s, requesting auth grant", this.username, this.playerUuid, identityClaims.scope, NettyUtil.formatRemoteAddress(this.channel));
        this.continueStage("auth:grant", timeouts.getAuthGrant(), () -> this.authState != AuthState.REQUESTING_AUTH_GRANT);
        this.requestAuthGrant();
    }

    private void requestAuthGrant() {
        String serverSessionToken = ServerAuthManager.getInstance().getSessionToken();
        if (serverSessionToken == null || serverSessionToken.isEmpty()) {
            LOGGER.at(Level.SEVERE).log("Server session token not available - cannot request auth grant");
            this.disconnect("Server authentication unavailable - please try again later");
            return;
        }
        ((CompletableFuture)HandshakeHandler.getSessionServiceClient().requestAuthorizationGrantAsync(this.identityToken, AuthConfig.getServerAudience(), serverSessionToken).thenAccept(authGrant -> {
            if (!this.channel.isActive()) {
                return;
            }
            if (authGrant == null) {
                this.channel.eventLoop().execute(() -> this.disconnect("Failed to obtain authorization grant from session service"));
                return;
            }
            String serverIdentityToken = ServerAuthManager.getInstance().getIdentityToken();
            if (serverIdentityToken == null || serverIdentityToken.isEmpty()) {
                LOGGER.at(Level.SEVERE).log("Server identity token not available - cannot complete mutual authentication");
                this.channel.eventLoop().execute(() -> this.disconnect("Server authentication unavailable - please try again later"));
                return;
            }
            String finalServerIdentityToken = serverIdentityToken;
            this.channel.eventLoop().execute(() -> {
                if (!this.channel.isActive()) {
                    return;
                }
                if (this.authState != AuthState.REQUESTING_AUTH_GRANT) {
                    LOGGER.at(Level.WARNING).log("State changed during auth grant request, current state: %s", (Object)this.authState);
                    return;
                }
                this.clearTimeout();
                LOGGER.at(Level.INFO).log("Sending AuthGrant to %s (with server identity: %s)", (Object)NettyUtil.formatRemoteAddress(this.channel), !finalServerIdentityToken.isEmpty());
                this.write((Packet)new AuthGrant((String)authGrant, finalServerIdentityToken));
                this.authState = AuthState.AWAITING_AUTH_TOKEN;
                HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
                this.continueStage("auth:token", timeouts.getAuthToken(), () -> this.authState != AuthState.AWAITING_AUTH_TOKEN);
            });
        })).exceptionally(ex -> {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause((Throwable)ex)).log("Error requesting auth grant");
            this.channel.eventLoop().execute(() -> this.disconnect("Authentication error: " + ex.getMessage()));
            return null;
        });
    }

    public void handle(@Nonnull Disconnect packet) {
        this.disconnectReason.setClientDisconnectType(packet.type);
        LOGGER.at(Level.INFO).log("%s (%s) at %s left with reason: %s - %s", this.playerUuid, this.username, NettyUtil.formatRemoteAddress(this.channel), packet.type.name(), packet.reason);
        ProtocolUtil.closeApplicationConnection(this.channel);
    }

    public void handle(@Nonnull AuthToken packet) {
        if (this.authState != AuthState.AWAITING_AUTH_TOKEN) {
            LOGGER.at(Level.WARNING).log("Received unexpected AuthToken packet in state %s from %s", (Object)this.authState, (Object)NettyUtil.formatRemoteAddress(this.channel));
            this.disconnect("Protocol error: unexpected AuthToken packet");
            return;
        }
        if (this.authTokenPacketReceived) {
            LOGGER.at(Level.WARNING).log("Received duplicate AuthToken packet from %s", NettyUtil.formatRemoteAddress(this.channel));
            this.disconnect("Protocol error: duplicate AuthToken packet");
            return;
        }
        this.authTokenPacketReceived = true;
        this.authState = AuthState.PROCESSING_AUTH_TOKEN;
        this.clearTimeout();
        String accessToken = packet.accessToken;
        if (accessToken == null || accessToken.isEmpty()) {
            LOGGER.at(Level.WARNING).log("Received AuthToken packet with empty access token from %s", NettyUtil.formatRemoteAddress(this.channel));
            this.disconnect("Invalid access token");
            return;
        }
        String serverAuthGrant = packet.serverAuthorizationGrant;
        X509Certificate clientCert = this.channel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).get();
        LOGGER.at(Level.INFO).log("Received AuthToken from %s, validating JWT (mTLS cert present: %s, server auth grant: %s)", NettyUtil.formatRemoteAddress(this.channel), clientCert != null, serverAuthGrant != null && !serverAuthGrant.isEmpty());
        JWTValidator.JWTClaims claims = HandshakeHandler.getJwtValidator().validateToken(accessToken, clientCert);
        if (claims == null) {
            LOGGER.at(Level.WARNING).log("JWT validation failed for %s", NettyUtil.formatRemoteAddress(this.channel));
            this.disconnect("Invalid access token");
            return;
        }
        UUID tokenUuid = claims.getSubjectAsUUID();
        String tokenUsername = claims.username;
        if (tokenUuid == null || !tokenUuid.equals(this.playerUuid)) {
            LOGGER.at(Level.WARNING).log("JWT UUID mismatch for %s (expected: %s, got: %s)", NettyUtil.formatRemoteAddress(this.channel), this.playerUuid, tokenUuid);
            this.disconnect("Invalid token claims: UUID mismatch");
            return;
        }
        if (tokenUsername == null || tokenUsername.isEmpty()) {
            LOGGER.at(Level.WARNING).log("JWT missing username for %s", NettyUtil.formatRemoteAddress(this.channel));
            this.disconnect("Invalid token claims: missing username");
            return;
        }
        if (!tokenUsername.equals(this.username)) {
            LOGGER.at(Level.WARNING).log("JWT username mismatch for %s (expected: %s, got: %s)", NettyUtil.formatRemoteAddress(this.channel), this.username, tokenUsername);
            this.disconnect("Invalid token claims: username mismatch");
            return;
        }
        this.authenticatedUsername = tokenUsername;
        if (serverAuthGrant != null && !serverAuthGrant.isEmpty()) {
            this.authState = AuthState.EXCHANGING_SERVER_TOKEN;
            HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
            this.continueStage("auth:server-exchange", timeouts.getAuthServerExchange(), () -> this.authState != AuthState.EXCHANGING_SERVER_TOKEN);
            this.exchangeServerAuthGrant(serverAuthGrant);
        } else {
            LOGGER.at(Level.WARNING).log("Client did not provide server auth grant for mutual authentication");
            this.disconnect("Mutual authentication required - please update your client");
        }
    }

    private void exchangeServerAuthGrant(@Nonnull String serverAuthGrant) {
        ServerAuthManager serverAuthManager = ServerAuthManager.getInstance();
        String serverCertFingerprint = serverAuthManager.getServerCertificateFingerprint();
        if (serverCertFingerprint == null) {
            LOGGER.at(Level.SEVERE).log("Server certificate fingerprint not available for mutual auth");
            this.disconnect("Server authentication unavailable - please try again later");
            return;
        }
        String serverSessionToken = serverAuthManager.getSessionToken();
        LOGGER.at(Level.FINE).log("Server session token available: %s, identity token available: %s", serverSessionToken != null, serverAuthManager.getIdentityToken() != null);
        if (serverSessionToken == null) {
            LOGGER.at(Level.SEVERE).log("Server session token not available for auth grant exchange");
            LOGGER.at(Level.FINE).log("Auth mode: %s, has session token: %s, has identity token: %s", serverAuthManager.getAuthStatus(), serverAuthManager.hasSessionToken(), serverAuthManager.hasIdentityToken());
            this.disconnect("Server authentication unavailable - please try again later");
            return;
        }
        LOGGER.at(Level.FINE).log("Using session token (first 20 chars): %s...", serverSessionToken.length() > 20 ? serverSessionToken.substring(0, 20) : serverSessionToken);
        ((CompletableFuture)HandshakeHandler.getSessionServiceClient().exchangeAuthGrantForTokenAsync(serverAuthGrant, serverCertFingerprint, serverSessionToken).thenAccept(serverAccessToken -> {
            if (!this.channel.isActive()) {
                return;
            }
            this.channel.eventLoop().execute(() -> {
                if (!this.channel.isActive()) {
                    return;
                }
                if (this.authState != AuthState.EXCHANGING_SERVER_TOKEN) {
                    LOGGER.at(Level.WARNING).log("State changed during server token exchange, current state: %s", (Object)this.authState);
                    return;
                }
                if (serverAccessToken == null) {
                    LOGGER.at(Level.SEVERE).log("Failed to exchange server auth grant for access token");
                    this.disconnect("Server authentication failed - please try again later");
                    return;
                }
                byte[] passwordChallenge = this.generatePasswordChallengeIfNeeded();
                LOGGER.at(Level.INFO).log("Sending ServerAuthToken to %s (with password challenge: %s)", (Object)NettyUtil.formatRemoteAddress(this.channel), passwordChallenge != null);
                this.write((Packet)new ServerAuthToken((String)serverAccessToken, passwordChallenge));
                this.completeAuthentication(passwordChallenge);
            });
        })).exceptionally(ex -> {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause((Throwable)ex)).log("Error exchanging server auth grant");
            this.channel.eventLoop().execute(() -> {
                if (this.authState != AuthState.EXCHANGING_SERVER_TOKEN) {
                    return;
                }
                this.disconnect("Server authentication failed - please try again later");
            });
            return null;
        });
    }

    private byte[] generatePasswordChallengeIfNeeded() {
        UUID ownerUuid;
        String password = HytaleServer.get().getConfig().getPassword();
        if (password == null || password.isEmpty()) {
            return null;
        }
        if (Constants.SINGLEPLAYER && (ownerUuid = SingleplayerModule.getUuid()) != null && ownerUuid.equals(this.playerUuid)) {
            return null;
        }
        byte[] challenge = new byte[32];
        new SecureRandom().nextBytes(challenge);
        return challenge;
    }

    private void completeAuthentication(byte[] passwordChallenge) {
        this.auth = new PlayerAuthentication(this.playerUuid, this.authenticatedUsername);
        if (this.referralData != null) {
            this.auth.setReferralData(this.referralData);
        }
        if (this.referralSource != null) {
            this.auth.setReferralSource(this.referralSource);
        }
        this.authState = AuthState.AUTHENTICATED;
        this.clearTimeout();
        LOGGER.at(Level.INFO).log("Mutual authentication complete for %s (%s) from %s", this.authenticatedUsername, this.playerUuid, NettyUtil.formatRemoteAddress(this.channel));
        this.onAuthenticated(passwordChallenge);
    }

    protected abstract void onAuthenticated(byte[] var1);

    private static enum AuthState {
        REQUESTING_AUTH_GRANT,
        AWAITING_AUTH_TOKEN,
        PROCESSING_AUTH_TOKEN,
        EXCHANGING_SERVER_TOKEN,
        AUTHENTICATED;

    }
}

