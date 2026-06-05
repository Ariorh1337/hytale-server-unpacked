package com.hypixel.hytale.builtin.reactiveblocks.states;

import com.hypixel.hytale.builtin.reactiveblocks.ReactiveBlocksPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.ExplosionConfig;
import com.hypixel.hytale.server.core.entity.ExplosionUtils;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.ExplodeInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Instant;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;

public class BlockExplosive implements Component<ChunkStore> {
   @Nonnull
   public static final BuilderCodec<BlockExplosive> CODEC = BuilderCodec.builder(BlockExplosive.class, BlockExplosive::new)
      .append(new KeyedCodec<>("PrimedOnPlace", Codec.BOOLEAN), (state, value) -> state.primedOnPlace = value, state -> state.primedOnPlace)
      .documentation("If the fuse will immediately being running down when the block is placed.")
      .add()
      .<Float>append(new KeyedCodec<>("FuseDuration", Codec.FLOAT), (state, value) -> state.fuseDuration = value, state -> state.fuseDuration)
      .documentation("The duration between being primed and exploding.")
      .add()
      .build();
   protected boolean primedOnPlace;
   protected float fuseDuration;
   protected Instant detonationInstant;

   @Nonnull
   public static ComponentType<ChunkStore, BlockExplosive> getComponentType() {
      return ReactiveBlocksPlugin.get().getExplosiveComponentType();
   }

   public boolean getPrimedOnPlace() {
      return this.primedOnPlace;
   }

   public float getFuseDuration() {
      return this.fuseDuration;
   }

   public void setDetonationInstant(Instant startInstant) {
      this.detonationInstant = startInstant;
   }

   @NullableDecl
   public Instant getDetonationInstant() {
      return this.detonationInstant;
   }

   public BlockExplosive() {
   }

   public BlockExplosive(boolean primedOnPlace, float fuseDuration) {
      this.primedOnPlace = primedOnPlace;
      this.fuseDuration = fuseDuration;
   }

   public void doExplosion(
      @Nonnull BlockType blockType,
      @Nonnull Vector3d position,
      @Nonnull Rotation3f blockRotation,
      @Nonnull ComponentAccessor<EntityStore> entityAccessor,
      @Nonnull ComponentAccessor<ChunkStore> chunkAccessor
   ) {
      ExplosionConfig explosionConfig = blockType.getExplosionConfig();
      if (explosionConfig != null) {
         ExplosionUtils.performExplosion(
            ExplodeInteraction.DAMAGE_SOURCE_EXPLOSION, position, blockRotation, explosionConfig, null, entityAccessor, chunkAccessor
         );
      }
   }

   @NullableDecl
   @Override
   public Component<ChunkStore> clone() {
      return new BlockExplosive(this.primedOnPlace, this.fuseDuration);
   }
}
