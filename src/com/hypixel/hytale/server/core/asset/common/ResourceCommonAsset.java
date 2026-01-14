package com.hypixel.hytale.server.core.asset.common;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResourceCommonAsset extends CommonAsset {
   private final Class<?> clazz;
   private final String path;

   public ResourceCommonAsset(Class<?> clazz, String path, @Nonnull String name, byte[] bytes) {
      super(name, bytes);
      this.clazz = clazz;
      this.path = path;
   }

   public ResourceCommonAsset(Class<?> clazz, String path, @Nonnull String name, @Nonnull String hash, byte[] bytes) {
      super(name, hash, bytes);
      this.clazz = clazz;
      this.path = path;
   }

   public String getPath() {
      return this.path;
   }

   @Nonnull
   @Override
   public CompletableFuture<byte[]> getBlob0() {
      try (InputStream stream = this.clazz.getResourceAsStream(this.path)) {
         return CompletableFuture.completedFuture(stream.readAllBytes());
      } catch (IOException e) {
         return CompletableFuture.failedFuture(e);
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return "ResourceCommonAsset{" + super.toString() + "}";
   }

   @Nullable
   public static ResourceCommonAsset of(@Nonnull Class<?> clazz, @Nonnull String path, @Nonnull String name) {
      try (InputStream stream = clazz.getResourceAsStream(path)) {
         if (stream == null) {
            return null;
         }

         byte[] bytes = stream.readAllBytes();
         return new ResourceCommonAsset(clazz, path, name, bytes);
      } catch (IOException e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }
}
