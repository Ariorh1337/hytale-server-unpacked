package com.hypixel.hytale.builtin.triggervolumes.command;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class TriggerVolumeTpCommand extends AbstractWorldCommand {
   private final RequiredArg<String> nameArg = this.withRequiredArg("name", "server.commands.triggervolume.tp.name.desc", TriggerVolumeArgTypes.VOLUME_NAME);

   public TriggerVolumeTpCommand() {
      super("tp", "server.commands.triggervolume.tp.desc");
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
      String name = this.nameArg.get(context);
      TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
      if (manager != null) {
         VolumeEntry entry = manager.getVolume(name);
         if (entry == null) {
            context.sendMessage(Message.translation("server.commands.triggervolume.notFound").param("name", name));
         } else {
            Ref<EntityStore> playerRef = context.senderAsPlayerRef();
            if (playerRef != null && playerRef.isValid()) {
               TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
               if (transform != null) {
                  Vector3d destination = new Vector3d(entry.getPosition());
                  store.addComponent(playerRef, Teleport.getComponentType(), Teleport.createForPlayer(destination, transform.getRotation()));
                  context.sendMessage(Message.translation("server.commands.triggervolume.tp.success").param("name", name));
               }
            }
         }
      }
   }
}
