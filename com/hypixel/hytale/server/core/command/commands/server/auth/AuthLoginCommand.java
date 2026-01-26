/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.server.auth;

import com.hypixel.hytale.server.core.command.commands.server.auth.AuthLoginBrowserCommand;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthLoginDeviceCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class AuthLoginCommand
extends AbstractCommandCollection {
    public AuthLoginCommand() {
        super("login", "server.commands.auth.login.desc");
        this.addSubCommand(new AuthLoginBrowserCommand());
        this.addSubCommand(new AuthLoginDeviceCommand());
    }
}

