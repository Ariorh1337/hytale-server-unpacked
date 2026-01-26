/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.Builder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BuilderContext {
    public BuilderContext getOwner();

    public String getLabel();

    default public void setCurrentStateName(String name) {
    }

    @Nullable
    default public Builder<?> getParent() {
        BuilderContext owner = this.getOwner();
        return owner instanceof Builder ? (Builder<?>)owner : (owner != null ? owner.getParent() : null);
    }

    default public void getBreadCrumbs(@Nonnull StringBuilder stringBuilder) {
        String label;
        BuilderContext owner = this.getOwner();
        if (owner != null) {
            owner.getBreadCrumbs(stringBuilder);
        }
        if ((label = this.getLabel()) != null && !label.isEmpty()) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append('|');
            }
            stringBuilder.append(label);
        }
    }

    @Nonnull
    default public String getBreadCrumbs() {
        StringBuilder stringBuilder = new StringBuilder(80);
        this.getBreadCrumbs(stringBuilder);
        return stringBuilder.toString();
    }
}

