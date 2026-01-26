/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.receiver;

import com.hypixel.hytale.server.core.Message;

public interface IEventTitleReceiver {
    public static final float DEFAULT_DURATION = 4.0f;
    public static final float DEFAULT_FADE_DURATION = 1.5f;

    default public void showEventTitle(Message primaryTitle, Message secondaryTitle, boolean isMajor, String icon) {
        this.showEventTitle(primaryTitle, secondaryTitle, isMajor, icon, 4.0f);
    }

    default public void showEventTitle(Message primaryTitle, Message secondaryTitle, boolean isMajor, String icon, float duration) {
        this.showEventTitle(primaryTitle, secondaryTitle, isMajor, icon, duration, 1.5f, 1.5f);
    }

    public void showEventTitle(Message var1, Message var2, boolean var3, String var4, float var5, float var6, float var7);

    default public void hideEventTitle() {
        this.hideEventTitle(1.5f);
    }

    public void hideEventTitle(float var1);
}

