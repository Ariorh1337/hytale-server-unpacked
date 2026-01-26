/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.CombatInteractionValidator;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterCombat;
import javax.annotation.Nonnull;

public class BuilderEntityFilterCombat
extends BuilderEntityFilterBase {
    public static final double MAX_ELAPSED_TIME = 3.4028234663852886E38;
    public static final double[] DEFAULT_TIME_ELAPSED_RANGE = new double[]{0.0, 3.4028234663852886E38};
    protected final AssetHolder sequence = new AssetHolder();
    protected final NumberArrayHolder elapsedTimeRange = new NumberArrayHolder();
    protected final EnumHolder<EntityFilterCombat.Mode> mode = new EnumHolder();

    @Override
    @Nonnull
    public EntityFilterCombat build(@Nonnull BuilderSupport builderSupport) {
        return new EntityFilterCombat(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Check the target's combat state";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Check the target's combat state. This includes whether they're attacking at all, if they're using a particular attack, etc";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.getAsset(data, "Sequence", this.sequence, null, (AssetValidator)CombatInteractionValidator.withConfig(AssetValidator.CanBeEmpty), BuilderDescriptorState.Stable, "The attack ID to check for.", null);
        this.getDoubleRange(data, "TimeElapsedRange", this.elapsedTimeRange, DEFAULT_TIME_ELAPSED_RANGE, (DoubleArrayValidator)DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, 3.4028234663852886E38), BuilderDescriptorState.Stable, "The acceptable elapsed time range in seconds.", null);
        this.getEnum(data, "Mode", this.mode, EntityFilterCombat.Mode.class, EntityFilterCombat.Mode.Attacking, BuilderDescriptorState.Stable, "Type of combat to trigger on", null);
        this.validateAssetIfEnumIs(this.sequence, CombatInteractionValidator.required(), this.mode, EntityFilterCombat.Mode.Sequence);
        return this;
    }

    public String getSequence(@Nonnull BuilderSupport builderSupport) {
        return this.sequence.get(builderSupport.getExecutionContext());
    }

    public EntityFilterCombat.Mode getCombatMode(@Nonnull BuilderSupport builderSupport) {
        return this.mode.get(builderSupport.getExecutionContext());
    }

    public double[] getTimeElapsedRange(@Nonnull BuilderSupport builderSupport) {
        return this.elapsedTimeRange.get(builderSupport.getExecutionContext());
    }
}

