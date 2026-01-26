/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.suggestion;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionResult;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface SuggestionProvider {
    public void suggest(@Nonnull CommandSender var1, @Nonnull String var2, int var3, @Nonnull SuggestionResult var4);
}

