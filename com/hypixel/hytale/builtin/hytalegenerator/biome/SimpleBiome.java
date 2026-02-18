/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.biome;

import com.hypixel.hytale.builtin.hytalegenerator.PropField;
import com.hypixel.hytale.builtin.hytalegenerator.biome.Biome;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class SimpleBiome
implements Biome {
    @Nonnull
    private final Density terrainDensity;
    @Nonnull
    private final MaterialProvider<Material> materialProvider;
    @Nonnull
    private final List<PropField> propFields;
    @Nonnull
    private final EnvironmentProvider environmentProvider;
    @Nonnull
    private final TintProvider tintProvider;
    @Nonnull
    private final String biomeName;

    public SimpleBiome(@Nonnull String biomeName, @Nonnull Density terrainDensity, @Nonnull MaterialProvider<Material> materialProvider, @Nonnull EnvironmentProvider environmentProvider, @Nonnull TintProvider tintProvider) {
        this.terrainDensity = terrainDensity;
        this.materialProvider = materialProvider;
        this.biomeName = biomeName;
        this.propFields = new ArrayList<PropField>();
        this.environmentProvider = environmentProvider;
        this.tintProvider = tintProvider;
    }

    public void addPropFieldTo(@Nonnull PropField propField) {
        this.propFields.add(propField);
    }

    @Override
    @Nonnull
    public MaterialProvider<Material> getMaterialProvider() {
        return this.materialProvider;
    }

    @Override
    @Nonnull
    public Density getTerrainDensity() {
        return this.terrainDensity;
    }

    @Override
    @Nonnull
    public String getBiomeName() {
        return this.biomeName;
    }

    @Override
    @Nonnull
    public List<PropField> getPropFields() {
        return this.propFields;
    }

    @Override
    @Nonnull
    public EnvironmentProvider getEnvironmentProvider() {
        return this.environmentProvider;
    }

    @Override
    @Nonnull
    public TintProvider getTintProvider() {
        return this.tintProvider;
    }

    @Override
    @Nonnull
    public List<Assignments> getAllPropDistributions() {
        ArrayList<Assignments> list = new ArrayList<Assignments>();
        for (PropField f : this.propFields) {
            list.add(f.getPropDistribution());
        }
        return list;
    }
}

