/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.metadata.ui;

import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import javax.annotation.Nonnull;

public class UIDisplayMode
implements Metadata {
    public static final UIDisplayMode NORMAL = new UIDisplayMode(DisplayMode.NORMAL);
    public static final UIDisplayMode COMPACT = new UIDisplayMode(DisplayMode.COMPACT);
    public static final UIDisplayMode HIDDEN = new UIDisplayMode(DisplayMode.HIDDEN);
    private final DisplayMode mode;

    private UIDisplayMode(DisplayMode mode) {
        this.mode = mode;
    }

    @Override
    public void modify(@Nonnull Schema schema) {
        schema.getHytale().setUiDisplayMode(this.mode);
    }

    public static enum DisplayMode {
        NORMAL,
        COMPACT,
        HIDDEN;

    }
}

