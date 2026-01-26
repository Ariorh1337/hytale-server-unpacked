/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.metadata.ui;

import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import javax.annotation.Nonnull;

public class UIEditorFeatures
implements Metadata {
    private final EditorFeature[] features;

    public UIEditorFeatures(EditorFeature ... features) {
        this.features = features;
    }

    @Override
    public void modify(@Nonnull Schema schema) {
        schema.getHytale().setUiEditorFeatures(this.features);
    }

    public static enum EditorFeature {
        WEATHER_DAYTIME_BAR,
        WEATHER_PREVIEW_LOCAL;

    }
}

