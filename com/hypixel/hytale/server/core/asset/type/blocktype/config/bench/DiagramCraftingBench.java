/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.CraftingBench;

public class DiagramCraftingBench
extends CraftingBench {
    public static final BuilderCodec<DiagramCraftingBench> CODEC = BuilderCodec.builder(DiagramCraftingBench.class, DiagramCraftingBench::new, CraftingBench.CODEC).build();
}

