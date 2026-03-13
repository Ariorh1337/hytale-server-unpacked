package com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.common.util.ExceptionUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabLoader {
   public static void traverseAllPrefabBuffersUnder(@Nonnull Path path, @Nonnull BiConsumer<Path, IPrefabBuffer> prefabsOut) {
      if (!Files.isDirectory(path)) {
         IPrefabBuffer prefab = loadPrefabBufferAt(path);
         if (prefab != null) {
            prefabsOut.accept(path, prefab);
         }
      } else {
         try {
            Files.walkFileTree(path, new PrefabFileVisitor(prefabsOut));
         } catch (IOException e) {
            String msg = "Exception thrown by HytaleGenerator while loading a Prefab:\n";
            msg = msg + ExceptionUtil.toStringWithStack(e);
            LoggerUtil.getLogger().severe(msg);
         }
      }
   }

   @Nullable
   public static IPrefabBuffer loadPrefabBufferAt(@Nonnull Path filePath) {
      if (!hasJsonExtension(filePath)) {
         return null;
      } else if (!Files.exists(filePath)) {
         LoggerUtil.getLogger().info("Didn't find a prefab with path: " + filePath);
         return null;
      } else {
         return PrefabBufferUtil.getCached(filePath);
      }
   }

   public static boolean hasJsonExtension(@Nonnull Path path) {
      String pathString = path.toString();
      return pathString.toLowerCase().endsWith(".json");
   }
}
