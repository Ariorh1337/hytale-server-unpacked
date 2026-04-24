package com.hypixel.hytale.builtin.ambience.resources;

import com.hypixel.hytale.builtin.ambience.AmbiencePlugin;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class AmbienceResource implements Resource<EntityStore> {
   private int forcedMusicContainerIndex;
   private int forcedMusicStateIndex = -1;
   private float forcedMusicStateFadeDuration = 1.0F;
   private int forcedMusicStateVersion;

   public static ResourceType<EntityStore, AmbienceResource> getResourceType() {
      return AmbiencePlugin.get().getAmbienceResourceType();
   }

   public void setForcedMusicContainerIndex(int index) {
      if (this.forcedMusicContainerIndex != index) {
         this.forcedMusicStateIndex = -1;
         this.forcedMusicStateFadeDuration = 1.0F;
         this.forcedMusicStateVersion = 0;
      }

      this.forcedMusicContainerIndex = index;
   }

   public int getForcedMusicContainerIndex() {
      return this.forcedMusicContainerIndex;
   }

   public void setForcedMusicState(int stateIndex, float fadeDuration) {
      this.forcedMusicStateIndex = stateIndex;
      this.forcedMusicStateFadeDuration = fadeDuration;
      this.forcedMusicStateVersion++;
   }

   public int getForcedMusicStateVersion() {
      return this.forcedMusicStateVersion;
   }

   public int getForcedMusicStateIndex() {
      return this.forcedMusicStateIndex;
   }

   public float getForcedMusicStateFadeDuration() {
      return this.forcedMusicStateFadeDuration;
   }

   @Override
   public Resource<EntityStore> clone() {
      AmbienceResource clone = new AmbienceResource();
      clone.forcedMusicContainerIndex = this.forcedMusicContainerIndex;
      clone.forcedMusicStateIndex = this.forcedMusicStateIndex;
      clone.forcedMusicStateFadeDuration = this.forcedMusicStateFadeDuration;
      clone.forcedMusicStateVersion = this.forcedMusicStateVersion;
      return clone;
   }
}
