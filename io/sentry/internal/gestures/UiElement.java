/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.gestures;

import io.sentry.util.Objects;
import java.lang.ref.WeakReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UiElement {
    @NotNull
    final WeakReference<Object> viewRef;
    @Nullable
    final String className;
    @Nullable
    final String resourceName;
    @Nullable
    final String tag;
    @NotNull
    final String origin;

    public UiElement(@Nullable Object view, @Nullable String className, @Nullable String resourceName, @Nullable String tag, @NotNull String origin) {
        this.viewRef = new WeakReference<Object>(view);
        this.className = className;
        this.resourceName = resourceName;
        this.tag = tag;
        this.origin = origin;
    }

    @Nullable
    public String getClassName() {
        return this.className;
    }

    @Nullable
    public String getResourceName() {
        return this.resourceName;
    }

    @Nullable
    public String getTag() {
        return this.tag;
    }

    @NotNull
    public String getOrigin() {
        return this.origin;
    }

    @NotNull
    public String getIdentifier() {
        if (this.resourceName != null) {
            return this.resourceName;
        }
        return Objects.requireNonNull(this.tag, "UiElement.tag can't be null");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UiElement uiElement = (UiElement)o;
        return Objects.equals(this.className, uiElement.className) && Objects.equals(this.resourceName, uiElement.resourceName) && Objects.equals(this.tag, uiElement.tag);
    }

    @Nullable
    public Object getView() {
        return this.viewRef.get();
    }

    public int hashCode() {
        return Objects.hash(this.viewRef, this.resourceName, this.tag);
    }

    public static enum Type {
        CLICKABLE,
        SCROLLABLE;

    }
}

