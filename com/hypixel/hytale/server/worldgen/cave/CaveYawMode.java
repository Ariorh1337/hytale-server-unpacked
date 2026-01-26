/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave;

import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import javax.annotation.Nullable;

public enum CaveYawMode {
    NODE{

        @Override
        public float combine(float parentYaw, @Nullable PrefabRotation parentRotation) {
            return parentYaw;
        }
    }
    ,
    SUM{

        @Override
        public float combine(float parentYaw, @Nullable PrefabRotation parentRotation) {
            if (parentRotation == null) {
                return parentYaw;
            }
            return parentYaw + parentRotation.getYaw();
        }
    }
    ,
    PREFAB{

        @Override
        public float combine(float parentYaw, @Nullable PrefabRotation parentRotation) {
            if (parentRotation == null) {
                return parentYaw;
            }
            return parentRotation.getYaw();
        }
    };


    public abstract float combine(float var1, @Nullable PrefabRotation var2);
}

