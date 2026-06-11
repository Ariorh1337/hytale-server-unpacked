package com.hypixel.hytale.server.core.modules.entity.teleport;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Rotation3fc;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class Teleport implements Component<EntityStore> {
   @Nullable
   private final World world;
   @Nonnull
   private final Vector3d position = new Vector3d();
   @Nonnull
   private final Rotation3f rotation = new Rotation3f();
   @Nullable
   private Rotation3f headRotation;
   private boolean resetVelocity = true;
   private CompletableFuture<Void> onComplete;

   @Nonnull
   public static ComponentType<EntityStore, Teleport> getComponentType() {
      return EntityModule.get().getTeleportComponentType();
   }

   public Teleport(@Nullable World world, @Nonnull Vector3dc position, @Nonnull Rotation3fc rotation) {
      this.world = world;
      this.position.set(position);
      this.rotation.set(rotation);
   }

   public Teleport(@Nonnull Vector3dc position, @Nonnull Rotation3fc rotation) {
      this.world = null;
      this.position.set(position);
      this.rotation.set(rotation);
   }

   public Teleport(@Nonnull Teleport t) {
      this.world = t.world;
      this.position.set(t.position);
      this.rotation.set(t.rotation);
      this.headRotation = t.headRotation == null ? null : new Rotation3f(t.headRotation);
      this.resetVelocity = t.resetVelocity;
   }

   @Nonnull
   public static Teleport createForPlayer(@Nullable World world, @Nonnull Transform transform) {
      Rotation3f headRotation = transform.getRotation();
      Rotation3f bodyRotation = new Rotation3f(0.0F, headRotation.yaw(), 0.0F);
      return new Teleport(world, transform.getPosition(), bodyRotation).setHeadRotation(headRotation);
   }

   @Nonnull
   public static Teleport createForPlayer(@Nullable World world, @Nonnull Vector3dc position, @Nonnull Rotation3fc rotation) {
      Rotation3f bodyRotation = new Rotation3f(0.0F, rotation.yaw(), 0.0F);
      return new Teleport(world, position, bodyRotation).setHeadRotation(rotation);
   }

   @Nonnull
   public static Teleport createForPlayer(@Nonnull Vector3dc position, @Nonnull Rotation3fc rotation) {
      return createForPlayer(null, position, rotation);
   }

   @Nonnull
   public static Teleport createForPlayer(@Nonnull Transform transform) {
      return createForPlayer(null, transform);
   }

   @Nonnull
   public static Teleport createExact(@Nonnull Vector3dc position, @Nonnull Rotation3fc bodyRotation, @Nonnull Rotation3fc headRotation) {
      return new Teleport(position, bodyRotation).setHeadRotation(headRotation);
   }

   @Nonnull
   public static Teleport createExact(@Nonnull Vector3dc position, @Nonnull Rotation3fc bodyRotation) {
      return new Teleport(position, bodyRotation);
   }

   public void setPosition(@Nonnull Vector3dc position) {
      this.position.set(position);
   }

   public void setRotation(@Nonnull Rotation3fc rotation) {
      this.rotation.set(rotation);
   }

   @Nonnull
   public Teleport setHeadRotation(@Nonnull Rotation3fc headRotation) {
      if (this.headRotation == null) {
         this.headRotation = new Rotation3f(headRotation);
      } else {
         this.headRotation.set(headRotation);
      }

      return this;
   }

   @Nonnull
   public Teleport withoutVelocityReset() {
      this.resetVelocity = false;
      return this;
   }

   public void setOnComplete(@Nonnull CompletableFuture<Void> onComplete) {
      this.onComplete = onComplete;
   }

   public CompletableFuture<Void> getOnComplete() {
      return this.onComplete;
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   @Nonnull
   public Vector3d getPosition() {
      return this.position;
   }

   @Nonnull
   public Rotation3f getRotation() {
      return this.rotation;
   }

   @Nullable
   public Rotation3f getHeadRotation() {
      return this.headRotation;
   }

   public boolean isResetVelocity() {
      return this.resetVelocity;
   }

   @Nonnull
   public Teleport clone() {
      return new Teleport(this);
   }
}
