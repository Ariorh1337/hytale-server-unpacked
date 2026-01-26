/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments.AssignmentsAsset;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ImportedAssignmentsAsset
extends AssignmentsAsset {
    public static final BuilderCodec<ImportedAssignmentsAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ImportedAssignmentsAsset.class, ImportedAssignmentsAsset::new, AssignmentsAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Name", Codec.STRING, true), (asset, v) -> {
        asset.name = v;
    }, asset -> asset.name).add()).build();
    private String name = "";

    @Override
    public Assignments build(@Nonnull AssignmentsAsset.Argument argument) {
        if (super.skip()) {
            return Assignments.noPropDistribution(argument.runtime);
        }
        AssignmentsAsset asset = ImportedAssignmentsAsset.getExportedAsset(this.name);
        if (asset == null) {
            LoggerUtil.getLogger().warning("Couldn't find Assignments asset exported with name: '" + this.name + "'.");
            return Assignments.noPropDistribution(argument.runtime);
        }
        return asset.build(argument);
    }
}

