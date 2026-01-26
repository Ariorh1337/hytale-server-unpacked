/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.server.auth;

import com.hypixel.hytale.server.core.command.commands.server.auth.AuthCancelCommand;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthLoginCommand;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthLogoutCommand;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthPersistenceCommand;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthSelectCommand;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthStatusCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class AuthCommand
extends AbstractCommandCollection {
    public AuthCommand() {
        super("auth", "server.commands.auth.desc");
        this.addSubCommand(new AuthStatusCommand());
        this.addSubCommand(new AuthLoginCommand());
        this.addSubCommand(new AuthSelectCommand());
        this.addSubCommand(new AuthLogoutCommand());
        this.addSubCommand(new AuthCancelCommand());
        this.addSubCommand(new AuthPersistenceCommand());
    }
}

