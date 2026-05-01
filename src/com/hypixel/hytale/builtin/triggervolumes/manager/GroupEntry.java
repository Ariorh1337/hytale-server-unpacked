package com.hypixel.hytale.builtin.triggervolumes.manager;

import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class GroupEntry {
   @Nonnull
   public static final BuilderCodec<GroupEntry> CODEC = BuilderCodec.builder(GroupEntry.class, GroupEntry::new)
      .append(new KeyedCodec<>("Origin", Vector3dUtil.CODEC), (g, o) -> g.origin = o, g -> g.origin)
      .add()
      .append(
         new KeyedCodec<>("Effects", new ArrayCodec<>(TriggerEffect.CODEC, TriggerEffect[]::new), false),
         (g, effects) -> Collections.addAll(g.effects, effects),
         g -> g.effects.isEmpty() ? null : g.effects.toArray(TriggerEffect[]::new)
      )
      .add()
      .append(new KeyedCodec<>("TargetTypes", new ArrayCodec<>(new EnumCodec<>(EntityTargetType.class), EntityTargetType[]::new), false), (g, arr) -> {
         g.targetTypes.clear();
         Collections.addAll(g.targetTypes, arr);
      }, g -> g.targetTypes.isEmpty() ? null : g.targetTypes.toArray(EntityTargetType[]::new))
      .add()
      .append(new KeyedCodec<>("Enabled", Codec.BOOLEAN, false), (g, b) -> g.enabled = b, g -> g.enabled)
      .add()
      .append(new KeyedCodec<>("Color", Codec.INTEGER, false), (g, c) -> g.color = c, g -> g.color)
      .add()
      .append(
         new KeyedCodec<>("Tags", new MapCodec<>(Codec.STRING_ARRAY, HashMap::new, false), false),
         (g, tags) -> g.rawTags = tags,
         g -> g.rawTags.isEmpty() ? null : g.rawTags
      )
      .add()
      .build();
   @Nonnull
   private String id = "";
   @Nonnull
   private String worldName = "";
   @Nonnull
   private Vector3d origin = new Vector3d();
   @Nonnull
   private final List<TriggerEffect> effects;
   @Nonnull
   private final Set<EntityTargetType> targetTypes;
   private boolean enabled = true;
   private int color;
   @Nonnull
   private Map<String, String[]> rawTags = Collections.emptyMap();
   @Nonnull
   private final Set<String> memberVolumeIds = new LinkedHashSet<>();

   GroupEntry() {
      this.effects = new ArrayList<>();
      this.targetTypes = EnumSet.of(EntityTargetType.PLAYER);
   }

   public GroupEntry(
      @Nonnull String id,
      @Nonnull String worldName,
      @Nonnull Vector3d origin,
      @Nonnull List<TriggerEffect> effects,
      @Nonnull Set<EntityTargetType> targetTypes,
      boolean enabled,
      int color
   ) {
      this.id = id;
      this.worldName = worldName;
      this.origin = origin;
      this.effects = effects;
      this.targetTypes = targetTypes;
      this.enabled = enabled;
      this.color = color;
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
   public Vector3d getOrigin() {
      return this.origin;
   }

   public void setOrigin(@Nonnull Vector3d origin) {
      this.origin = origin;
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

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   @Nonnull
   public Set<String> getMemberVolumeIds() {
      return this.memberVolumeIds;
   }

   public void addMember(@Nonnull String volumeId) {
      this.memberVolumeIds.add(volumeId);
   }

   public void removeMember(@Nonnull String volumeId) {
      this.memberVolumeIds.remove(volumeId);
   }

   @Nonnull
   public Map<String, String[]> getRawTags() {
      return this.rawTags;
   }

   public void setTags(@Nonnull Map<String, String[]> tags) {
      this.rawTags = tags;
   }
}
