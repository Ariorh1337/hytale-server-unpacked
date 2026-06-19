package com.hypixel.hytale.builtin.triggervolumes.command;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TriggerVolumeTagCommand extends AbstractWorldCommand {
   private final RequiredArg<TriggerVolumeTagCommand.TagOperation> operationArg = this.withRequiredArg(
      "operation", "server.commands.triggervolume.tag.operation.desc", TriggerVolumeArgTypes.TAG_OPERATION
   );
   private final RequiredArg<String> volumeNameArg = this.withRequiredArg(
      "name", "server.commands.triggervolume.tag.name.desc", TriggerVolumeArgTypes.VOLUME_NAME
   );
   private final RequiredArg<String> keyArg = this.withRequiredArg("key", "server.commands.triggervolume.tag.key.desc", ArgTypes.STRING);
   private final DefaultArg<String> valueArg = this.withDefaultArg(
      "value", "server.commands.triggervolume.tag.value.desc", ArgTypes.STRING, "", "server.commands.triggervolume.tag.value.default"
   );

   public TriggerVolumeTagCommand() {
      super("tag", "server.commands.triggervolume.tag.desc");
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
      TriggerVolumeTagCommand.TagOperation operation = this.operationArg.get(context);
      String name = this.volumeNameArg.get(context);
      String key = this.keyArg.get(context);
      String value = this.valueArg.get(context);
      TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
      if (manager != null) {
         VolumeEntry entry = manager.getVolume(name);
         if (entry == null) {
            context.sendMessage(Message.translation("server.commands.triggervolume.notFound").param("name", name));
         } else {
            Ref<EntityStore> playerRef = context.senderAsPlayerRef();
            if (playerRef != null && playerRef.isValid()) {
               UUIDComponent uuidComponent = store.getComponent(playerRef, UUIDComponent.getComponentType());
               if (uuidComponent != null) {
                  boolean changed = switch (operation) {
                     case SET -> manager.setTag(entry.getId(), key, value, playerRef, uuidComponent.getUuid());
                     case REMOVE -> manager.removeTag(entry.getId(), key, value.isBlank() ? null : value, playerRef, uuidComponent.getUuid());
                  };
                  if (changed) {
                     context.sendMessage(
                        Message.translation("server.commands.triggervolume.tag.success")
                           .param("operation", operation.token())
                           .param("key", key)
                           .param("name", name)
                     );
                  } else {
                     context.sendMessage(Message.translation("server.commands.triggervolume.tag.noChange").param("key", key).param("name", name));
                  }
               }
            }
         }
      }
   }

   public enum TagOperation {
      SET,
      REMOVE;

      @Nonnull
      public String token() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      @Nullable
      public static TriggerVolumeTagCommand.TagOperation fromInput(@Nonnull String input) {
         for (TriggerVolumeTagCommand.TagOperation operation : values()) {
            if (operation.token().equalsIgnoreCase(input)) {
               return operation;
            }
         }

         return null;
      }
   }
}
