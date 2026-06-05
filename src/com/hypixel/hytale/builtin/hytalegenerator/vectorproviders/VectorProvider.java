package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.engine.TerrainDensityProvider;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public abstract class VectorProvider {
   public abstract void process(@Nonnull VectorProvider.Context var1, @Nonnull Vector3d var2);

   public static class Context {
      @Nonnull
      public Vector3d position;
      @Nullable
      public TerrainDensityProvider terrainDensityProvider;
      @Nullable
      public GraphSpace.Node graphNode;

      public Context() {
         this.position = new Vector3d();
         this.terrainDensityProvider = null;
         this.graphNode = null;
      }

      public Context(@Nonnull Vector3d position, @Nullable TerrainDensityProvider terrainDensityProvider, @Nullable GraphSpace.Node graphNode) {
         this.position = position;
         this.terrainDensityProvider = terrainDensityProvider;
         this.graphNode = graphNode;
      }

      public Context(@Nonnull VectorProvider.Context other) {
         this.position = other.position;
         this.terrainDensityProvider = other.terrainDensityProvider;
         this.graphNode = other.graphNode;
      }

      public Context(@Nonnull Density.Context other) {
         this.position = other.position;
         this.terrainDensityProvider = other.terrainDensityProvider;
         this.graphNode = other.graphNode;
      }

      public void assign(@Nonnull Density.Context other) {
         this.position = other.position;
         this.terrainDensityProvider = other.terrainDensityProvider;
         this.graphNode = other.graphNode;
      }

      public void assign(@Nonnull PositionProvider.Context other, @Nonnull Vector3d position) {
         this.position = position;
         this.terrainDensityProvider = null;
         this.graphNode = other.graphNode;
      }
   }
}
