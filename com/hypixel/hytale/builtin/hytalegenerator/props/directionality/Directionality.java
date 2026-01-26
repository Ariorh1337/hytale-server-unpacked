/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props.directionality;

import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Directionality {
    @Nullable
    public abstract PrefabRotation getRotationAt(@Nonnull Pattern.Context var1);

    public abstract Pattern getGeneralPattern();

    public abstract Vector3i getReadRangeWith(@Nonnull Scanner var1);

    public abstract List<PrefabRotation> getPossibleRotations();

    @Nonnull
    public static Directionality noDirectionality() {
        return new Directionality(){

            @Override
            public PrefabRotation getRotationAt(@Nonnull Pattern.Context context) {
                return null;
            }

            @Override
            @Nonnull
            public Pattern getGeneralPattern() {
                return Pattern.noPattern();
            }

            @Override
            @Nonnull
            public Vector3i getReadRangeWith(@Nonnull Scanner scanner) {
                return new Vector3i();
            }

            @Override
            @Nonnull
            public List<PrefabRotation> getPossibleRotations() {
                return Collections.emptyList();
            }
        };
    }
}

