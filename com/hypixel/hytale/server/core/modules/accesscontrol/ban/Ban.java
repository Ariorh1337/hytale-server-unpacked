/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.ban;

import com.google.gson.JsonObject;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.AccessProvider;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Ban
extends AccessProvider {
    public UUID getTarget();

    public UUID getBy();

    public Instant getTimestamp();

    public boolean isInEffect();

    public Optional<String> getReason();

    public String getType();

    public JsonObject toJsonObject();
}

