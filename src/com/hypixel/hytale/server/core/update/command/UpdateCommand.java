package com.hypixel.hytale.server.core.update.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class UpdateCommand extends AbstractCommandCollection {
   public UpdateCommand() {
      super("update", "server.commands.update.desc");
      this.addSubCommand(new UpdateCheckCommand());
      this.addSubCommand(new UpdateDownloadCommand());
      this.addSubCommand(new UpdateApplyCommand());
      this.addSubCommand(new UpdateCancelCommand());
      this.addSubCommand(new UpdateStatusCommand());
      this.addSubCommand(new UpdatePatchlineCommand());
   }
}
