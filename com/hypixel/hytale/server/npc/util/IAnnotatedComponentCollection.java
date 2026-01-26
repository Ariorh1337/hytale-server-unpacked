/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import javax.annotation.Nullable;

public interface IAnnotatedComponentCollection
extends IAnnotatedComponent {
    public int componentCount();

    @Nullable
    public IAnnotatedComponent getComponent(int var1);
}

