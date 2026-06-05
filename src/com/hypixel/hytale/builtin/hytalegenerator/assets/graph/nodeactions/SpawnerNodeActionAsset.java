package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.bounds.DecimalBounds3dAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers.ConstantContentSupplierAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers.ContentSupplierAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ConstantVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.SpawnerNodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SpawnerNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<SpawnerNodeActionAsset> CODEC = BuilderCodec.builder(
         SpawnerNodeActionAsset.class, SpawnerNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Offset", VectorProviderAsset.CODEC, true), (asset, value) -> asset.offsetAsset = value, asset -> asset.offsetAsset)
      .add()
      .append(new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true), (asset, value) -> asset.positionsAsset = value, asset -> asset.positionsAsset)
      .add()
      .append(
         new KeyedCodec<>("Content", ContentSupplierAsset.CODEC, true),
         (asset, value) -> asset.contentSupplierAsset = value,
         asset -> asset.contentSupplierAsset
      )
      .add()
      .append(
         new KeyedCodec<>("ClusterBounds", DecimalBounds3dAsset.CODEC, true),
         (asset, value) -> asset.clusterBoundsAsset = value,
         asset -> asset.clusterBoundsAsset
      )
      .add()
      .<Double>append(
         new KeyedCodec<>("MaxOffsetExclusive", Codec.DOUBLE, true), (asset, value) -> asset.maxOffsetExclusive = value, asset -> asset.maxOffsetExclusive
      )
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("CreateEdges", Codec.BOOLEAN, true), (asset, value) -> asset.isCreateEdges = value, asset -> asset.isCreateEdges)
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset offsetAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private PositionProviderAsset positionsAsset = ListPositionProviderAsset.INSTANCE;
   @Nonnull
   private ContentSupplierAsset contentSupplierAsset = ConstantContentSupplierAsset.INSTANCE;
   @Nonnull
   private DecimalBounds3dAsset clusterBoundsAsset = DecimalBounds3dAsset.ZERO_INSTANCE;
   private double maxOffsetExclusive = 0.0;
   private boolean isCreateEdges = false;

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      }

      assert this.maxOffsetExclusive >= 0.0;
      return new SpawnerNodeAction(
         this.offsetAsset.build(new VectorProviderAsset.Argument(argument)),
         this.positionsAsset.build(new PositionProviderAsset.Argument(argument)),
         this.contentSupplierAsset.build(argument),
         this.clusterBoundsAsset.build(),
         this.maxOffsetExclusive,
         this.isCreateEdges
      );
   }

   @Override
   public void cleanUp() {
      this.offsetAsset.cleanUp();
      this.positionsAsset.cleanUp();
      this.contentSupplierAsset.cleanUp();
   }
}
