/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.biome;

import com.hypixel.hytale.builtin.hytalegenerator.PropField;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import java.util.List;

public interface PropsSource {
    public List<PropField> getPropFields();

    public List<Assignments> getAllPropDistributions();
}

