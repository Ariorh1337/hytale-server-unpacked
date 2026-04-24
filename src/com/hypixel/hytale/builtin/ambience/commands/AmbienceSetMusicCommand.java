package com.hypixel.hytale.builtin.ambience.commands;

import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.musiccontainer.config.MusicContainer;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class AmbienceSetMusicCommand extends AbstractWorldCommand {
   @Nonnull
   private final RequiredArg<MusicContainer> musicContainerArg = this.withRequiredArg(
      "musicContainerId", "server.commands.ambience.setmusic.arg.musiccontainerid.desc", ArgTypes.MUSIC_CONTAINER_ASSET
   );

   public AmbienceSetMusicCommand() {
      super("setmusic", "server.commands.ambience.setmusic.desc");
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      MusicContainer musicContainer = this.musicContainerArg.get(context);
      AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
      ambienceResource.setForcedMusicContainerIndex(MusicContainer.getAssetMap().getIndex(musicContainer.getId()));
      context.sendMessage(Message.translation("server.commands.ambience.setmusic.success").param("ambience", musicContainer.getId()));
   }
}
