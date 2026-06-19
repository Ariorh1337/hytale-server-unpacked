package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerVolumeEntityQuery;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.system.DelayedEffectScheduler;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayAnimationEffect extends TriggerEffect {
   public static final String APPLY_ON_PLAYER = "Player";
   public static final String APPLY_ON_EVERYONE = "Everyone";
   @Nonnull
   public static final BuilderCodec<PlayAnimationEffect> CODEC = BuilderCodec.builder(PlayAnimationEffect.class, PlayAnimationEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("NpcType", Codec.STRING, false), (effect, npcType) -> effect.npcType = npcType == null ? "" : npcType, effect -> effect.npcType)
      .add()
      .append(new KeyedCodec<>("Animation", Codec.STRING, false), (effect, animation) -> effect.animation = animation, effect -> effect.animation)
      .add()
      .append(
         new KeyedCodec<>("Target", new EnumCodec<>(PlayAnimationEffect.Target.class), false),
         (effect, target) -> effect.target = target,
         effect -> effect.target
      )
      .add()
      .append(new KeyedCodec<>("Duration", Codec.FLOAT, false), (effect, duration) -> effect.duration = duration, effect -> effect.duration)
      .add()
      .append(new KeyedCodec<>("Stop", Codec.BOOLEAN, false), (effect, stop) -> effect.stop = stop, effect -> effect.stop)
      .add()
      .build();
   @Nullable
   private String animation;
   @Nonnull
   private static final AnimationSlot SLOT = AnimationSlot.ServerAction;
   @Nonnull
   private PlayAnimationEffect.Target target = PlayAnimationEffect.Target.NPCS_IN_VOLUME;
   @Nonnull
   private String npcType = "";
   private float duration;
   private boolean stop;

   @Override
   public void execute(@Nonnull TriggerContext context) {
      Store<EntityStore> store = context.getStore();
      if (this.stop || this.animation != null && !this.animation.isBlank()) {
         for (Ref<EntityStore> ref : this.resolveTargets(context)) {
            if (this.stop) {
               this.stopOn(store, ref);
            } else {
               boolean started = this.playOn(store, ref);
               if (started && this.duration > 0.0F && Float.isFinite(this.duration)) {
                  this.scheduleStop(context, ref);
               }
            }
         }
      }
   }

   @Nonnull
   public String getNpcType() {
      return this.npcType;
   }

   @Nullable
   public String getAnimation() {
      return this.animation;
   }

   @Nonnull
   private List<Ref<EntityStore>> resolveTargets(@Nonnull TriggerContext context) {
      Store<EntityStore> store = context.getStore();
      if (this.target == PlayAnimationEffect.Target.TRIGGERING_ENTITY) {
         Ref<EntityStore> ref = context.getEntityRef();
         return this.matchesApplyOn(store, ref) ? List.of(ref) : List.of();
      } else {
         List<Ref<EntityStore>> targets = TriggerVolumeEntityQuery.collectTargets(
            store, context.getSpatialVolumes(), this.includesNpcs(), this.includesPlayers(), this.roleFilter()
         );
         Ref<EntityStore> triggerRef = context.getEntityRef();
         if (this.isEligibleTriggerTarget(store, triggerRef) && !containsEntity(store, targets, triggerRef)) {
            ArrayList<Ref<EntityStore>> withTrigger = new ArrayList<>(targets.size() + 1);
            withTrigger.addAll(targets);
            withTrigger.add(triggerRef);
            return withTrigger;
         } else {
            return targets;
         }
      }
   }

   private boolean isEligibleTriggerTarget(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
      if (!this.matchesApplyOn(store, ref)) {
         return false;
      } else if (store.getComponent(ref, NPCEntity.getComponentType()) != null) {
         return this.includesNpcs();
      } else {
         return store.getComponent(ref, PlayerRef.getComponentType()) != null ? this.includesPlayers() : false;
      }
   }

   private static boolean containsEntity(@Nonnull Store<EntityStore> store, @Nonnull List<Ref<EntityStore>> targets, @Nonnull Ref<EntityStore> ref) {
      UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
      if (uuidComponent == null) {
         return targets.contains(ref);
      }

      for (Ref<EntityStore> targetRef : targets) {
         UUIDComponent targetUuid = store.getComponent(targetRef, UUIDComponent.getComponentType());
         if (targetUuid != null && targetUuid.getUuid().equals(uuidComponent.getUuid())) {
            return true;
         }
      }

      return false;
   }

   private boolean includesPlayers() {
      return "Player".equals(this.npcType) || "Everyone".equals(this.npcType);
   }

   private boolean includesNpcs() {
      return !"Player".equals(this.npcType);
   }

   @Nonnull
   private String roleFilter() {
      return !this.npcType.isBlank() && !"Player".equals(this.npcType) && !"Everyone".equals(this.npcType) ? this.npcType : "";
   }

   private boolean matchesApplyOn(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
      if (!ref.isValid()) {
         return false;
      }

      if (this.npcType.isBlank() || "Everyone".equals(this.npcType)) {
         return true;
      }

      if ("Player".equals(this.npcType)) {
         return store.getComponent(ref, PlayerRef.getComponentType()) != null;
      }

      NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
      if (npc == null) {
         return false;
      }

      String roleName = npc.getRoleName();
      return roleName != null && roleName.equalsIgnoreCase(this.npcType);
   }

   private boolean playOn(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
      if (ref.isValid() && this.animation != null) {
         boolean isTickRow = this.getEventType() == TriggerEventType.TICK;
         NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
         if (npc != null) {
            if (isTickRow) {
               ActiveAnimationComponent activeAnimation = store.getComponent(ref, ActiveAnimationComponent.getComponentType());
               if (activeAnimation != null && this.animation.equals(activeAnimation.getActiveAnimations()[SLOT.ordinal()])) {
                  return false;
               }
            }

            npc.playAnimation(ref, SLOT, this.animation, !isTickRow, store);
         } else {
            AnimationUtils.playAnimation(ref, SLOT, this.animation, true, store);
         }

         return true;
      } else {
         return false;
      }
   }

   private void stopOn(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
      if (ref.isValid()) {
         NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
         if (npc != null) {
            npc.playAnimation(ref, SLOT, null, store);
         } else {
            AnimationUtils.stopAnimation(ref, SLOT, true, store);
         }
      }
   }

   private void scheduleStop(@Nonnull TriggerContext context, @Nonnull Ref<EntityStore> ref) {
      Store<EntityStore> store = context.getStore();
      UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
      if (uuidComponent != null) {
         TriggerVolumeManager manager = store.getResource(TriggerVolumesPlugin.get().getManagerResourceType());
         if (manager != null) {
            AnimationSlot slotToStop = SLOT;
            String animationToStop = this.animation;
            String gateKey = "PlayAnimation:" + SLOT.name();
            DelayedEffectScheduler scheduler = manager.getDelayedEffectScheduler();
            scheduler.cancelGates(uuidComponent.getUuid(), gateKey);
            scheduler.scheduleGate(
               (gateRef, gateUuid, gateEventType, gateVolume, gateSpatialVolumes, gateStore, gateNow) -> stopAnimationIfMatching(
                  gateStore, gateRef, slotToStop, animationToStop
               ),
               ref,
               uuidComponent.getUuid(),
               TriggerEventType.EXIT,
               context.getVolume(),
               System.nanoTime(),
               this.duration,
               context.getSpatialVolumes(),
               gateKey
            );
         }
      }
   }

   private static void stopAnimationIfMatching(
      @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull AnimationSlot slot, @Nullable String animation
   ) {
      if (ref.isValid()) {
         NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
         if (npc != null) {
            ActiveAnimationComponent activeAnimation = store.getComponent(ref, ActiveAnimationComponent.getComponentType());
            if (activeAnimation != null && !Objects.equals(activeAnimation.getActiveAnimations()[slot.ordinal()], animation)) {
               return;
            }

            npc.playAnimation(ref, slot, null, store);
         } else {
            AnimationUtils.stopAnimation(ref, slot, true, store);
         }
      }
   }

   public enum Target {
      TRIGGERING_ENTITY,
      NPCS_IN_VOLUME;
   }
}
