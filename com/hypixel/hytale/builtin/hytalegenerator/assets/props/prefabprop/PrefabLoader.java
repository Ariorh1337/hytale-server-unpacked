/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabFileVisitor;
import com.hypixel.hytale.common.util.ExceptionUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.BsonPrefabBufferDeserializer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.util.BsonUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class PrefabLoader {
    @Nonnull
    public static void loadAllPrefabBuffersUnder(@Nonnull Path dirPath, List<PrefabBuffer> pathPrefabs) {
        if (!Files.isDirectory(dirPath, new LinkOption[0])) {
            PrefabBuffer prefab = PrefabLoader.loadPrefabBufferAt(dirPath);
            if (prefab == null) {
                return;
            }
            pathPrefabs.add(prefab);
            return;
        }
        try {
            Files.walkFileTree(dirPath, new PrefabFileVisitor(pathPrefabs));
        }
        catch (IOException e) {
            Object msg = "Exception thrown by HytaleGenerator while loading a Prefab:\n";
            msg = (String)msg + ExceptionUtil.toStringWithStack(e);
            LoggerUtil.getLogger().severe((String)msg);
        }
    }

    @Nullable
    public static PrefabBuffer loadPrefabBufferAt(@Nonnull Path filePath) {
        if (!PrefabLoader.hasJsonExtension(filePath)) {
            return null;
        }
        try {
            BsonDocument prefabAsBson = BsonUtil.readDocumentNow(filePath);
            if (prefabAsBson == null) {
                return null;
            }
            return BsonPrefabBufferDeserializer.INSTANCE.deserialize(filePath, prefabAsBson);
        }
        catch (Exception e) {
            String msg = "Exception thrown by HytaleGenerator while loading a PrefabBuffer for " + String.valueOf(filePath) + ":\n";
            msg = msg + ExceptionUtil.toStringWithStack(e);
            LoggerUtil.getLogger().severe(msg);
            return null;
        }
    }

    public static boolean hasJsonExtension(@Nonnull Path path) {
        String pathString = path.toString();
        return pathString.toLowerCase().endsWith(".json");
    }
}

