/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions;

import javax.annotation.Nonnull;

public interface PermissionHolder {
    public boolean hasPermission(@Nonnull String var1);

    public boolean hasPermission(@Nonnull String var1, boolean var2);
}

