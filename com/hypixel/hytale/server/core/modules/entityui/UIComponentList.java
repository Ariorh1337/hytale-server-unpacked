/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entityui;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.entityui.EntityUIModule;
import com.hypixel.hytale.server.core.modules.entityui.asset.EntityUIComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class UIComponentList
implements Component<EntityStore> {
    public static final BuilderCodec<UIComponentList> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UIComponentList.class, UIComponentList::new).append(new KeyedCodec<T[]>("Components", Codec.STRING_ARRAY), (list, v) -> {
        list.components = v;
    }, list -> list.components).add()).afterDecode(list -> {
        list.componentIds = ArrayUtil.EMPTY_INT_ARRAY;
        list.update();
    })).build();
    protected String[] components;
    protected int[] componentIds;

    public static ComponentType<EntityStore, UIComponentList> getComponentType() {
        return EntityUIModule.get().getUIComponentListType();
    }

    public UIComponentList() {
    }

    public UIComponentList(@Nonnull UIComponentList other) {
        this.componentIds = other.componentIds;
        this.components = other.components;
    }

    public void update() {
        int oldLength = this.componentIds.length;
        IndexedLookupTableAssetMap<String, EntityUIComponent> assetMap = EntityUIComponent.getAssetMap();
        int assetCount = assetMap.getNextIndex();
        if (oldLength <= assetCount) {
            this.componentIds = Arrays.copyOf(this.componentIds, assetCount);
            for (int index = oldLength; index < assetCount; ++index) {
                this.componentIds[index] = index;
            }
        }
    }

    public int[] getComponentIds() {
        return this.componentIds;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new UIComponentList(this);
    }
}

