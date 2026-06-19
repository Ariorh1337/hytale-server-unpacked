package com.hypixel.hytale.server.npc.entities;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.vector.Rotation3fc;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.ApplicationEffects;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.support.CombatSupport;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.role.support.StateSupport;
import com.hypixel.hytale.server.npc.storage.AlarmStore;
import com.hypixel.hytale.server.npc.util.DamageData;
import com.hypixel.hytale.server.spawning.assets.spawns.config.WorldNPCSpawn;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class NPCEntity extends LivingEntity implements INonPlayerCharacter {
   public static final BuilderCodec<NPCEntity> CODEC = BuilderCodec.<LivingEntity>builder(NPCEntity.class, NPCEntity::new, LivingEntity.CODEC)
      .addField(new KeyedCodec<>("Env", Codec.STRING), (npcEntity, s) -> npcEntity.environmentIndex = Environment.getAssetMap().getIndex(s), npcEntity -> {
         Environment environment = Environment.getAssetMap().getAssetOrDefault(npcEntity.environmentIndex, null);
         return environment != null ? environment.getId() : null;
      })
      .addField(new KeyedCodec<>("HvrPhs", Codec.DOUBLE), (npcEntity, d) -> npcEntity.hoverPhase = d.floatValue(), npcEntity -> (double)npcEntity.hoverPhase)
      .addField(new KeyedCodec<>("HvrHght", Codec.DOUBLE), (npcEntity, d) -> npcEntity.hoverHeight = d, npcEntity -> npcEntity.hoverHeight)
      .addField(new KeyedCodec<>("SpawnName", Codec.STRING), (npcEntity, s) -> {
         npcEntity.spawnRoleName = s;
         npcEntity.spawnRoleIndex = NPCPlugin.get().getIndex(s);
      }, npcEntity -> npcEntity.spawnRoleName)
      .addField(
         new KeyedCodec<>("MdlScl", Codec.DOUBLE),
         (npcEntity, d) -> npcEntity.initialModelScale = d.floatValue(),
         npcEntity -> (double)npcEntity.initialModelScale
      )
      .addField(new KeyedCodec<>("SpawnConfig", Codec.STRING), (npcEntity, s) -> {
         npcEntity.spawnConfigurationName = s;
         npcEntity.spawnConfigurationIndex = WorldNPCSpawn.getAssetMap().getIndex(s);
      }, npcEntity -> npcEntity.spawnConfigurationName)
      .addField(new KeyedCodec<>("SpawnInstant", Codec.INSTANT), (npcEntity, instant) -> npcEntity.spawnInstant = instant, npcEntity -> npcEntity.spawnInstant)
      .append(new KeyedCodec<>("AlarmStore", AlarmStore.CODEC), (npcEntity, alarmStore) -> npcEntity.alarmStore = alarmStore, npcEntity -> null)
      .add()
      .addField(new KeyedCodec<>("WorldgenId", Codec.INTEGER), (npcEntity, i) -> npcEntity.worldgenId = i, npcEntity -> npcEntity.worldgenId)
      .append(new KeyedCodec<>("PathManager", PathManager.CODEC), (npcEntity, manager) -> npcEntity.pathManager = manager, npcEntity -> npcEntity.pathManager)
      .add()
      .addField(new KeyedCodec<>("LeashPos", Vector3dUtil.CODEC), (npcEntity, v) -> {
         npcEntity.leashPoint.set(v);
         npcEntity.hasLeashPosition = true;
      }, npcEntity -> npcEntity.requiresLeashPosition() ? npcEntity.leashPoint : null)
      .addField(
         new KeyedCodec<>("LeashHdg", Codec.DOUBLE),
         (npcEntity, v) -> npcEntity.leashHeading = v.floatValue(),
         npcEntity -> npcEntity.requiresLeashPosition() ? (double)npcEntity.leashHeading : null
      )
      .addField(
         new KeyedCodec<>("LeashPtch", Codec.DOUBLE),
         (npcEntity, v) -> npcEntity.leashPitch = v.floatValue(),
         npcEntity -> npcEntity.requiresLeashPosition() ? (double)npcEntity.leashPitch : null
      )
      .addField(new KeyedCodec<>("RoleName", Codec.STRING), (npcEntity, s) -> npcEntity.roleName = s, npcEntity -> npcEntity.roleName)
      .addField(
         new KeyedCodec<>("ActiveMC", Codec.STRING),
         (npcEntity, s) -> npcEntity.activeMotionControllerName = s,
         npcEntity -> npcEntity.activeMotionControllerName
      )
      .build();
   private String roleName;
   private int roleIndex = Integer.MIN_VALUE;
   @Nullable
   private Role role;
   private int spawnRoleIndex = Integer.MIN_VALUE;
   @Nullable
   private String spawnRoleName;
   @Nullable
   private String spawnConfigurationName;
   @Nullable
   private String activeMotionControllerName;
   private int environmentIndex = Integer.MIN_VALUE;
   private int spawnConfigurationIndex = Integer.MIN_VALUE;
   private boolean isSpawnTracked;
   private boolean collectSensorStats;
   private boolean isDespawning;
   private boolean isPlayingDespawnAnim;
   private float despawnRemainingSeconds;
   private float despawnCheckRemainingSeconds = RandomExtra.randomRange(1.0F, 5.0F);
   private float despawnAnimationRemainingSeconds;
   private float cachedEntityHorizontalSpeedMultiplier = Float.MAX_VALUE;
   private final Vector3d leashPoint = new Vector3d();
   private float leashHeading;
   private float leashPitch;
   private boolean hasLeashPosition;
   private float hoverPhase;
   private double hoverHeight;
   private float initialModelScale = 1.0F;
   private Instant spawnInstant;
   @Nonnull
   private PathManager pathManager = new PathManager();
   private final DamageData damageData = new DamageData();
   @Deprecated(forRemoval = true)
   @Nullable
   private AlarmStore alarmStore;
   @Deprecated(forRemoval = true)
   private int worldgenId = 0;
   @Nonnull
   private final Set<UUID> reservedBy = new HashSet<>();
   private final Vector3d oldPosition = new Vector3d();

   @Nullable
   public static ComponentType<EntityStore, NPCEntity> getComponentType() {
      return EntityModule.get().getComponentType(NPCEntity.class);
   }

   public NPCEntity() {
      this.role = null;
   }

   public NPCEntity(@Nonnull World world) {
      super(world);
      this.role = null;
   }

   @Deprecated(forRemoval = true)
   @Nullable
   public AlarmStore takeLegacyAlarmStore() {
      AlarmStore legacy = this.alarmStore;
      this.alarmStore = null;
      return legacy;
   }

   @Nullable
   public Role getRole() {
      return this.role;
   }

   public void invalidateCachedHorizontalSpeedMultiplier() {
      this.cachedEntityHorizontalSpeedMultiplier = Float.MAX_VALUE;
   }

   public void storeTickStartPosition(@Nonnull Vector3d position) {
      this.oldPosition.set(position);
   }

   public boolean tickDespawnAnimationRemainingSeconds(float dt) {
      return (this.despawnAnimationRemainingSeconds -= dt) <= 0.0F;
   }

   public void setDespawnAnimationRemainingSeconds(float seconds) {
      this.despawnAnimationRemainingSeconds = seconds;
   }

   public boolean tickDespawnRemainingSeconds(float dt) {
      return (this.despawnRemainingSeconds -= dt) <= 0.0F;
   }

   public void setDespawnRemainingSeconds(float seconds) {
      this.despawnRemainingSeconds = seconds;
   }

   public void setDespawning(boolean despawning) {
      this.isDespawning = despawning;
   }

   public void setPlayingDespawnAnim(boolean playingDespawnAnim) {
      this.isPlayingDespawnAnim = playingDespawnAnim;
   }

   public boolean tickDespawnCheckRemainingSeconds(float dt) {
      return (this.despawnCheckRemainingSeconds -= dt) <= 0.0F;
   }

   public void setDespawnCheckRemainingSeconds(float seconds) {
      this.despawnCheckRemainingSeconds = seconds;
   }

   public void setInitialModelScale(float scale) {
      this.initialModelScale = scale;
   }

   public Vector3d getOldPosition() {
      return this.oldPosition;
   }

   public void playAnimation(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull AnimationSlot animationSlot,
      @Nullable String animationId,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      this.playAnimation(ref, animationSlot, animationId, false, componentAccessor);
   }

   public void playAnimation(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull AnimationSlot animationSlot,
      @Nullable String animationId,
      boolean force,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      Model model = null;
      ModelComponent modelComponent = componentAccessor.getComponent(ref, ModelComponent.getComponentType());
      if (modelComponent != null) {
         model = modelComponent.getModel();
      }

      if (animationSlot != AnimationSlot.Action && animationId != null && model != null && !model.getAnimationSetMap().containsKey(animationId)) {
         Entity.LOGGER.at(Level.WARNING).atMostEvery(1, TimeUnit.MINUTES).log("Missing animation '%s' for Model '%s'", animationId, model.getModelAssetId());
      } else {
         ActiveAnimationComponent activeAnimationComponent = componentAccessor.getComponent(ref, ActiveAnimationComponent.getComponentType());
         if (activeAnimationComponent == null) {
            Entity.LOGGER.at(Level.WARNING).atMostEvery(1, TimeUnit.MINUTES).log("Missing active animation component for entity: %s", this.roleName);
         } else {
            String[] activeAnimations = activeAnimationComponent.getActiveAnimations();
            if (force || animationSlot == AnimationSlot.Action || !Objects.equals(activeAnimations[animationSlot.ordinal()], animationId)) {
               activeAnimations[animationSlot.ordinal()] = animationId;
               activeAnimationComponent.setPlayingAnimation(animationSlot, animationId);
               AnimationUtils.playAnimation(ref, animationSlot, animationId, componentAccessor);
            }
         }
      }
   }

   public void clearDamageData() {
      this.damageData.reset();
   }

   public void setToDespawn() {
      this.isDespawning = true;
   }

   public void setDespawnTime(float time) {
      if (this.isDespawning) {
         this.despawnRemainingSeconds = time;
      }
   }

   public double getDespawnTime() {
      return this.despawnRemainingSeconds;
   }

   public DamageData getDamageData() {
      return this.damageData;
   }

   public boolean getCanCauseDamage(@Nonnull Ref<EntityStore> attackerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      CombatSupport combatSupport = CombatSupport.get(this.reference, componentAccessor);
      return combatSupport.getCanCauseDamage(this.reference, attackerRef, componentAccessor);
   }

   public void onFlockSetState(
      @Nonnull Ref<EntityStore> ref, @Nonnull String state, @Nullable String subState, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      StateSupport stateSupport = StateSupport.get(ref, componentAccessor);
      stateSupport.setState(ref, state, subState, componentAccessor);
   }

   public void onFlockSetTarget(@Nonnull String targetSlot, @Nonnull Ref<EntityStore> target) {
      this.role.setMarkedTarget(this.reference, this.reference.getStore(), targetSlot, target);
   }

   public void saveLeashInformation(@Nonnull Vector3dc position, @Nonnull Rotation3fc rotation) {
      this.leashPoint.set(position);
      this.leashHeading = rotation.yaw();
      this.leashPitch = rotation.pitch();
      this.saveLeashBlockType();
   }

   public void saveLeashBlockType() {
   }

   public boolean requiresLeashPosition() {
      return this.role != null ? this.role.requiresLeashPosition() : this.hasLeashPosition;
   }

   public Vector3d getLeashPoint() {
      return this.leashPoint;
   }

   public void setLeashPoint(@Nonnull Vector3d leashPoint) {
      this.leashPoint.set(leashPoint);
   }

   public float getLeashHeading() {
      return this.leashHeading;
   }

   public void setLeashHeading(float leashHeading) {
      this.leashHeading = leashHeading;
   }

   public float getLeashPitch() {
      return this.leashPitch;
   }

   public void setLeashPitch(float leashPitch) {
      this.leashPitch = leashPitch;
   }

   public float getHoverPhase() {
      return this.hoverPhase;
   }

   public void setHoverPhase(float hoverPhase) {
      this.hoverPhase = hoverPhase;
   }

   public double getHoverHeight() {
      return this.hoverHeight;
   }

   public void setHoverHeight(double hoverHeight) {
      this.hoverHeight = hoverHeight;
   }

   public String getRoleName() {
      return this.roleName;
   }

   public void setRoleName(String roleName) {
      this.roleName = roleName;
   }

   public int getRoleIndex() {
      return this.roleIndex;
   }

   public void setRoleIndex(int roleIndex) {
      this.roleIndex = roleIndex;
   }

   public boolean shouldCollectSensorStats() {
      return this.collectSensorStats;
   }

   public void setCollectSensorStats(boolean collectSensorStats) {
      this.collectSensorStats = collectSensorStats;
   }

   public void setRole(Role role) {
      this.role = role;
   }

   public int getSpawnRoleIndex() {
      return this.spawnRoleIndex != Integer.MIN_VALUE ? this.spawnRoleIndex : this.roleIndex;
   }

   public void setSpawnRoleIndex(int spawnRoleIndex) {
      if (spawnRoleIndex == this.roleIndex) {
         spawnRoleIndex = Integer.MIN_VALUE;
      }

      this.spawnRoleIndex = spawnRoleIndex;
      if (spawnRoleIndex == Integer.MIN_VALUE) {
         this.spawnRoleName = null;
      } else {
         this.spawnRoleName = NPCPlugin.get().getName(spawnRoleIndex);
      }
   }

   @Nullable
   public String getActiveMotionControllerName() {
      return this.activeMotionControllerName;
   }

   public void setActiveMotionControllerName(@Nullable String activeMotionControllerName) {
      this.activeMotionControllerName = activeMotionControllerName;
   }

   public void setEnvironment(int env) {
      this.environmentIndex = env;
   }

   public int getEnvironment() {
      return this.environmentIndex;
   }

   public int getSpawnConfiguration() {
      return this.spawnConfigurationIndex;
   }

   public void setSpawnConfiguration(int spawnConfigurationIndex) {
      if (spawnConfigurationIndex == Integer.MIN_VALUE) {
         this.spawnConfigurationIndex = Integer.MIN_VALUE;
         this.spawnConfigurationName = null;
      } else {
         String name = WorldNPCSpawn.getAssetMap().getAsset(spawnConfigurationIndex).getId();
         if (name == null) {
            throw new IllegalArgumentException("setSpawnConfiguration: Cannot find spawn configuration name for index: " + spawnConfigurationIndex);
         }

         this.spawnConfigurationIndex = spawnConfigurationIndex;
         this.spawnConfigurationName = name;
      }
   }

   public boolean updateSpawnTrackingState(boolean newState) {
      boolean oldState = this.isSpawnTracked;
      this.isSpawnTracked = newState;
      return oldState;
   }

   public boolean isDespawning() {
      return this.isDespawning;
   }

   public boolean isPlayingDespawnAnim() {
      return this.isPlayingDespawnAnim;
   }

   public EnumSet<RoleDebugFlags> getRoleDebugFlags() {
      DebugSupport debugSupport = DebugSupport.get(this.reference, this.reference.getStore());
      return debugSupport.getDebugFlags();
   }

   public void setRoleDebugFlags(@Nonnull EnumSet<RoleDebugFlags> flags) {
      DebugSupport debugSupport = DebugSupport.get(this.reference, this.reference.getStore());
      debugSupport.setDebugFlags(flags);
   }

   public void setSpawnInstant(@Nonnull Instant spawned) {
      this.spawnInstant = spawned;
   }

   public Instant getSpawnInstant() {
      return this.spawnInstant;
   }

   @Deprecated(forRemoval = true)
   public int getLegacyWorldgenId() {
      return this.worldgenId;
   }

   @Nonnull
   public PathManager getPathManager() {
      return this.pathManager;
   }

   public static boolean setAppearance(@Nonnull Ref<EntityStore> ref, @Nonnull String name, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (name.isEmpty()) {
         throw new IllegalArgumentException("Appearance can't be changed to empty");
      } else {
         ModelComponent modelComponent = componentAccessor.getComponent(ref, ModelComponent.getComponentType());
         if (modelComponent == null) {
            return false;
         } else {
            Model model = modelComponent.getModel();
            if (name.equals(model.getModelAssetId())) {
               return true;
            } else {
               NPCEntity npcComponent = componentAccessor.getComponent(ref, getComponentType());
               assert npcComponent != null;
               ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(name);
               if (modelAsset == null) {
                  NPCPlugin.get().getLogger().at(Level.SEVERE).log("Role '%s': Cannot find model '%s'", npcComponent.roleName, name);
                  return false;
               } else {
                  npcComponent.setAppearance(ref, modelAsset, componentAccessor);
                  return true;
               }
            }
         }
      }
   }

   public void setAppearance(@Nonnull Ref<EntityStore> ref, @Nonnull ModelAsset modelAsset, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      Model model = Model.createScaledModel(modelAsset, this.initialModelScale);
      componentAccessor.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
      this.role.updateMotionControllers(ref, model, model.getBoundingBox(), componentAccessor);
   }

   public float getCurrentHorizontalSpeedMultiplier(@Nullable Ref<EntityStore> ref, @Nullable ComponentAccessor<EntityStore> componentAccessor) {
      if (this.cachedEntityHorizontalSpeedMultiplier != Float.MAX_VALUE) {
         return this.cachedEntityHorizontalSpeedMultiplier;
      }

      this.cachedEntityHorizontalSpeedMultiplier = 1.0F;
      if (ref != null && componentAccessor != null) {
         EffectControllerComponent effectControllerComponent = componentAccessor.getComponent(ref, EffectControllerComponent.getComponentType());
         if (effectControllerComponent == null) {
            return this.cachedEntityHorizontalSpeedMultiplier;
         }

         int[] cachedEffectIndexes = effectControllerComponent.getActiveEffectIndexes();
         if (cachedEffectIndexes == null) {
            return this.cachedEntityHorizontalSpeedMultiplier;
         }

         for (int cachedEffectIndex : cachedEffectIndexes) {
            EntityEffect effect = EntityEffect.getAssetMap().getAsset(cachedEffectIndex);
            if (effect != null) {
               ApplicationEffects applicationEffects = effect.getApplicationEffects();
               if (applicationEffects != null) {
                  float multiplier = applicationEffects.getHorizontalSpeedMultiplier();
                  if (multiplier >= 0.0F) {
                     this.cachedEntityHorizontalSpeedMultiplier *= multiplier;
                  }
               }
            }
         }

         return this.cachedEntityHorizontalSpeedMultiplier;
      } else {
         return this.cachedEntityHorizontalSpeedMultiplier;
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return "NPCEntity{role="
         + this.role
         + ", spawnRoleIndex="
         + this.spawnRoleIndex
         + ", spawnPoint="
         + this.leashPoint
         + ", spawnHeading="
         + this.leashHeading
         + ", spawnPitch="
         + this.leashPitch
         + ", environmentIndex='"
         + this.environmentIndex
         + "'} "
         + super.toString();
   }

   @Override
   public String getNPCTypeId() {
      return this.roleName;
   }

   @Override
   public int getNPCTypeIndex() {
      return this.roleIndex;
   }

   public void addReservation(@Nonnull UUID playerUUID) {
      this.reservedBy.add(playerUUID);
   }

   public void removeReservation(@Nonnull UUID playerUUID) {
      this.reservedBy.remove(playerUUID);
   }

   public boolean isReserved() {
      return !this.reservedBy.isEmpty();
   }

   public boolean isReservedBy(@Nonnull UUID playerUUID) {
      return this.reservedBy.contains(playerUUID);
   }
}
