package com.hypixel.hytale.builtin.triggervolumes.effect.builtin.conditions;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerCondition;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerVolumeEntityQuery;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class EntityCountCondition extends TriggerCondition {
   @Nonnull
   public static final BuilderCodec<EntityCountCondition> CODEC = BuilderCodec.builder(EntityCountCondition.class, EntityCountCondition::new, BASE_CODEC)
      .append(
         new KeyedCodec<>("Comparison", new EnumCodec<>(EntityCountCondition.Comparison.class), false),
         (condition, comparison) -> condition.comparison = comparison,
         condition -> condition.comparison
      )
      .add()
      .append(new KeyedCodec<>("Count", Codec.INTEGER, false), (condition, count) -> condition.count = count, condition -> condition.count)
      .add()
      .append(
         new KeyedCodec<>("EntityType", Codec.STRING_ARRAY, false),
         (condition, entityTypes) -> condition.entityTypes = entityTypes != null ? entityTypes : new String[0],
         condition -> condition.entityTypes.length == 0 ? null : condition.entityTypes
      )
      .add()
      .build();
   @Nonnull
   private EntityCountCondition.Comparison comparison = EntityCountCondition.Comparison.AT_LEAST;
   private int count = 1;
   @Nonnull
   private String[] entityTypes = new String[0];

   @Nonnull
   public static EntityCountCondition create(@Nonnull TriggerEventType eventType, @Nonnull EntityCountCondition.Comparison comparison, int count) {
      EntityCountCondition condition = new EntityCountCondition();
      condition.setEventType(eventType);
      condition.comparison = comparison;
      condition.count = count;
      return condition;
   }

   @Override
   public boolean test(@Nonnull TriggerContext context) {
      return this.matches(this.countEntities(context));
   }

   public int countEntities(@Nonnull TriggerContext context) {
      return TriggerVolumeEntityQuery.collectLivingNpcs(context.getStore(), context.getSpatialVolumes(), Arrays.asList(this.entityTypes)).size();
   }

   public boolean matches(int entityCount) {
      return switch (this.comparison) {
         case AT_LEAST -> entityCount >= this.count;
         case AT_MOST -> entityCount <= this.count;
         case EXACTLY -> entityCount == this.count;
         case MORE_THAN -> entityCount > this.count;
         case LESS_THAN -> entityCount < this.count;
      };
   }

   public enum Comparison {
      AT_LEAST,
      AT_MOST,
      EXACTLY,
      MORE_THAN,
      LESS_THAN;
   }
}
