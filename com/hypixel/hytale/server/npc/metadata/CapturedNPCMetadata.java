/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.metadata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class CapturedNPCMetadata {
    public static final String KEY = "CapturedEntity";
    public static final BuilderCodec<CapturedNPCMetadata> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CapturedNPCMetadata.class, CapturedNPCMetadata::new).appendInherited(new KeyedCodec<String>("IconPath", Codec.STRING), (meta, s) -> {
        meta.iconPath = s;
    }, meta -> meta.iconPath, (meta, parent) -> {
        meta.iconPath = parent.iconPath;
    }).add()).appendInherited(new KeyedCodec<String>("NpcNameKey", Codec.STRING), (meta, s) -> {
        meta.npcNameKey = s;
    }, meta -> meta.npcNameKey, (meta, parent) -> {
        meta.npcNameKey = parent.npcNameKey;
    }).add()).appendInherited(new KeyedCodec<String>("FullItemIcon", Codec.STRING), (meta, s) -> {
        meta.fullItemIcon = s;
    }, meta -> meta.fullItemIcon, (meta, parent) -> {
        meta.fullItemIcon = parent.fullItemIcon;
    }).add()).build();
    public static final KeyedCodec<CapturedNPCMetadata> KEYED_CODEC = new KeyedCodec<CapturedNPCMetadata>("CapturedEntity", CODEC);
    private String iconPath;
    private String npcNameKey;
    private String fullItemIcon;

    public String getIconPath() {
        return this.iconPath;
    }

    public String getNpcNameKey() {
        return this.npcNameKey;
    }

    public String getFullItemIcon() {
        return this.fullItemIcon;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setNpcNameKey(String npcNameKey) {
        this.npcNameKey = npcNameKey;
    }

    public void setFullItemIcon(String fullItemIcon) {
        this.fullItemIcon = fullItemIcon;
    }
}

