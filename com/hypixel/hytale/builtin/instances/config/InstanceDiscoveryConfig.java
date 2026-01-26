/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstanceDiscoveryConfig {
    @Nonnull
    public static final BuilderCodec<InstanceDiscoveryConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InstanceDiscoveryConfig.class, InstanceDiscoveryConfig::new).documentation("Configuration for displaying an event title when a player discovers an instance.")).append(new KeyedCodec<String>("TitleKey", Codec.STRING), (o, i) -> {
        o.titleKey = i;
    }, o -> o.titleKey).documentation("The translation key for the primary title (e.g., \"server.instances.gaia_temple.title\").").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<String>("SubtitleKey", Codec.STRING), (o, i) -> {
        o.subtitleKey = i;
    }, o -> o.subtitleKey).documentation("The translation key for the subtitle (e.g., \"server.instances.gaia_temple.subtitle\").").add()).append(new KeyedCodec<Boolean>("Display", Codec.BOOLEAN), (o, i) -> {
        o.display = i;
    }, o -> o.display).documentation("Whether to display the discovery title and play the discovery sound.").add()).append(new KeyedCodec<Boolean>("AlwaysDisplay", Codec.BOOLEAN), (o, i) -> {
        o.alwaysDisplay = i;
    }, o -> o.alwaysDisplay).documentation("Whether to always display the discovery title, even if already discovered.").add()).append(new KeyedCodec<String>("DiscoverySoundEventId", Codec.STRING), (o, i) -> {
        o.discoverySoundEventId = i;
    }, o -> o.discoverySoundEventId).documentation("The sound event ID to play when discovering this instance.").add()).append(new KeyedCodec<String>("Icon", Codec.STRING), (o, i) -> {
        o.icon = i;
    }, o -> o.icon).documentation("The icon to display with the event title.").add()).append(new KeyedCodec<Boolean>("Major", Codec.BOOLEAN), (o, i) -> {
        o.major = i;
    }, o -> o.major).documentation("Whether this is a major discovery (affects visual presentation).").add()).append(new KeyedCodec<Float>("Duration", Codec.FLOAT), (o, i) -> {
        o.duration = i.floatValue();
    }, o -> Float.valueOf(o.duration)).documentation("The duration to display the event title for, in seconds.").add()).append(new KeyedCodec<Float>("FadeInDuration", Codec.FLOAT), (o, i) -> {
        o.fadeInDuration = i.floatValue();
    }, o -> Float.valueOf(o.fadeInDuration)).documentation("The fade-in duration for the event title, in seconds.").add()).append(new KeyedCodec<Float>("FadeOutDuration", Codec.FLOAT), (o, i) -> {
        o.fadeOutDuration = i.floatValue();
    }, o -> Float.valueOf(o.fadeOutDuration)).documentation("The fade-out duration for the event title, in seconds.").add()).build();
    @Nullable
    private String titleKey;
    @Nullable
    private String subtitleKey;
    private boolean display = true;
    private boolean alwaysDisplay = false;
    @Nullable
    private String discoverySoundEventId;
    @Nullable
    private String icon;
    private boolean major = false;
    private float duration = 4.0f;
    private float fadeInDuration = 1.5f;
    private float fadeOutDuration = 1.5f;

    @Nullable
    public String getTitleKey() {
        return this.titleKey;
    }

    public void setTitleKey(@Nonnull String titleKey) {
        this.titleKey = titleKey;
    }

    @Nullable
    public String getSubtitleKey() {
        return this.subtitleKey;
    }

    public void setSubtitleKey(@Nullable String subtitleKey) {
        this.subtitleKey = subtitleKey;
    }

    public boolean isDisplay() {
        return this.display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean alwaysDisplay() {
        return this.alwaysDisplay;
    }

    public void setAlwaysDisplay(boolean alwaysDisplay) {
        this.alwaysDisplay = alwaysDisplay;
    }

    @Nullable
    public String getDiscoverySoundEventId() {
        return this.discoverySoundEventId;
    }

    public void setDiscoverySoundEventId(@Nullable String discoverySoundEventId) {
        this.discoverySoundEventId = discoverySoundEventId;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(@Nullable String icon) {
        this.icon = icon;
    }

    public boolean isMajor() {
        return this.major;
    }

    public void setMajor(boolean major) {
        this.major = major;
    }

    public float getDuration() {
        return this.duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getFadeInDuration() {
        return this.fadeInDuration;
    }

    public void setFadeInDuration(float fadeInDuration) {
        this.fadeInDuration = fadeInDuration;
    }

    public float getFadeOutDuration() {
        return this.fadeOutDuration;
    }

    public void setFadeOutDuration(float fadeOutDuration) {
        this.fadeOutDuration = fadeOutDuration;
    }

    @Nonnull
    public InstanceDiscoveryConfig clone() {
        InstanceDiscoveryConfig clone = new InstanceDiscoveryConfig();
        clone.titleKey = this.titleKey;
        clone.subtitleKey = this.subtitleKey;
        clone.display = this.display;
        clone.alwaysDisplay = this.alwaysDisplay;
        clone.discoverySoundEventId = this.discoverySoundEventId;
        clone.icon = this.icon;
        clone.major = this.major;
        clone.duration = this.duration;
        clone.fadeInDuration = this.fadeInDuration;
        clone.fadeOutDuration = this.fadeOutDuration;
        return clone;
    }
}

