package com.hypixel.hytale.builtin.triggervolumes.manager;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.shape.BoxShape;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.math.vector.Vector3fUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.IntFunction;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class VolumeEntry {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final ArrayCodec<TriggerEffect> TOLERANT_EFFECTS_CODEC = new ArrayCodec<TriggerEffect>(TriggerEffect.CODEC, TriggerEffect[]::new) {
      @Nullable
      protected TriggerEffect decodeJsonElement(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
         try {
            return (TriggerEffect)super.decodeJsonElement(reader, extraInfo);
         } catch (Exception e) {
            reader.reset();
            reader.skipValue();
            VolumeEntry.LOGGER.at(Level.WARNING).log("Skipping unrecognized trigger effect: %s", e.getMessage());
            return null;
         }
      }

      @Nullable
      protected TriggerEffect decodeElement(@Nonnull BsonValue value, ExtraInfo extraInfo) {
         try {
            return (TriggerEffect)super.decodeElement(value, extraInfo);
         } catch (Exception e) {
            VolumeEntry.LOGGER.at(Level.WARNING).log("Skipping unrecognized trigger effect: %s", e.getMessage());
            return null;
         }
      }
   };
   @Nonnull
   public static final BuilderCodec<VolumeEntry> CODEC = BuilderCodec.builder(VolumeEntry.class, VolumeEntry::new)
      .append(new KeyedCodec<>("Position", Vector3dUtil.CODEC), (v, pos) -> v.position = pos, v -> v.position)
      .add()
      .append(new KeyedCodec<>("Shape", TriggerVolumeShape.CODEC), (v, s) -> v.shape = s, v -> v.shape)
      .add()
      .append(new KeyedCodec<>("EffectAsset", Codec.STRING, false), (v, ref) -> v.effectAssetRef = ref, v -> v.effectAssetRef)
      .add()
      .append(new KeyedCodec<>("Effects", TOLERANT_EFFECTS_CODEC, false), (v, effects) -> {
         for (TriggerEffect e : effects) {
            if (e != null) {
               v.effects.add(e);
            }
         }
      }, v -> v.effectAssetRef == null && !v.effects.isEmpty() ? v.effects.toArray(TriggerEffect[]::new) : null)
      .add()
      .append(new KeyedCodec<>("TargetTypes", new ArrayCodec<>(new EnumCodec<>(EntityTargetType.class), EntityTargetType[]::new), false), (v, arr) -> {
         v.targetTypes.clear();
         Collections.addAll(v.targetTypes, arr);
      }, v -> v.targetTypes.isEmpty() ? null : v.targetTypes.toArray(EntityTargetType[]::new))
      .add()
      .append(new KeyedCodec<>("Enabled", Codec.BOOLEAN, false), (v, b) -> v.enabled = b, v -> v.enabled)
      .add()
      .append(new KeyedCodec<>("GroupId", Codec.STRING, false), (v, g) -> v.groupId = g, v -> v.groupId)
      .add()
      .append(new KeyedCodec<>("Color", Vector3fUtil.CODEC, false), (v, c) -> v.color = c, v -> v.color)
      .add()
      .append(new KeyedCodec<>("KeepLoaded", Codec.BOOLEAN, false), (v, b) -> v.keepLoaded = b, v -> v.keepLoaded)
      .add()
      .append(
         new KeyedCodec<>("ActivationDelay", Codec.FLOAT, false), (v, d) -> v.activationDelay = d, v -> v.activationDelay > 0.0F ? v.activationDelay : null
      )
      .add()
      .append(new KeyedCodec<>("Cooldown", Codec.FLOAT, false), (v, c) -> v.cooldown = c, v -> v.cooldown > 0.0F ? v.cooldown : null)
      .add()
      .append(new KeyedCodec<>("CooldownMode", Codec.STRING, false), (v, s) -> {
         try {
            v.cooldownMode = CooldownMode.valueOf(s.toUpperCase());
         } catch (IllegalArgumentException var3) {
         }
      }, v -> v.cooldownMode != CooldownMode.PER_ENTITY ? v.cooldownMode.name() : null)
      .add()
      .append(
         new KeyedCodec<>("Tags", new MapCodec<>(Codec.STRING_ARRAY, HashMap::new, false), false),
         (v, tags) -> v.rawTags = tags,
         v -> v.rawTags.isEmpty() ? null : v.rawTags
      )
      .add()
      .build();
   @Nonnull
   private String id = "";
   @Nonnull
   private String worldName = "";
   @Nonnull
   private Vector3d position = new Vector3d();
   @Nonnull
   private TriggerVolumeShape shape = new BoxShape();
   @Nonnull
   private final List<TriggerEffect> effects;
   @Nonnull
   private final Set<EntityTargetType> targetTypes;
   private boolean enabled = true;
   private boolean keepLoaded;
   private float activationDelay = 0.0F;
   private float cooldown = 0.0F;
   @Nonnull
   private CooldownMode cooldownMode = CooldownMode.PER_ENTITY;
   private transient boolean pendingDestroy;
   private transient long lastGlobalActivationNanos;
   private final transient Map<UUID, Long> lastEntityActivationNanos = new HashMap<>();
   @Nullable
   private String effectAssetRef;
   @Nullable
   private String groupId;
   @Nullable
   private Vector3f color;
   @Nonnull
   private Map<String, String[]> rawTags = Collections.emptyMap();
   @Nonnull
   private IntSet expandedTagIndexes = IntSets.EMPTY_SET;
   private final Map<UUID, Ref<EntityStore>> trackedEntities = new HashMap<>();
   private final Map<VolumeEntry.EffectEntityKey, Long> lastFireTimes = new HashMap<>();

   VolumeEntry() {
      this.effects = new ArrayList<>();
      this.targetTypes = EnumSet.of(EntityTargetType.PLAYER);
   }

   public VolumeEntry(
      @Nonnull String id,
      @Nonnull String worldName,
      @Nonnull Vector3d position,
      @Nonnull TriggerVolumeShape shape,
      @Nonnull List<TriggerEffect> effects,
      @Nonnull Set<EntityTargetType> targetTypes,
      boolean enabled
   ) {
      this.id = id;
      this.worldName = worldName;
      this.position = position;
      this.shape = shape;
      this.effects = effects;
      this.targetTypes = targetTypes;
      this.enabled = enabled;
   }

   public void setId(@Nonnull String id) {
      this.id = id;
   }

   public void setWorldName(@Nonnull String worldName) {
      this.worldName = worldName;
   }

   @Nonnull
   public String getId() {
      return this.id;
   }

   @Nonnull
   public String getWorldName() {
      return this.worldName;
   }

   @Nonnull
   public Vector3d getPosition() {
      return this.position;
   }

   public void setPosition(@Nonnull Vector3d position) {
      this.position = position;
   }

   @Nonnull
   public TriggerVolumeShape getShape() {
      return this.shape;
   }

   public void setShape(@Nonnull TriggerVolumeShape shape) {
      this.shape = shape;
   }

   @Nonnull
   public List<TriggerEffect> getEffects() {
      return this.effects;
   }

   @Nonnull
   public Set<EntityTargetType> getTargetTypes() {
      return this.targetTypes;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isKeepLoaded() {
      return this.keepLoaded;
   }

   public void setKeepLoaded(boolean keepLoaded) {
      this.keepLoaded = keepLoaded;
   }

   public float getActivationDelay() {
      return this.activationDelay;
   }

   public void setActivationDelay(float activationDelay) {
      this.activationDelay = activationDelay;
   }

   public float getCooldown() {
      return this.cooldown;
   }

   public void setCooldown(float cooldown) {
      this.cooldown = cooldown;
   }

   @Nonnull
   public CooldownMode getCooldownMode() {
      return this.cooldownMode;
   }

   public void setCooldownMode(@Nonnull CooldownMode cooldownMode) {
      this.cooldownMode = cooldownMode;
   }

   public boolean isOnCooldown(@Nonnull UUID entityUuid, long nowNanos) {
      if (this.cooldown <= 0.0F) {
         return false;
      }

      long cooldownNanos = (long)(this.cooldown * 1.0E9F);
      if (this.cooldownMode != CooldownMode.TOTAL) {
         Long last = this.lastEntityActivationNanos.get(entityUuid);
         if (last == null) {
            return false;
         } else if (nowNanos - last >= cooldownNanos) {
            this.lastEntityActivationNanos.remove(entityUuid);
            return false;
         } else {
            return true;
         }
      } else {
         return this.lastGlobalActivationNanos != 0L && nowNanos - this.lastGlobalActivationNanos < cooldownNanos;
      }
   }

   public void recordActivation(@Nonnull UUID entityUuid, long nowNanos) {
      if (!(this.cooldown <= 0.0F)) {
         if (this.cooldownMode == CooldownMode.TOTAL) {
            this.lastGlobalActivationNanos = nowNanos;
         } else {
            this.lastEntityActivationNanos.put(entityUuid, nowNanos);
         }
      }
   }

   public boolean isPendingDestroy() {
      return this.pendingDestroy;
   }

   public void markPendingDestroy() {
      this.pendingDestroy = true;
   }

   @Nullable
   public String getEffectAssetRef() {
      return this.effectAssetRef;
   }

   public void setEffectAssetRef(@Nullable String effectAssetRef) {
      this.effectAssetRef = effectAssetRef;
   }

   @Nullable
   public String getGroupId() {
      return this.groupId;
   }

   public void setGroupId(@Nullable String groupId) {
      this.groupId = groupId;
   }

   @Nullable
   public Vector3f getColor() {
      return this.color;
   }

   public void setColor(@Nullable Vector3f color) {
      this.color = color;
   }

   @Nonnull
   public Map<String, String[]> getRawTags() {
      return this.rawTags;
   }

   public void setTags(@Nonnull Map<String, String[]> tags) {
      this.rawTags = tags;
      this.expandTags();
   }

   @Nonnull
   public IntSet getExpandedTagIndexes() {
      return this.expandedTagIndexes;
   }

   public void setExpandedTagIndexes(@Nonnull IntSet expandedTagIndexes) {
      this.expandedTagIndexes = expandedTagIndexes;
   }

   public boolean hasTag(int tagIndex) {
      return this.expandedTagIndexes.contains(tagIndex);
   }

   void expandTags() {
      if (this.rawTags.isEmpty()) {
         this.expandedTagIndexes = IntSets.EMPTY_SET;
      } else {
         IntOpenHashSet indexes = new IntOpenHashSet();

         for (Entry<String, String[]> entry : this.rawTags.entrySet()) {
            String tag = entry.getKey();
            indexes.add(AssetRegistry.getOrCreateTagIndex(tag));

            for (String value : entry.getValue()) {
               indexes.add(AssetRegistry.getOrCreateTagIndex(value));
               indexes.add(AssetRegistry.getOrCreateTagIndex(tag + "=" + value));
            }
         }

         this.expandedTagIndexes = IntSets.unmodifiable(indexes);
      }
   }

   @Nonnull
   public Map<UUID, Ref<EntityStore>> getTrackedEntities() {
      return this.trackedEntities;
   }

   @Nonnull
   public Map<VolumeEntry.EffectEntityKey, Long> getLastFireTimes() {
      return this.lastFireTimes;
   }

   public record EffectEntityKey(int effectIndex, @Nonnull UUID entityId) {
   }
}
