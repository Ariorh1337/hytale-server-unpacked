/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility.metacommands;

import com.hypixel.hytale.server.core.command.commands.utility.metacommands.DumpCommandsCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class CommandsCommand
extends AbstractCommandCollection {
    public CommandsCommand() {
        super("commands", "server.commands.meta.desc");
        this.addSubCommand(new DumpCommandsCommand());
    }
}

