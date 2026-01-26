/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.player.camera;

import com.hypixel.hytale.server.core.command.commands.player.camera.PlayerCameraDemoSubCommand;
import com.hypixel.hytale.server.core.command.commands.player.camera.PlayerCameraResetCommand;
import com.hypixel.hytale.server.core.command.commands.player.camera.PlayerCameraSideScrollerCommand;
import com.hypixel.hytale.server.core.command.commands.player.camera.PlayerCameraTopdownCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class PlayerCameraSubCommand
extends AbstractCommandCollection {
    public PlayerCameraSubCommand() {
        super("camera", "server.commands.camera.desc");
        this.addSubCommand(new PlayerCameraResetCommand());
        this.addSubCommand(new PlayerCameraTopdownCommand());
        this.addSubCommand(new PlayerCameraSideScrollerCommand());
        this.addSubCommand(new PlayerCameraDemoSubCommand());
    }
}

