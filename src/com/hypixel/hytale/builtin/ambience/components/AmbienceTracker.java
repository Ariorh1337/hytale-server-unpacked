package com.hypixel.hytale.builtin.ambience.components;

import com.hypixel.hytale.builtin.ambience.AmbiencePlugin;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.packets.world.UpdateForcedMusic;
import com.hypixel.hytale.protocol.packets.world.UpdateMusicState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmbienceTracker implements Component<EntityStore> {
   @Nonnull
   private final UpdateForcedMusic musicPacket = new UpdateForcedMusic();
   @Nonnull
   private final UpdateMusicState musicStatePacket = new UpdateMusicState();
   private int lastSentContainerIndex;
   private int lastSentMusicStateIndex = -1;
   private float lastSentMusicStateFadeDuration = 1.0F;
   private int lastSentMusicStateVersion;

   public static ComponentType<EntityStore, AmbienceTracker> getComponentType() {
      return AmbiencePlugin.get().getAmbienceTrackerComponentType();
   }

   public void setLastSentContainerIndex(int index) {
      this.lastSentContainerIndex = index;
   }

   public int getLastSentContainerIndex() {
      return this.lastSentContainerIndex;
   }

   @Nonnull
   public UpdateForcedMusic getMusicPacket() {
      return this.musicPacket;
   }

   public void setLastSentMusicState(int index, float fadeDuration) {
      this.lastSentMusicStateIndex = index;
      this.lastSentMusicStateFadeDuration = fadeDuration;
   }

   public int getLastSentMusicStateIndex() {
      return this.lastSentMusicStateIndex;
   }

   public float getLastSentMusicStateFadeDuration() {
      return this.lastSentMusicStateFadeDuration;
   }

   public void setLastSentMusicStateVersion(int version) {
      this.lastSentMusicStateVersion = version;
   }

   public int getLastSentMusicStateVersion() {
      return this.lastSentMusicStateVersion;
   }

   @Nonnull
   public UpdateMusicState getMusicStatePacket() {
      return this.musicStatePacket;
   }

   @Nullable
   @Override
   public Component<EntityStore> clone() {
      AmbienceTracker clone = new AmbienceTracker();
      clone.lastSentContainerIndex = this.lastSentContainerIndex;
      clone.lastSentMusicStateIndex = this.lastSentMusicStateIndex;
      clone.lastSentMusicStateFadeDuration = this.lastSentMusicStateFadeDuration;
      clone.lastSentMusicStateVersion = this.lastSentMusicStateVersion;
      return clone;
   }
}
