/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.ban;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.Ban;

@FunctionalInterface
public interface BanParser {
    public Ban parse(JsonObject var1) throws JsonParseException;
}

