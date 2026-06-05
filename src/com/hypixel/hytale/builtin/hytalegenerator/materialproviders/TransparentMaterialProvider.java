package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TransparentMaterialProvider<V> extends MaterialProvider<V> {
   private static final MaterialProvider INSTANCE = new TransparentMaterialProvider();

   @Nonnull
   public static <V> TransparentMaterialProvider<V> instance() {
      return (TransparentMaterialProvider<V>)INSTANCE;
   }

   private TransparentMaterialProvider() {
   }

   @NullableDecl
   @Override
   public V getVoxelTypeAt(@NonNullDecl MaterialProvider.Context context) {
      return null;
   }
}
