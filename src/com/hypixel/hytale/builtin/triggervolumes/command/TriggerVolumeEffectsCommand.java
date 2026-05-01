package com.hypixel.hytale.builtin.triggervolumes.command;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.ui.TriggerVolumeEffectEditorPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.Locale;
import javax.annotation.Nonnull;

public class TriggerVolumeEffectsCommand extends AbstractPlayerCommand {
   private final OptionalArg<String> volumeNameArg = this.withOptionalArg(
      "volumeName", "server.commands.triggervolume.effects.volumeName.desc", TriggerVolumeArgTypes.VOLUME_NAME
   );
   private final FlagArg groupFlag = this.withFlagArg("group", "server.commands.triggervolume.effects.group.desc");

   public TriggerVolumeEffectsCommand() {
      super("effects", "server.commands.triggervolume.effects.desc");
   }

   @Override
   protected void execute(
      @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
   ) {
      TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
      String worldName = world.getName().toLowerCase(Locale.ROOT);
      TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
      if (manager != null) {
         String volumeName = this.volumeNameArg.get(context);
         if (volumeName == null) {
            volumeName = manager.getPlayerSelection(context.sender().getUuid());
         }

         if (volumeName == null) {
            context.sendMessage(Message.translation("server.commands.triggervolume.effects.noSelection"));
         } else {
            VolumeEntry entry = manager.getVolume(volumeName);
            if (entry == null) {
               context.sendMessage(Message.translation("server.commands.triggervolume.notFound").param("name", volumeName));
            } else {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (playerComponent != null) {
                  String groupId = entry.getGroupId();
                  if (this.groupFlag.get(context) && groupId != null) {
                     GroupEntry group = manager.getGroup(groupId);
                     if (group != null) {
                        ArrayList<VolumeEntry> members = new ArrayList<>();

                        for (String memberId : group.getMemberVolumeIds()) {
                           VolumeEntry m = manager.getVolume(memberId);
                           if (m != null) {
                              members.add(m);
                           }
                        }

                        playerComponent.getPageManager()
                           .openCustomPage(ref, store, new TriggerVolumeEffectEditorPage(playerRef, entry, manager, groupId, members));
                        return;
                     }
                  }

                  if (groupId != null) {
                     GroupEntry group = manager.getGroup(groupId);
                     if (group != null) {
                        playerComponent.getPageManager().openCustomPage(ref, store, new TriggerVolumeEffectEditorPage(playerRef, entry, manager, group));
                        return;
                     }
                  }

                  playerComponent.getPageManager().openCustomPage(ref, store, new TriggerVolumeEffectEditorPage(playerRef, entry, manager));
               }
            }
         }
      }
   }
}
