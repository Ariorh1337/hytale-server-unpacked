/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.schema.metadata.ui;

import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import com.hypixel.hytale.codec.schema.metadata.ui.UIButton;
import javax.annotation.Nonnull;

public class UISidebarButtons
implements Metadata {
    private final UIButton[] buttons;

    public UISidebarButtons(UIButton ... buttons) {
        this.buttons = buttons;
    }

    @Override
    public void modify(@Nonnull Schema schema) {
        schema.getHytale().setUiSidebarButtons(this.buttons);
    }
}

