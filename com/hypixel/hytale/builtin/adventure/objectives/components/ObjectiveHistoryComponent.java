/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.components;

import com.hypixel.hytale.builtin.adventure.objectives.historydata.ObjectiveHistoryData;
import com.hypixel.hytale.builtin.adventure.objectives.historydata.ObjectiveLineHistoryData;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class ObjectiveHistoryComponent
implements Component<EntityStore> {
    public static final BuilderCodec<ObjectiveHistoryComponent> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ObjectiveHistoryComponent.class, ObjectiveHistoryComponent::new).append(new KeyedCodec("ObjectiveHistory", new MapCodec<ObjectiveHistoryData, Object2ObjectOpenHashMap>(ObjectiveHistoryData.CODEC, Object2ObjectOpenHashMap::new, false)), (objectiveHistoryComponent, stringObjectiveHistoryDataMap) -> {
        objectiveHistoryComponent.objectiveHistoryMap = stringObjectiveHistoryDataMap;
    }, objectiveHistoryComponent -> objectiveHistoryComponent.objectiveHistoryMap).add()).append(new KeyedCodec("ObjectiveLineHistory", new MapCodec<ObjectiveLineHistoryData, Object2ObjectOpenHashMap>(ObjectiveLineHistoryData.CODEC, Object2ObjectOpenHashMap::new, false)), (objectiveHistoryComponent, stringObjectiveLineHistoryDataMap) -> {
        objectiveHistoryComponent.objectiveLineHistoryMap = stringObjectiveLineHistoryDataMap;
    }, objectiveHistoryComponent -> objectiveHistoryComponent.objectiveLineHistoryMap).add()).build();
    private Map<String, ObjectiveHistoryData> objectiveHistoryMap = new Object2ObjectOpenHashMap<String, ObjectiveHistoryData>();
    private Map<String, ObjectiveLineHistoryData> objectiveLineHistoryMap = new Object2ObjectOpenHashMap<String, ObjectiveLineHistoryData>();

    public Map<String, ObjectiveHistoryData> getObjectiveHistoryMap() {
        return this.objectiveHistoryMap;
    }

    public Map<String, ObjectiveLineHistoryData> getObjectiveLineHistoryMap() {
        return this.objectiveLineHistoryMap;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        ObjectiveHistoryComponent component = new ObjectiveHistoryComponent();
        component.objectiveHistoryMap.putAll(this.objectiveHistoryMap);
        component.objectiveLineHistoryMap.putAll(this.objectiveLineHistoryMap);
        return component;
    }
}

