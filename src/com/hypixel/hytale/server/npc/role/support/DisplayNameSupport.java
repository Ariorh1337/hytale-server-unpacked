package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentDisplayName;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DisplayNameSupport implements Component<EntityStore> {
   @Nullable
   private String[] displayNames;
   @Nullable
   private String nominatedDisplayName;

   @Nonnull
   public static ComponentType<EntityStore, DisplayNameSupport> getComponentType() {
      return NPCPlugin.get().getDisplayNameSupportComponentType();
   }

   @Nonnull
   public static DisplayNameSupport get(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      DisplayNameSupport support = accessor.getComponent(ref, getComponentType());
      assert support != null : "Missing DisplayNameSupport on entity " + ref;
      return support;
   }

   public void setDisplayNames(@Nullable String[] displayNames) {
      this.displayNames = displayNames;
   }

   @Nullable
   public String[] getDisplayNames() {
      return this.displayNames;
   }

   public void nominateDisplayName(@Nonnull String displayName) {
      this.nominatedDisplayName = displayName;
   }

   public void handleNominatedDisplayName(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.nominatedDisplayName != null) {
         setDisplayName(ref, this.nominatedDisplayName, componentAccessor);
      }

      this.nominatedDisplayName = null;
   }

   public void pickRandomDisplayName(@Nonnull Holder<EntityStore> holder, boolean override) {
      setRandomDisplayName(holder, this.displayNames, override);
   }

   public void pickRandomDisplayName(@Nonnull Ref<EntityStore> ref, boolean override, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      setRandomDisplayName(ref, this.displayNames, override, componentAccessor);
   }

   public static void setDisplayName(@Nonnull Holder<EntityStore> holder, @Nonnull String displayName) {
      setDisplayName(holder, displayName, true);
   }

   public static void setDisplayName(@Nonnull Holder<EntityStore> holder, @Nullable String displayName, boolean override) {
      PersistentDisplayName persistentDisplayName = holder.getComponent(PersistentDisplayName.getComponentType());
      if (persistentDisplayName != null) {
         Message existing = persistentDisplayName.getDisplayName();
         if (existing != null && !existing.getAnsiMessage().isEmpty() && !override) {
            return;
         }
      }

      Message message = displayName != null ? Message.raw(displayName) : null;
      holder.putComponent(PersistentDisplayName.getComponentType(), new PersistentDisplayName(message));
      holder.putComponent(DisplayNameComponent.getComponentType(), new DisplayNameComponent(message));
      if (displayName != null) {
         Nameplate nameplateComponent = holder.ensureAndGetComponent(Nameplate.getComponentType());
         nameplateComponent.setText(displayName);
      } else {
         holder.removeComponent(Nameplate.getComponentType());
      }
   }

   public static void setRandomDisplayName(
      @Nonnull Ref<EntityStore> ref, @Nullable String[] names, boolean override, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (names != null && names.length != 0) {
         setDisplayName(ref, names[MathUtil.randomInt(0, names.length)], override, componentAccessor);
      }
   }

   public static void setRandomDisplayName(@Nonnull Holder<EntityStore> holder, @Nullable String[] names, boolean override) {
      if (names != null && names.length != 0) {
         setDisplayName(holder, names[MathUtil.randomInt(0, names.length)], override);
      }
   }

   public static void setDisplayName(@Nonnull Ref<EntityStore> ref, @Nonnull String displayName, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      setDisplayName(ref, displayName, true, componentAccessor);
   }

   public static void setDisplayName(
      @Nonnull Ref<EntityStore> ref, @Nullable String displayName, boolean override, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (ref.isValid()) {
         PersistentDisplayName persistentDisplayName = componentAccessor.getComponent(ref, PersistentDisplayName.getComponentType());
         if (persistentDisplayName != null) {
            Message existing = persistentDisplayName.getDisplayName();
            if (existing != null && !existing.getAnsiMessage().isEmpty() && !override) {
               return;
            }
         }

         Message message = displayName != null ? Message.raw(displayName) : null;
         componentAccessor.putComponent(ref, PersistentDisplayName.getComponentType(), new PersistentDisplayName(message));
         componentAccessor.putComponent(ref, DisplayNameComponent.getComponentType(), new DisplayNameComponent(message));
         if (displayName != null) {
            Nameplate nameplateComponent = componentAccessor.ensureAndGetComponent(ref, Nameplate.getComponentType());
            nameplateComponent.setText(displayName);
         } else {
            componentAccessor.removeComponent(ref, Nameplate.getComponentType());
         }
      }
   }

   @Override
   public Component<EntityStore> clone() {
      return this;
   }
}
