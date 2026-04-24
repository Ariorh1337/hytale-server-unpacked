package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.server.core.Message;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class PermissionBypassSender implements CommandSender {
   @Nonnull
   private final CommandSender delegate;

   public PermissionBypassSender(@Nonnull CommandSender delegate) {
      this.delegate = delegate;
   }

   @Nonnull
   public CommandSender delegateSender() {
      return this.delegate;
   }

   @Override
   public boolean hasPermission(@Nonnull String id) {
      return true;
   }

   @Override
   public boolean hasPermission(@Nonnull String id, boolean def) {
      return true;
   }

   @Override
   public void sendMessage(@Nonnull Message message) {
      this.delegate.sendMessage(message);
   }

   @Override
   public String getUsername() {
      return this.delegate.getUsername();
   }

   @Override
   public UUID getUuid() {
      return this.delegate.getUuid();
   }
}
