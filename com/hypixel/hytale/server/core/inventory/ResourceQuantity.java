/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory;

import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResourceQuantity {
    protected String resourceId;
    protected int quantity;

    public ResourceQuantity(String resourceId, int quantity) {
        Objects.requireNonNull(resourceId, "resourceId cannot be null!");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity " + quantity + " must be >0!");
        }
        this.resourceId = resourceId;
        this.quantity = quantity;
    }

    protected ResourceQuantity() {
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    @Nonnull
    public ResourceQuantity clone(int quantity) {
        return new ResourceQuantity(this.resourceId, quantity);
    }

    @Nullable
    public ItemResourceType getResourceType(@Nonnull Item item) {
        return ItemContainer.getMatchingResourceType(item, this.resourceId);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResourceQuantity itemStack = (ResourceQuantity)o;
        if (this.quantity != itemStack.quantity) {
            return false;
        }
        return this.resourceId != null ? this.resourceId.equals(itemStack.resourceId) : itemStack.resourceId == null;
    }

    public int hashCode() {
        int result = this.resourceId != null ? this.resourceId.hashCode() : 0;
        result = 31 * result + this.quantity;
        return result;
    }

    @Nonnull
    public String toString() {
        return "ResourceQuantity{resourceId='" + this.resourceId + "', quantity=" + this.quantity + "}";
    }
}

