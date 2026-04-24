package com.hypixel.hytale.builtin.ambience.commands;

import com.hypixel.hytale.assetstore.map.IndexedAssetMap;
import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.musiccontainer.config.MusicContainer;
import com.hypixel.hytale.server.core.asset.type.musiccontainer.config.SegmentMusicContainer;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class AmbienceSetMusicStateCommand extends AbstractWorldCommand {
   @Nonnull
   private final RequiredArg<String> stateNameArg = this.withRequiredArg(
      "stateName", "server.commands.ambience.setmusicstate.arg.statename.desc", ArgTypes.STRING
   );
   @Nonnull
   private final RequiredArg<Float> fadeDurationArg = this.withRequiredArg(
      "fadeDuration", "server.commands.ambience.setmusicstate.arg.fadeduration.desc", ArgTypes.FLOAT
   );

   public AmbienceSetMusicStateCommand() {
      super("setmusicstate", "server.commands.ambience.setmusicstate.desc");
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      String stateName = this.stateNameArg.get(context);
      Float rawFadeDuration = this.fadeDurationArg.get(context);
      float fadeDuration = Float.isFinite(rawFadeDuration) && rawFadeDuration >= 0.0F ? rawFadeDuration : 0.0F;
      AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
      int forcedIndex = ambienceResource.getForcedMusicContainerIndex();
      if (forcedIndex <= 0) {
         context.sendMessage(Message.translation("server.commands.ambience.setmusicstate.notfound").param("state", stateName));
      } else {
         int stateIndex = resolveStateIndex(forcedIndex, stateName);
         if (stateIndex < 0) {
            context.sendMessage(Message.translation("server.commands.ambience.setmusicstate.notfound").param("state", stateName));
         } else {
            ambienceResource.setForcedMusicState(stateIndex, fadeDuration);
            context.sendMessage(
               Message.translation("server.commands.ambience.setmusicstate.success").param("state", stateName).param("fade", String.valueOf(fadeDuration))
            );
         }
      }
   }

   private static int resolveStateIndex(int forcedContainerIndex, @Nonnull String stateName) {
      if (forcedContainerIndex <= 0) {
         return -1;
      }

      IndexedAssetMap<String, MusicContainer> assetMap = MusicContainer.getAssetMap();

      for (MusicContainer container : assetMap.getAssetMap().values()) {
         if (assetMap.getIndex(container.getId()) == forcedContainerIndex) {
            return findStateInHierarchy(container, stateName);
         }
      }

      return -1;
   }

   private static int findStateInHierarchy(@Nonnull MusicContainer container, @Nonnull String stateName) {
      if (container instanceof SegmentMusicContainer segment) {
         return findStateInSegment(segment, stateName);
      } else {
         IndexedAssetMap<String, MusicContainer> assetMap = MusicContainer.getAssetMap();

         for (String childId : container.getChildIds()) {
            if (childId != null) {
               MusicContainer child = assetMap.getAsset(childId);
               if (child != null) {
                  int result = findStateInHierarchy(child, stateName);
                  if (result >= 0) {
                     return result;
                  }
               }
            }
         }

         return -1;
      }
   }

   private static int findStateInSegment(@Nonnull SegmentMusicContainer segment, @Nonnull String stateName) {
      if (segment.getStates() == null) {
         return -1;
      }

      int i = 0;

      for (String key : segment.getStates().keySet()) {
         if (key.equals(stateName)) {
            return i;
         }

         i++;
      }

      return -1;
   }
}
