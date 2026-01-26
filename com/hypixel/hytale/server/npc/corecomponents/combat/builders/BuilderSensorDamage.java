/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.combat.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNullOrNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.SensorDamage;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorDamage
extends BuilderSensorBase {
    public static final String[] REQUIRE_ONE_OF = new String[]{"Combat", "Drowning", "Environment", "Other"};
    public static final String[] ANTECEDENT = new String[]{"TargetSlot"};
    public static final String[] SUBSEQUENT = new String[]{"Drowning", "Environment", "Other"};
    protected boolean combatDamage;
    protected boolean friendlyDamage;
    protected boolean drowningDamage;
    protected boolean environmentDamage;
    protected boolean otherDamage;
    protected String targetSlot;

    @Override
    @Nonnull
    public SensorDamage build(@Nonnull BuilderSupport builderSupport) {
        return new SensorDamage(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Test if NPC suffered damage";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Test if NPC suffered damage. A position is only returned when NPC suffered combat damage.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.getBoolean(data, "Combat", (boolean v) -> {
            this.combatDamage = v;
        }, true, BuilderDescriptorState.Stable, "Test for combat damage", null);
        this.getBoolean(data, "Friendly", (boolean v) -> {
            this.friendlyDamage = v;
        }, false, BuilderDescriptorState.Stable, "Test for damage from usually disabled damage groups", null);
        this.getBoolean(data, "Drowning", (boolean v) -> {
            this.drowningDamage = v;
        }, false, BuilderDescriptorState.Stable, "Test for damage from drowning", null);
        this.getBoolean(data, "Environment", (boolean v) -> {
            this.environmentDamage = v;
        }, false, BuilderDescriptorState.Stable, "Test for damage from environment", null);
        this.getBoolean(data, "Other", (boolean v) -> {
            this.otherDamage = v;
        }, false, BuilderDescriptorState.Stable, "Test for other damage", null);
        this.getString(data, "TargetSlot", (String v) -> {
            this.targetSlot = v;
        }, null, (StringValidator)StringNullOrNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The slot to use for locking on the target if damage is taken. If omitted, target will not be locked", null);
        this.validateAny(REQUIRE_ONE_OF, new boolean[]{this.combatDamage, this.drowningDamage, this.environmentDamage, this.otherDamage});
        this.validateBooleanImplicationAnyAntecedent(ANTECEDENT, new boolean[]{this.targetSlot != null}, true, SUBSEQUENT, new boolean[]{this.drowningDamage, this.environmentDamage, this.otherDamage}, false);
        this.provideFeature(Feature.AnyEntity);
        return this;
    }

    public boolean isCombatDamage() {
        return this.combatDamage;
    }

    public boolean isFriendlyDamage() {
        return this.friendlyDamage;
    }

    public boolean isDrowningDamage() {
        return this.drowningDamage;
    }

    public boolean isEnvironmentDamage() {
        return this.environmentDamage;
    }

    public boolean isOtherDamage() {
        return this.otherDamage;
    }

    public int getTargetSlot(@Nonnull BuilderSupport support) {
        if (this.targetSlot == null) {
            return Integer.MIN_VALUE;
        }
        return support.getTargetSlot(this.targetSlot);
    }
}

