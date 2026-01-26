/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.resources;

import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;

public sealed interface WorldSleep
permits Awake, WorldSlumber {

    public static enum Awake implements WorldSleep
    {
        INSTANCE;

    }
}

