/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StateData {
    public static final String NULL_STATE_ID = "default";
    public static final BuilderCodec.Builder<StateData> DEFAULT_CODEC_BUILDER = (BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StateData.class, StateData::new).appendInherited(new KeyedCodec<String>("Id", Codec.STRING), (stateData, s) -> {
        stateData.id = s;
    }, stateData -> stateData.id, (o, p) -> {
        o.id = p.id;
    }).add()).afterDecode((stateData, extraInfo) -> {
        if (stateData.stateToBlock != null) {
            Object2ObjectOpenHashMap<String, String> map = new Object2ObjectOpenHashMap<String, String>();
            for (Map.Entry<String, String> entry : stateData.stateToBlock.entrySet()) {
                map.put(entry.getValue(), entry.getKey());
            }
            stateData.blockToState = Collections.unmodifiableMap(map);
        }
    });
    public static final BuilderCodec<StateData> DEFAULT_CODEC = DEFAULT_CODEC_BUILDER.build();
    public static final CodecMapCodec<StateData> CODEC = new CodecMapCodec(true).register(Priority.DEFAULT, "StateData", (Class)StateData.class, (Codec)DEFAULT_CODEC);
    private String id;
    private Map<String, String> stateToBlock;
    private Map<String, String> blockToState;

    protected StateData() {
    }

    public StateData(String id) {
        this.id = id;
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    @Nullable
    public String getBlockForState(String state) {
        if (this.stateToBlock == null) {
            return null;
        }
        return this.stateToBlock.get(state);
    }

    @Nullable
    public String getStateForBlock(String blockTypeKey) {
        if (this.blockToState == null) {
            return null;
        }
        return this.blockToState.get(blockTypeKey);
    }

    @Nullable
    public Map<String, Integer> toPacket(@Nonnull BlockType current) {
        if (this.stateToBlock == null) {
            return null;
        }
        Object2IntOpenHashMap<String> data = new Object2IntOpenHashMap<String>();
        for (String state : this.stateToBlock.keySet()) {
            String key = current.getBlockKeyForState(state);
            int index = BlockType.getAssetMap().getIndex(key);
            if (index == Integer.MIN_VALUE) continue;
            data.put(state, Integer.valueOf(index));
        }
        return data;
    }

    @Nonnull
    public String toString() {
        return "StateData{id='" + this.id + "', stateToBlock='" + String.valueOf(this.stateToBlock) + "'}";
    }

    public void copyFrom(@Nullable StateData state) {
        if (state == null || this.stateToBlock != null) {
            return;
        }
        this.stateToBlock = state.stateToBlock;
        this.blockToState = state.blockToState;
    }

    static void addDefinitions() {
        DEFAULT_CODEC_BUILDER.addField(new KeyedCodec("Definitions", new MapCodec(new ContainedAssetCodec(BlockType.class, BlockType.CODEC, ContainedAssetCodec.Mode.INJECT_PARENT, StateData::generateBlockKey), HashMap::new)), (stateData, m) -> {
            stateData.stateToBlock = m;
        }, stateData -> stateData.stateToBlock);
    }

    @Nonnull
    private static String generateBlockKey(@Nonnull AssetExtraInfo<String> extraInfo) {
        String key = extraInfo.getKey();
        String newName = "*" + key + "_" + extraInfo.peekKey('_');
        return newName;
    }
}

