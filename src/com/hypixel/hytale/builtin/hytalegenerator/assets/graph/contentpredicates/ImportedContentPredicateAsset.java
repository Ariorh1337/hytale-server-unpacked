package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentpredicates;

import com.hypixel.hytale.builtin.hytalegenerator.predicates.ConstantSetPredicate;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class ImportedContentPredicateAsset extends ContentPredicateAsset {
   @Nonnull
   public static final BuilderCodec<ImportedContentPredicateAsset> CODEC = BuilderCodec.builder(
         ImportedContentPredicateAsset.class, ImportedContentPredicateAsset::new, ContentPredicateAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @Nonnull
   @Override
   public Predicate<IntSet> build() {
      if (super.skip()) {
         return ConstantSetPredicate.TRUE;
      } else if (this.name != null && !this.name.isEmpty()) {
         ContentPredicateAsset exportedAsset = ContentPredicateAsset.getExportedAsset(this.name);
         return exportedAsset == null ? ConstantSetPredicate.TRUE : exportedAsset.build();
      } else {
         HytaleLogger.getLogger().atWarning().log("An exported ContentPredicate with the name does not exist: " + this.name);
         return ConstantSetPredicate.TRUE;
      }
   }
}
