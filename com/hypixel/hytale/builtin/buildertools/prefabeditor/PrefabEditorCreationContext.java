/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor;

import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabAlignment;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabRootDirectory;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabRowSplitMode;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabStackingAxis;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.WorldGenType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.nio.file.Path;
import java.util.List;

public interface PrefabEditorCreationContext {
    public Player getEditor();

    public PlayerRef getEditorRef();

    public List<Path> getPrefabPaths();

    public int getBlocksBetweenEachPrefab();

    public int getPasteLevelGoal();

    public boolean loadChildPrefabs();

    public boolean shouldLoadEntities();

    public PrefabStackingAxis getStackingAxis();

    public WorldGenType getWorldGenType();

    public int getBlocksAboveSurface();

    public PrefabAlignment getAlignment();

    public PrefabRootDirectory getPrefabRootDirectory();

    public boolean isWorldTickingEnabled();

    public PrefabRowSplitMode getRowSplitMode();

    public List<String> getUnprocessedPrefabPaths();

    public String getEnvironment();

    public String getGrassTint();
}

