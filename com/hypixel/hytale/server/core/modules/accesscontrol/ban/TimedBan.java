/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.ban;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.AbstractBan;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class TimedBan
extends AbstractBan {
    private final Instant expiresOn;

    @Nonnull
    public static TimedBan fromJsonObject(@Nonnull JsonObject object) throws JsonParseException {
        try {
            UUID target = UUID.fromString(object.get("target").getAsString());
            UUID by = UUID.fromString(object.get("by").getAsString());
            Instant timestamp = Instant.ofEpochMilli(object.get("timestamp").getAsLong());
            Instant expiresOn = Instant.ofEpochMilli(object.get("expiresOn").getAsLong());
            String reason = null;
            if (object.has("reason")) {
                reason = object.get("reason").getAsString();
            }
            return new TimedBan(target, by, timestamp, expiresOn, reason);
        }
        catch (Throwable throwable) {
            throw new JsonParseException(throwable);
        }
    }

    public TimedBan(UUID target, UUID by, Instant timestamp, Instant expiresOn, String reason) {
        super(target, by, timestamp, reason);
        this.expiresOn = expiresOn;
    }

    @Override
    public boolean isInEffect() {
        return this.expiresOn.isAfter(Instant.now());
    }

    @Override
    @Nonnull
    public String getType() {
        return "timed";
    }

    public Instant getExpiresOn() {
        return this.expiresOn;
    }

    @Override
    @Nonnull
    public CompletableFuture<Optional<String>> getDisconnectReason(UUID uuid) {
        Duration timeRemaining = Duration.between(Instant.now(), this.expiresOn);
        StringBuilder message = new StringBuilder("You are temporarily banned for ").append(StringUtil.humanizeTime(timeRemaining)).append('!');
        this.reason.ifPresent(s -> message.append(" Reason: ").append((String)s));
        return CompletableFuture.completedFuture(Optional.of(message.toString()));
    }

    @Override
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject object = super.toJsonObject();
        object.addProperty("expiresOn", this.expiresOn.toEpochMilli());
        return object;
    }
}

