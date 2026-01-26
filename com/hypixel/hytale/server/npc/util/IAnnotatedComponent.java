/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.util.ComponentInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAnnotatedComponent {
    public void getInfo(Role var1, ComponentInfo var2);

    public void setContext(IAnnotatedComponent var1, int var2);

    @Nullable
    public IAnnotatedComponent getParent();

    public int getIndex();

    default public String getLabel() {
        int index = this.getIndex();
        return index >= 0 ? String.format("[%s]%s", index, this.getClass().getSimpleName()) : this.getClass().getSimpleName();
    }

    default public void getBreadCrumbs(@Nonnull StringBuilder sb) {
        String label;
        IAnnotatedComponent parent = this.getParent();
        if (parent != null) {
            parent.getBreadCrumbs(sb);
        }
        if ((label = this.getLabel()) != null && !label.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append('|');
            }
            sb.append(label);
        }
    }

    @Nonnull
    default public String getBreadCrumbs() {
        StringBuilder sb = new StringBuilder();
        this.getBreadCrumbs(sb);
        return sb.toString();
    }
}

