/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth.oauth;

import com.hypixel.hytale.server.core.auth.oauth.OAuthFlow;

public abstract class OAuthDeviceFlow
extends OAuthFlow {
    public abstract void onFlowInfo(String var1, String var2, String var3, int var4);
}

