/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders.cached;

import com.hypixel.hytale.math.vector.Vector3d;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CacheThreadMemory {
    Map<Long, Vector3d[]> sections;
    LinkedList<Long> expirationList;
    int size;

    public CacheThreadMemory(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        this.sections = new HashMap<Long, Vector3d[]>(size);
        this.expirationList = new LinkedList();
        this.size = size;
    }
}

