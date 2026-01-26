/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions.provider;

import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface PermissionProvider {
    @Nonnull
    public String getName();

    public void addUserPermissions(@Nonnull UUID var1, @Nonnull Set<String> var2);

    public void removeUserPermissions(@Nonnull UUID var1, @Nonnull Set<String> var2);

    public Set<String> getUserPermissions(@Nonnull UUID var1);

    public void addGroupPermissions(@Nonnull String var1, @Nonnull Set<String> var2);

    public void removeGroupPermissions(@Nonnull String var1, @Nonnull Set<String> var2);

    public Set<String> getGroupPermissions(@Nonnull String var1);

    public void addUserToGroup(@Nonnull UUID var1, @Nonnull String var2);

    public void removeUserFromGroup(@Nonnull UUID var1, @Nonnull String var2);

    public Set<String> getGroupsForUser(@Nonnull UUID var1);
}

