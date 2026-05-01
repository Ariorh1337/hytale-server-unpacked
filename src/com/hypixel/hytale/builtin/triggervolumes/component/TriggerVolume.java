package com.hypixel.hytale.builtin.triggervolumes.component;

import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.manager.CooldownMode;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.BoxShape;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.math.vector.Vector3fUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class TriggerVolume implements Component<EntityStore> {
   @Nonnull
   public static final BuilderCodec<TriggerVolume> CODEC = BuilderCodec.builder(TriggerVolume.class, TriggerVolume::new)
      .append(new KeyedCodec<>("Shape", TriggerVolumeShape.CODEC), (c, v) -> c.shape = v, c -> c.shape)
      .add()
      .append(
         new KeyedCodec<>("Effects", new ArrayCodec<>(TriggerEffect.CODEC, TriggerEffect[]::new), false),
         (c, v) -> c.effects = v != null ? List.of(v) : null,
         c -> c.effects != null ? c.effects.toArray(TriggerEffect[]::new) : null
      )
      .add()
      .append(new KeyedCodec<>("Enabled", Codec.BOOLEAN, false), (c, v) -> c.enabled = v, c -> c.enabled)
      .add()
      .append(new KeyedCodec<>("TargetTypes", new ArrayCodec<>(new EnumCodec<>(EntityTargetType.class), EntityTargetType[]::new), false), (c, arr) -> {
         c.targetTypes.clear();
         Collections.addAll(c.targetTypes, arr);
      }, c -> c.targetTypes.isEmpty() ? null : c.targetTypes.toArray(EntityTargetType[]::new))
      .add()
      .append(new KeyedCodec<>("EffectAsset", Codec.STRING, false), (c, v) -> c.effectAssetRef = v, c -> c.effectAssetRef)
      .add()
      .append(
         new KeyedCodec<>("Tags", new MapCodec<>(Codec.STRING_ARRAY, HashMap::new, false), false),
         (c, v) -> c.rawTags = v,
         c -> c.rawTags.isEmpty() ? null : c.rawTags
      )
      .add()
      .append(new KeyedCodec<>("KeepLoaded", Codec.BOOLEAN, false), (c, v) -> c.keepLoaded = v, c -> c.keepLoaded)
      .add()
      .append(new KeyedCodec<>("Color", Vector3fUtil.CODEC, false), (c, v) -> c.color = v, c -> c.color)
      .add()
      .append(
         new KeyedCodec<>("ActivationDelay", Codec.FLOAT, false), (c, v) -> c.activationDelay = v, c -> c.activationDelay > 0.0F ? c.activationDelay : null
      )
      .add()
      .append(new KeyedCodec<>("Cooldown", Codec.FLOAT, false), (c, v) -> c.cooldown = v, c -> c.cooldown > 0.0F ? c.cooldown : null)
      .add()
      .append(new KeyedCodec<>("CooldownMode", Codec.STRING, false), (c, v) -> {
         try {
            c.cooldownMode = CooldownMode.valueOf(v.toUpperCase());
         } catch (IllegalArgumentException var3) {
         }
      }, c -> c.cooldownMode != CooldownMode.PER_ENTITY ? c.cooldownMode.name() : null)
      .add()
      .append(new KeyedCodec<>("GroupLinkId", Codec.STRING, false), (c, v) -> c.groupLinkId = v, c -> c.groupLinkId)
      .add()
      .build();
   @Nullable
   private TriggerVolumeShape shape;
   @Nullable
   private List<TriggerEffect> effects;
   private boolean enabled = true;
   @Nonnull
   private Set<EntityTargetType> targetTypes = EnumSet.of(EntityTargetType.PLAYER);
   @Nullable
   private String effectAssetRef;
   @Nonnull
   private Map<String, String[]> rawTags = Collections.emptyMap();
   private boolean keepLoaded;
   @Nullable
   private Vector3f color;
   private float activationDelay;
   private float cooldown;
   @Nonnull
   private CooldownMode cooldownMode = CooldownMode.PER_ENTITY;
   @Nullable
   private String groupLinkId;

   @Nonnull
   public static TriggerVolume fromVolumeEntry(@Nonnull VolumeEntry entry) {
      TriggerVolume tv = new TriggerVolume();
      tv.shape = entry.getShape();
      tv.effects = entry.getEffects().isEmpty() ? null : new ArrayList<>(entry.getEffects());
      tv.enabled = entry.isEnabled();
      tv.targetTypes = EnumSet.copyOf(entry.getTargetTypes());
      tv.effectAssetRef = entry.getEffectAssetRef();
      tv.rawTags = entry.getRawTags();
      tv.keepLoaded = entry.isKeepLoaded();
      tv.color = entry.getColor();
      tv.activationDelay = entry.getActivationDelay();
      tv.cooldown = entry.getCooldown();
      tv.cooldownMode = entry.getCooldownMode();
      tv.groupLinkId = null;
      return tv;
   }

   @Nonnull
   public VolumeEntry toVolumeEntry(@Nonnull String id, @Nonnull String worldName, @Nonnull Vector3d position) {
      VolumeEntry entry = new VolumeEntry(
         id,
         worldName,
         position,
         this.shape != null ? this.shape : new BoxShape(),
         this.effects != null ? new ArrayList<>(this.effects) : new ArrayList<>(),
         EnumSet.copyOf(this.targetTypes),
         this.enabled
      );
      entry.setEffectAssetRef(this.effectAssetRef);
      entry.setKeepLoaded(this.keepLoaded);
      if (this.color != null) {
         entry.setColor(new Vector3f(this.color));
      }

      entry.setActivationDelay(this.activationDelay);
      entry.setCooldown(this.cooldown);
      entry.setCooldownMode(this.cooldownMode);
      if (!this.rawTags.isEmpty()) {
         entry.setTags(this.rawTags);
      }

      return entry;
   }

   @Nullable
   public String getGroupLinkId() {
      return this.groupLinkId;
   }

   public void setGroupLinkId(@Nullable String groupLinkId) {
      this.groupLinkId = groupLinkId;
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      TriggerVolume clone = new TriggerVolume();
      clone.shape = this.shape;
      clone.effects = this.effects != null ? new ArrayList<>(this.effects) : null;
      clone.enabled = this.enabled;
      clone.targetTypes = EnumSet.copyOf(this.targetTypes);
      clone.effectAssetRef = this.effectAssetRef;
      clone.rawTags = this.rawTags;
      clone.keepLoaded = this.keepLoaded;
      clone.color = this.color != null ? new Vector3f(this.color) : null;
      clone.activationDelay = this.activationDelay;
      clone.cooldown = this.cooldown;
      clone.cooldownMode = this.cooldownMode;
      clone.groupLinkId = this.groupLinkId;
      return clone;
   }

   @Nullable
   public TriggerVolumeShape getShape() {
      return this.shape;
   }

   @Nullable
   public List<TriggerEffect> getEffects() {
      return this.effects;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
