package com.hypixel.hytale.builtin.triggervolumes.command;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public class TriggerVolumeRenameCommand extends AbstractWorldCommand {
   private static final Pattern VALID_ID = Pattern.compile("^[a-zA-Z0-9_]{1,64}$");
   private final RequiredArg<String> oldIdArg = this.withRequiredArg(
      "oldId", "server.commands.triggervolume.rename.oldId.desc", TriggerVolumeArgTypes.VOLUME_NAME
   );
   private final RequiredArg<String> newIdArg = this.withRequiredArg("newId", "server.commands.triggervolume.rename.newId.desc", ArgTypes.STRING);

   public TriggerVolumeRenameCommand() {
      super("rename", "server.commands.triggervolume.rename.desc");
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
      String oldId = this.oldIdArg.get(context);
      String newId = this.newIdArg.get(context);
      TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
      if (manager != null) {
         if (!VALID_ID.matcher(newId).matches()) {
            context.sendMessage(Message.translation("server.commands.triggervolume.rename.invalidId").param("id", newId));
         } else if (manager.getVolume(oldId) == null) {
            context.sendMessage(Message.translation("server.commands.triggervolume.notFound").param("name", oldId));
         } else {
            VolumeEntry renamed = manager.renameVolume(oldId, newId);
            if (renamed == null) {
               context.sendMessage(Message.translation("server.commands.triggervolume.rename.collision").param("id", newId));
            } else {
               manager.notifyViewers();
               context.sendMessage(Message.translation("server.commands.triggervolume.rename.success").param("oldId", oldId).param("newId", newId));
            }
         }
      }
   }
}
