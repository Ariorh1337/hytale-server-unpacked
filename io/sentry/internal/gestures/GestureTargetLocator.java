/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.gestures;

import io.sentry.internal.gestures.UiElement;
import org.jetbrains.annotations.Nullable;

public interface GestureTargetLocator {
    @Nullable
    public UiElement locate(@Nullable Object var1, float var2, float var3, UiElement.Type var4);
}

