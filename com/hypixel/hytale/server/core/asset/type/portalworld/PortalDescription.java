/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.portalworld;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.portalworld.PillTag;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PortalDescription {
    public static final BuilderCodec<PortalDescription> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PortalDescription.class, PortalDescription::new).append(new KeyedCodec<String>("DisplayName", Codec.STRING), (portalType, o) -> {
        portalType.displayNameKey = o;
    }, portalType -> portalType.displayNameKey).documentation("The translation key for the name of this portal.").add()).append(new KeyedCodec<String>("FlavorText", Codec.STRING), (portalType, o) -> {
        portalType.flavorTextKey = o;
    }, portalType -> portalType.flavorTextKey).documentation("The translation key for the description of this portal.").add()).append(new KeyedCodec<Color>("ThemeColor", ProtocolCodecs.COLOR), (portalType, o) -> {
        portalType.themeColor = o;
    }, portalType -> portalType.themeColor).documentation("What color do you associate with this portal? May be used in many places.").add()).append(new KeyedCodec<T[]>("DescriptionTags", new ArrayCodec<PillTag>(PillTag.CODEC, PillTag[]::new)), (portalType, o) -> {
        portalType.pillTags = o;
    }, portalType -> portalType.pillTags).documentation("Purely cosmetic list of tags for the UI.").add()).append(new KeyedCodec<T[]>("Objectives", Codec.STRING_ARRAY), (portalType, o) -> {
        portalType.objectivesKeys = o;
    }, portalType -> portalType.objectivesKeys).documentation("List of translation keys for the objectives in this portal.").add()).append(new KeyedCodec<T[]>("Tips", Codec.STRING_ARRAY), (portalType, o) -> {
        portalType.wisdomKeys = o;
    }, portalType -> portalType.wisdomKeys).documentation("List of translation keys for the tips/wisdom offered for this portal.").add()).append(new KeyedCodec<String>("SplashImage", Codec.STRING), (portalType, o) -> {
        portalType.splashImageFilename = o;
    }, portalType -> portalType.splashImageFilename).documentation("The filename of the splash image for this portal. Your best bet to find the folder is to search for an existing portal's image in assets. Screenshots taken 60 fov.").add()).build();
    private String displayNameKey;
    private String flavorTextKey;
    private Color themeColor;
    private PillTag[] pillTags;
    private String[] objectivesKeys;
    private String[] wisdomKeys;
    private String splashImageFilename;

    public String getDisplayNameKey() {
        return this.displayNameKey;
    }

    public Message getDisplayName() {
        return Message.translation(this.displayNameKey);
    }

    public String getFlavorTextKey() {
        return this.flavorTextKey;
    }

    public Message getFlavorText() {
        return Message.translation(this.flavorTextKey);
    }

    public Color getThemeColor() {
        return this.themeColor;
    }

    public List<PillTag> getPillTags() {
        return this.pillTags == null ? Collections.emptyList() : Arrays.asList(this.pillTags);
    }

    public String[] getObjectivesKeys() {
        return this.objectivesKeys == null ? ArrayUtil.EMPTY_STRING_ARRAY : this.objectivesKeys;
    }

    public String[] getWisdomKeys() {
        return this.wisdomKeys == null ? ArrayUtil.EMPTY_STRING_ARRAY : this.wisdomKeys;
    }

    public String getSplashImageFilename() {
        return this.splashImageFilename;
    }
}

