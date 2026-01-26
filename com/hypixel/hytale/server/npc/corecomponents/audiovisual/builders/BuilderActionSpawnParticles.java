/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.audiovisual.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.ParticleSystemExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.audiovisual.ActionSpawnParticles;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import javax.annotation.Nonnull;

public class BuilderActionSpawnParticles
extends BuilderActionBase {
    protected final AssetHolder particleSystem = new AssetHolder();
    protected final DoubleHolder range = new DoubleHolder();
    protected final NumberArrayHolder offset = new NumberArrayHolder();

    @Override
    @Nonnull
    public ActionSpawnParticles build(@Nonnull BuilderSupport builderSupport) {
        return new ActionSpawnParticles(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Spawn particle system visible within a given range with an offset relative to npc heading";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Nonnull
    public BuilderActionSpawnParticles readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "ParticleSystem", this.particleSystem, (AssetValidator)ParticleSystemExistsValidator.required(), BuilderDescriptorState.Stable, "Particle system to spawn", null);
        this.getDouble(data, "Range", this.range, 75.0, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "Maximum visibility range", null);
        this.getVector3d(data, "Offset", this.offset, null, null, BuilderDescriptorState.Stable, "Offset relative to footpoint in view direction of NPC", null);
        return this;
    }

    public String getParticleSystem(@Nonnull BuilderSupport support) {
        return this.particleSystem.get(support.getExecutionContext());
    }

    public double getRange(BuilderSupport support) {
        return this.range.get(support.getExecutionContext());
    }

    public Vector3d getOffset(BuilderSupport support) {
        return BuilderActionSpawnParticles.createVector3d(this.offset.get(support.getExecutionContext()), Vector3d.ZERO::clone);
    }
}

