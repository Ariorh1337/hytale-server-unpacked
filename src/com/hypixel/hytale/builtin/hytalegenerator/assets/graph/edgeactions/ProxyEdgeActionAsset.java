package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.AllNodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.NodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ConstantVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.ProxyEdgeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class ProxyEdgeActionAsset extends EdgeActionAsset {
   public static final ProxyEdgeActionAsset INSTANCE = new ProxyEdgeActionAsset();
   @Nonnull
   public static final BuilderCodec<ProxyEdgeActionAsset> CODEC = BuilderCodec.builder(
         ProxyEdgeActionAsset.class, ProxyEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("HostNodeSelector", NodeSelectorAsset.CODEC, true),
         (asset, value) -> asset.hostNodeSelectorAsset = value,
         asset -> asset.hostNodeSelectorAsset
      )
      .add()
      .append(
         new KeyedCodec<>("Normal", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.normalVectorProviderAsset = value,
         asset -> asset.normalVectorProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("AngleToNormal", DensityAsset.CODEC, true),
         (asset, value) -> asset.angleToNormalDensityAsset = value,
         asset -> asset.angleToNormalDensityAsset
      )
      .add()
      .append(
         new KeyedCodec<>("SpinAngle", DensityAsset.CODEC, true), (asset, value) -> asset.spinAngleDensityAsset = value, asset -> asset.spinAngleDensityAsset
      )
      .add()
      .append(
         new KeyedCodec<>("ProxyDistance", DensityAsset.CODEC, true),
         (asset, value) -> asset.proxyDistanceDensityAsset = value,
         asset -> asset.proxyDistanceDensityAsset
      )
      .add()
      .<Double>append(
         new KeyedCodec<>("MaxProxyDistance", Codec.DOUBLE, true), (asset, value) -> asset.maxProxyDistance = value, asset -> asset.maxProxyDistance
      )
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(
         new KeyedCodec<>("ProxyContent", GraphContentAsset.CODEC, true), (asset, value) -> asset.proxyContentAsset = value, asset -> asset.proxyContentAsset
      )
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private NodeSelectorAsset hostNodeSelectorAsset = AllNodeSelectorAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset normalVectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private DensityAsset angleToNormalDensityAsset = ConstantDensityAsset.ZERO_INSTANCE;
   @Nonnull
   private DensityAsset spinAngleDensityAsset = ConstantDensityAsset.ZERO_INSTANCE;
   @Nonnull
   private DensityAsset proxyDistanceDensityAsset = ConstantDensityAsset.ZERO_INSTANCE;
   private double maxProxyDistance = 0.0;
   @Nonnull
   private GraphContentAsset proxyContentAsset = GraphContentAsset.INSTANCE;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      DensityAsset.Argument densityArgument = new DensityAsset.Argument(argument);
      return new ProxyEdgeAction(
         this.hostNodeSelectorAsset.build(argument),
         this.normalVectorProviderAsset.build(new VectorProviderAsset.Argument(argument)),
         this.angleToNormalDensityAsset.build(densityArgument),
         this.spinAngleDensityAsset.build(densityArgument),
         this.proxyDistanceDensityAsset.build(densityArgument),
         this.proxyContentAsset.build(argument),
         this.maxProxyDistance,
         argument.parentSeed.child(this.seed).createSupplier().get()
      );
   }

   @Override
   public void cleanUp() {
      this.hostNodeSelectorAsset.cleanUp();
      this.normalVectorProviderAsset.cleanUp();
      this.angleToNormalDensityAsset.cleanUp();
      this.spinAngleDensityAsset.cleanUp();
      this.proxyDistanceDensityAsset.cleanUp();
      this.proxyContentAsset.cleanUp();
   }
}
