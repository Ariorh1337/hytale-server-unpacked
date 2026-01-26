/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.camera.asset.viewbobbing;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.adventure.camera.asset.CameraShakeConfig;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.MovementType;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class ViewBobbing
implements NetworkSerializable<com.hypixel.hytale.protocol.ViewBobbing>,
JsonAssetWithMap<MovementType, AssetMap<MovementType, ViewBobbing>> {
    @Nonnull
    public static final Codec<MovementType> MOVEMENT_TYPE_CODEC = new EnumCodec<MovementType>(MovementType.class);
    @Nonnull
    public static final AssetBuilderCodec<MovementType, ViewBobbing> CODEC = ((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ViewBobbing.class, ViewBobbing::new, MOVEMENT_TYPE_CODEC, (o, v) -> {
        o.id = v;
    }, ViewBobbing::getId, (o, data) -> {
        o.data = data;
    }, o -> o.data).appendInherited(new KeyedCodec<CameraShakeConfig>("FirstPerson", CameraShakeConfig.CODEC), (o, v) -> {
        o.firstPerson = v;
    }, o -> o.firstPerson, (o, p) -> {
        o.firstPerson = p.firstPerson;
    }).documentation("The camera shake profile to be applied").addValidator(Validators.nonNull()).add()).build();
    protected MovementType id;
    protected AssetExtraInfo.Data data;
    @Nonnull
    protected CameraShakeConfig firstPerson;

    @Override
    public MovementType getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ViewBobbing toPacket() {
        return new com.hypixel.hytale.protocol.ViewBobbing(this.firstPerson.toPacket());
    }

    @Nonnull
    public String toString() {
        return "ViewBobbing{id=" + String.valueOf((Object)this.id) + ", data=" + String.valueOf(this.data) + ", firstPerson=" + String.valueOf(this.firstPerson) + "}";
    }
}

