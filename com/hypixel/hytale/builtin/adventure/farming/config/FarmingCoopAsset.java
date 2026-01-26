/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class FarmingCoopAsset
implements JsonAssetWithMap<String, DefaultAssetMap<String, FarmingCoopAsset>> {
    public static final AssetBuilderCodec<String, FarmingCoopAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(FarmingCoopAsset.class, FarmingCoopAsset::new, Codec.STRING, (o, v) -> {
        o.id = v;
    }, FarmingCoopAsset::getId, (o, data) -> {
        o.data = data;
    }, o -> o.data).appendInherited(new KeyedCodec<Integer>("MaxResidents", Codec.INTEGER), (asset, maxResidents) -> {
        asset.maxResidents = maxResidents;
    }, asset -> asset.maxResidents, (asset, parent) -> {
        asset.maxResidents = parent.maxResidents;
    }).add()).append(new KeyedCodec("ProduceDrops", new MapCodec<String, HashMap>(ItemDropList.CHILD_ASSET_CODEC, HashMap::new)), (asset, drops) -> {
        asset.produceDrops = drops;
    }, asset -> asset.produceDrops).addValidator(ItemDropList.VALIDATOR_CACHE.getMapValueValidator()).add()).append(new KeyedCodec<Vector3d>("ResidentSpawnOffset", Vector3d.CODEC), (asset, residentSpawnOffset) -> asset.residentSpawnOffset.assign((Vector3d)residentSpawnOffset), asset -> asset.residentSpawnOffset).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<IntRange>("ResidentRoamTime", IntRange.CODEC), (asset, residentRoamTime) -> {
        asset.residentRoamTime = residentRoamTime;
    }, asset -> asset.residentRoamTime).add()).append(new KeyedCodec<Boolean>("CaptureWildNPCsInRange", Codec.BOOLEAN), (asset, captureWildNPCsInRange) -> {
        asset.captureWildNPCsInRange = captureWildNPCsInRange;
    }, asset -> asset.captureWildNPCsInRange).add()).append(new KeyedCodec<Float>("WildCaptureRadius", Codec.FLOAT), (asset, wildCaptureRadius) -> {
        asset.wildCaptureRadius = wildCaptureRadius.floatValue();
    }, asset -> Float.valueOf(asset.wildCaptureRadius)).add()).appendInherited(new KeyedCodec<String[]>("AcceptedNpcGroups", NPCGroup.CHILD_ASSET_CODEC_ARRAY), (o, v) -> {
        o.acceptedNpcGroupIds = v;
    }, o -> o.acceptedNpcGroupIds, (o, p) -> {
        o.acceptedNpcGroupIds = p.acceptedNpcGroupIds;
    }).addValidator(NPCGroup.VALIDATOR_CACHE.getArrayValidator()).add()).afterDecode(captureData -> {
        if (captureData.acceptedNpcGroupIds != null) {
            captureData.acceptedNpcGroupIndexes = new int[captureData.acceptedNpcGroupIds.length];
            for (int i = 0; i < captureData.acceptedNpcGroupIds.length; ++i) {
                int assetIdx;
                captureData.acceptedNpcGroupIndexes[i] = assetIdx = NPCGroup.getAssetMap().getIndex(captureData.acceptedNpcGroupIds[i]);
            }
        }
    })).build();
    private static AssetStore<String, FarmingCoopAsset, DefaultAssetMap<String, FarmingCoopAsset>> ASSET_STORE;
    private AssetExtraInfo.Data data;
    protected String id;
    protected int maxResidents;
    protected Map<String, String> produceDrops = Collections.emptyMap();
    protected IntRange residentRoamTime;
    protected Vector3d residentSpawnOffset = new Vector3d();
    protected String[] acceptedNpcGroupIds;
    protected int[] acceptedNpcGroupIndexes;
    protected boolean captureWildNPCsInRange;
    protected float wildCaptureRadius;

    public static AssetStore<String, FarmingCoopAsset, DefaultAssetMap<String, FarmingCoopAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(FarmingCoopAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, FarmingCoopAsset> getAssetMap() {
        return FarmingCoopAsset.getAssetStore().getAssetMap();
    }

    public FarmingCoopAsset() {
    }

    public FarmingCoopAsset(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public Map<String, String> getProduceDrops() {
        return this.produceDrops;
    }

    public int getMaxResidents() {
        return this.maxResidents;
    }

    public IntRange getResidentRoamTime() {
        return this.residentRoamTime;
    }

    public Vector3d getResidentSpawnOffset() {
        return this.residentSpawnOffset;
    }

    public int[] getAcceptedNpcGroupIndexes() {
        return this.acceptedNpcGroupIndexes;
    }

    public float getWildCaptureRadius() {
        return this.wildCaptureRadius;
    }

    public boolean getCaptureWildNPCsInRange() {
        return this.captureWildNPCsInRange;
    }

    @Nonnull
    public String toString() {
        return "FarmingCoopAsset{id='" + this.id + "', maxResidents=" + this.maxResidents + "}";
    }
}

