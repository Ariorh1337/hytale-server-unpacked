package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3fc;

public class ParticleAttractor {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 85;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 85;
   public static final int MAX_SIZE = 85;
   @Nullable
   public Vector3fc position;
   @Nullable
   public Vector3fc radialAxis;
   public float trailPositionMultiplier;
   public float radius;
   public float radialAcceleration;
   public float radialTangentAcceleration;
   @Nullable
   public Vector3fc linearAcceleration;
   public float radialImpulse;
   public float radialTangentImpulse;
   @Nullable
   public Vector3fc linearImpulse;
   @Nullable
   public Vector3fc dampingMultiplier;

   public ParticleAttractor() {
   }

   public ParticleAttractor(
      @Nullable Vector3fc position,
      @Nullable Vector3fc radialAxis,
      float trailPositionMultiplier,
      float radius,
      float radialAcceleration,
      float radialTangentAcceleration,
      @Nullable Vector3fc linearAcceleration,
      float radialImpulse,
      float radialTangentImpulse,
      @Nullable Vector3fc linearImpulse,
      @Nullable Vector3fc dampingMultiplier
   ) {
      this.position = position;
      this.radialAxis = radialAxis;
      this.trailPositionMultiplier = trailPositionMultiplier;
      this.radius = radius;
      this.radialAcceleration = radialAcceleration;
      this.radialTangentAcceleration = radialTangentAcceleration;
      this.linearAcceleration = linearAcceleration;
      this.radialImpulse = radialImpulse;
      this.radialTangentImpulse = radialTangentImpulse;
      this.linearImpulse = linearImpulse;
      this.dampingMultiplier = dampingMultiplier;
   }

   public ParticleAttractor(@Nonnull ParticleAttractor other) {
      this.position = other.position;
      this.radialAxis = other.radialAxis;
      this.trailPositionMultiplier = other.trailPositionMultiplier;
      this.radius = other.radius;
      this.radialAcceleration = other.radialAcceleration;
      this.radialTangentAcceleration = other.radialTangentAcceleration;
      this.linearAcceleration = other.linearAcceleration;
      this.radialImpulse = other.radialImpulse;
      this.radialTangentImpulse = other.radialTangentImpulse;
      this.linearImpulse = other.linearImpulse;
      this.dampingMultiplier = other.dampingMultiplier;
   }

   @Nonnull
   public static ParticleAttractor deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 85) {
         throw ProtocolException.bufferTooSmall("ParticleAttractor", 85, buf.readableBytes() - offset);
      }

      ParticleAttractor obj = new ParticleAttractor();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         obj.position = PacketIO.readVector3f(buf, offset + 1);
      }

      if ((nullBits & 2) != 0) {
         obj.radialAxis = PacketIO.readVector3f(buf, offset + 13);
      }

      obj.trailPositionMultiplier = buf.getFloatLE(offset + 25);
      obj.radius = buf.getFloatLE(offset + 29);
      obj.radialAcceleration = buf.getFloatLE(offset + 33);
      obj.radialTangentAcceleration = buf.getFloatLE(offset + 37);
      if ((nullBits & 4) != 0) {
         obj.linearAcceleration = PacketIO.readVector3f(buf, offset + 41);
      }

      obj.radialImpulse = buf.getFloatLE(offset + 53);
      obj.radialTangentImpulse = buf.getFloatLE(offset + 57);
      if ((nullBits & 8) != 0) {
         obj.linearImpulse = PacketIO.readVector3f(buf, offset + 61);
      }

      if ((nullBits & 16) != 0) {
         obj.dampingMultiplier = PacketIO.readVector3f(buf, offset + 73);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 85;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.position != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.radialAxis != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.linearAcceleration != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.linearImpulse != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.dampingMultiplier != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      if (this.position != null) {
         PacketIO.writeVector3f(buf, this.position);
      } else {
         buf.writeZero(12);
      }

      if (this.radialAxis != null) {
         PacketIO.writeVector3f(buf, this.radialAxis);
      } else {
         buf.writeZero(12);
      }

      buf.writeFloatLE(this.trailPositionMultiplier);
      buf.writeFloatLE(this.radius);
      buf.writeFloatLE(this.radialAcceleration);
      buf.writeFloatLE(this.radialTangentAcceleration);
      if (this.linearAcceleration != null) {
         PacketIO.writeVector3f(buf, this.linearAcceleration);
      } else {
         buf.writeZero(12);
      }

      buf.writeFloatLE(this.radialImpulse);
      buf.writeFloatLE(this.radialTangentImpulse);
      if (this.linearImpulse != null) {
         PacketIO.writeVector3f(buf, this.linearImpulse);
      } else {
         buf.writeZero(12);
      }

      if (this.dampingMultiplier != null) {
         PacketIO.writeVector3f(buf, this.dampingMultiplier);
      } else {
         buf.writeZero(12);
      }
   }

   public int computeSize() {
      return 85;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 85) {
         return ValidationResult.error("Buffer too small: expected at least 85 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      return ValidationResult.OK;
   }

   public ParticleAttractor clone() {
      ParticleAttractor copy = new ParticleAttractor();
      copy.position = this.position;
      copy.radialAxis = this.radialAxis;
      copy.trailPositionMultiplier = this.trailPositionMultiplier;
      copy.radius = this.radius;
      copy.radialAcceleration = this.radialAcceleration;
      copy.radialTangentAcceleration = this.radialTangentAcceleration;
      copy.linearAcceleration = this.linearAcceleration;
      copy.radialImpulse = this.radialImpulse;
      copy.radialTangentImpulse = this.radialTangentImpulse;
      copy.linearImpulse = this.linearImpulse;
      copy.dampingMultiplier = this.dampingMultiplier;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ParticleAttractor other)
            ? false
            : Objects.equals(this.position, other.position)
               && Objects.equals(this.radialAxis, other.radialAxis)
               && this.trailPositionMultiplier == other.trailPositionMultiplier
               && this.radius == other.radius
               && this.radialAcceleration == other.radialAcceleration
               && this.radialTangentAcceleration == other.radialTangentAcceleration
               && Objects.equals(this.linearAcceleration, other.linearAcceleration)
               && this.radialImpulse == other.radialImpulse
               && this.radialTangentImpulse == other.radialTangentImpulse
               && Objects.equals(this.linearImpulse, other.linearImpulse)
               && Objects.equals(this.dampingMultiplier, other.dampingMultiplier);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.position,
         this.radialAxis,
         this.trailPositionMultiplier,
         this.radius,
         this.radialAcceleration,
         this.radialTangentAcceleration,
         this.linearAcceleration,
         this.radialImpulse,
         this.radialTangentImpulse,
         this.linearImpulse,
         this.dampingMultiplier
      );
   }
}
