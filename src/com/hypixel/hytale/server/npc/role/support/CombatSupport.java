package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.group.EntityGroup;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.SupportConfigBuilder;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CombatSupport implements Component<EntityStore> {
   public static final String ATTACK_TAG = "Attack";
   public static final int ATTACK_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("Attack");
   public static final String AIMING_REFERENCE_TAG = "AimingReference";
   public static final int AIMING_REFERENCE_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("AimingReference");
   public static final String MELEE_TAG = "Attack=Melee";
   public static final int MELEE_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("Attack=Melee");
   public static final String RANGED_TAG = "Attack=Ranged";
   public static final int RANGED_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("Attack=Ranged");
   public static final String BLOCK_TAG = "Attack=Block";
   public static final int BLOCK_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("Attack=Block");
   protected final boolean disableDamageFlock;
   protected final int[] disableDamageGroups;
   private int roleIndex = -1;
   @Nullable
   protected InteractionChain activeAttack;
   protected boolean dealFriendlyDamage;
   protected double attackPause;
   protected final List<String> attackOverrides = new ObjectArrayList<>();
   protected int attackOverrideIndex = -1;

   @Nonnull
   public static ComponentType<EntityStore, CombatSupport> getComponentType() {
      return NPCPlugin.get().getCombatSupportComponentType();
   }

   @Nonnull
   public static CombatSupport get(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      CombatSupport support = accessor.getComponent(ref, getComponentType());
      assert support != null : "Missing CombatSupport on entity " + ref;
      return support;
   }

   public CombatSupport(@Nonnull SupportConfigBuilder<?> builder, @Nonnull BuilderSupport support) {
      this.disableDamageFlock = builder.isDisableDamageFlock();
      this.disableDamageGroups = builder.getDisableDamageGroups(support);
   }

   public void setRoleIndex(int roleIndex) {
      this.roleIndex = roleIndex;
   }

   public boolean isDealingFriendlyDamage() {
      return this.dealFriendlyDamage;
   }

   public int[] getDisableDamageGroups() {
      return this.disableDamageGroups;
   }

   public boolean isExecutingAttack() {
      return this.attackPause > 0.0 || this.activeAttack != null;
   }

   public void tick(double dt) {
      if (this.attackPause > 0.0) {
         this.attackPause -= dt;
      }

      if (this.activeAttack != null && this.activeAttack.getServerState() != InteractionState.NotFinished) {
         this.activeAttack = null;
      }
   }

   public boolean getCanCauseDamage(
      @Nonnull Ref<EntityStore> selfRef, @Nonnull Ref<EntityStore> attackerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.disableDamageFlock) {
         Ref<EntityStore> flockReference = FlockPlugin.getFlockReference(selfRef, componentAccessor.getExternalData().getStore());
         if (flockReference != null) {
            EntityGroup entityGroupComponent = componentAccessor.getComponent(flockReference, EntityGroup.getComponentType());
            if (entityGroupComponent != null && entityGroupComponent.isMember(attackerRef)) {
               return false;
            }
         }
      }

      boolean friendlyDamage = false;
      NPCEntity npcComponent = componentAccessor.getComponent(attackerRef, NPCEntity.getComponentType());
      if (npcComponent != null) {
         friendlyDamage = get(attackerRef, componentAccessor).isDealingFriendlyDamage();
      }

      return friendlyDamage || !WorldSupport.isGroupMember(this.roleIndex, attackerRef, this.disableDamageGroups, componentAccessor);
   }

   public void setExecutingAttack(InteractionChain chain, boolean damageFriendlies, double attackPause) {
      this.activeAttack = chain;
      this.dealFriendlyDamage = damageFriendlies;
      this.attackPause = attackPause;
   }

   public void addAttackOverride(String attackSequence) {
      this.attackOverrides.add(attackSequence);
      this.attackOverrideIndex = 0;
   }

   public void clearAttackOverrides() {
      this.attackOverrides.clear();
      this.attackOverrideIndex = -1;
   }

   @Nullable
   public String getNextAttackOverride() {
      if (this.attackOverrideIndex == -1) {
         return null;
      }

      int index = this.attackOverrideIndex;
      this.attackOverrideIndex = this.attackOverrideIndex < this.attackOverrides.size() - 1 ? this.attackOverrideIndex + 1 : 0;
      return this.attackOverrides.get(index);
   }

   @Override
   public Component<EntityStore> clone() {
      return this;
   }
}
