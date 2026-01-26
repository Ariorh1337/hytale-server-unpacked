/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockFaceTags;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomTemplateConnectedBlockPattern;

public class ConnectedBlockShape {
    public static final BuilderCodec<ConnectedBlockShape> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ConnectedBlockShape.class, ConnectedBlockShape::new).append(new KeyedCodec<T[]>("PatternsToMatchAnyOf", new ArrayCodec<CustomTemplateConnectedBlockPattern>(CustomTemplateConnectedBlockPattern.CODEC, CustomTemplateConnectedBlockPattern[]::new), true), (o, patternsToMatchAnyOf) -> {
        o.patternsToMatchAnyOf = patternsToMatchAnyOf;
    }, o -> o.patternsToMatchAnyOf).add()).append(new KeyedCodec<ConnectedBlockFaceTags>("FaceTags", ConnectedBlockFaceTags.CODEC, false), (o, faceTags) -> {
        o.faceTags = faceTags;
    }, o -> o.faceTags).add()).build();
    private CustomTemplateConnectedBlockPattern[] patternsToMatchAnyOf;
    private ConnectedBlockFaceTags faceTags;

    public CustomTemplateConnectedBlockPattern[] getPatternsToMatchAnyOf() {
        return this.patternsToMatchAnyOf;
    }

    public ConnectedBlockFaceTags getFaceTags() {
        return this.faceTags;
    }
}

