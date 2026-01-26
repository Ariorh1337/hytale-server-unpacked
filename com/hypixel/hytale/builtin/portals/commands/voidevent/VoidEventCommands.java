/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.commands.voidevent;

import com.hypixel.hytale.builtin.portals.commands.voidevent.StartVoidEventCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class VoidEventCommands
extends AbstractCommandCollection {
    public VoidEventCommands() {
        super("voidevent", "server.commands.voidevent.desc");
        this.addSubCommand(new StartVoidEventCommand());
    }
}

