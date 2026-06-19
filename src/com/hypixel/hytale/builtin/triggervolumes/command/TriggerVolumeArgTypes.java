package com.hypixel.hytale.builtin.triggervolumes.command;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionResult;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TriggerVolumeArgTypes {
   public static final SingleArgumentType<TriggerVolumeTagCommand.TagOperation> TAG_OPERATION = new SingleArgumentType<TriggerVolumeTagCommand.TagOperation>(
      "server.commands.triggervolume.argtype.tagOperation.name",
      "server.commands.triggervolume.argtype.tagOperation.usage",
      TriggerVolumeTagCommand.TagOperation.SET.token(),
      TriggerVolumeTagCommand.TagOperation.REMOVE.token()
   ) {
      @Nullable
      public TriggerVolumeTagCommand.TagOperation parse(String input, ParseResult parseResult) {
         TriggerVolumeTagCommand.TagOperation operation = TriggerVolumeTagCommand.TagOperation.fromInput(input);
         if (operation == null) {
            parseResult.fail(Message.translation("server.commands.triggervolume.tag.unknownOperation").param("operation", input));
         }

         return operation;
      }

      @Override
      public void suggest(@Nonnull CommandSender sender, @Nonnull String textAlreadyEntered, int numParametersTyped, @Nonnull SuggestionResult result) {
         String lowerInput = textAlreadyEntered.toLowerCase(Locale.ROOT);

         for (TriggerVolumeTagCommand.TagOperation operation : TriggerVolumeTagCommand.TagOperation.values()) {
            String token = operation.token();
            if (lowerInput.isEmpty() || token.startsWith(lowerInput)) {
               result.suggest(token);
            }
         }
      }

      @Override
      public int getSuggestionValueCount() {
         return TriggerVolumeTagCommand.TagOperation.values().length;
      }
   };
   public static final SingleArgumentType<String> VOLUME_NAME = new SingleArgumentType<String>(
      "server.commands.triggervolume.argtype.volumeName.name", "server.commands.triggervolume.argtype.volumeName.usage", "my_volume", "zone_entrance"
   ) {
      @Nullable
      public String parse(String input, ParseResult parseResult) {
         return input;
      }

      @Override
      public void suggest(@Nonnull CommandSender sender, @Nonnull String textAlreadyEntered, int numParametersTyped, @Nonnull SuggestionResult result) {
         TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
         if (plugin != null) {
            String lowerInput = textAlreadyEntered.toLowerCase(Locale.ROOT);
            int count = 0;

            for (World world : Universe.get().getWorlds().values()) {
               Store<EntityStore> store = world.getEntityStore().getStore();
               if (store != null) {
                  TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
                  if (manager != null) {
                     for (String name : manager.getVolumesMap().keySet()) {
                        if (lowerInput.isEmpty() || name.toLowerCase(Locale.ROOT).startsWith(lowerInput)) {
                           result.suggest(name);
                           if (++count >= 20) {
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   };
   public static final SingleArgumentType<String> GROUP_NAME = new SingleArgumentType<String>(
      "server.commands.triggervolume.argtype.groupName.name", "server.commands.triggervolume.argtype.groupName.usage", "tvg_abc123"
   ) {
      @Nullable
      public String parse(String input, ParseResult parseResult) {
         return input;
      }

      @Override
      public void suggest(@Nonnull CommandSender sender, @Nonnull String textAlreadyEntered, int numParametersTyped, @Nonnull SuggestionResult result) {
         TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
         if (plugin != null) {
            String lowerInput = textAlreadyEntered.toLowerCase(Locale.ROOT);
            int count = 0;

            for (World world : Universe.get().getWorlds().values()) {
               Store<EntityStore> store = world.getEntityStore().getStore();
               if (store != null) {
                  TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
                  if (manager != null) {
                     for (String name : manager.getGroupsMap().keySet()) {
                        if (lowerInput.isEmpty() || name.toLowerCase(Locale.ROOT).startsWith(lowerInput)) {
                           result.suggest(name);
                           if (++count >= 20) {
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   };

   private TriggerVolumeArgTypes() {
   }
}
