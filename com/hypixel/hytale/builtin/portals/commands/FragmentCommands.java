/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.commands;

import com.hypixel.hytale.builtin.portals.commands.TimerFragmentCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class FragmentCommands
extends AbstractCommandCollection {
    public FragmentCommands() {
        super("fragment", "server.commands.fragment.desc");
        this.addSubCommand(new TimerFragmentCommand());
    }
}

