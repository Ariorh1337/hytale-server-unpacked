/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.commands;

import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditCreateNewCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditExitCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditInfoCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditKillEntitiesCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditLoadCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditModifiedCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditSaveAsCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditSaveCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditSaveUICommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditSelectCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditTeleportCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditUpdateBoxCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class PrefabEditCommand
extends AbstractCommandCollection {
    public PrefabEditCommand() {
        super("editprefab", "server.commands.editprefab.desc");
        this.addAliases("prefabedit", "pedit");
        this.addSubCommand(new PrefabEditExitCommand());
        this.addSubCommand(new PrefabEditLoadCommand());
        this.addSubCommand(new PrefabEditCreateNewCommand());
        this.addSubCommand(new PrefabEditSelectCommand());
        this.addSubCommand(new PrefabEditSaveCommand());
        this.addSubCommand(new PrefabEditSaveUICommand());
        this.addSubCommand(new PrefabEditKillEntitiesCommand());
        this.addSubCommand(new PrefabEditSaveAsCommand());
        this.addSubCommand(new PrefabEditUpdateBoxCommand());
        this.addSubCommand(new PrefabEditInfoCommand());
        this.addSubCommand(new PrefabEditTeleportCommand());
        this.addSubCommand(new PrefabEditModifiedCommand());
    }
}

