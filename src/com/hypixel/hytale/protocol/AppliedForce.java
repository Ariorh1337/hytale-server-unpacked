package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.joml.Vector3fc;

public class AppliedForce {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 17;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 17;
   @Nonnull
   public Vector3fc direction = PacketIO.ZERO_VECTOR3;
   public boolean adjustVertical;
   public float force;

   public AppliedForce() {
   }

   public AppliedForce(@Nonnull Vector3fc direction, boolean adjustVertical, float force) {
      this.direction = direction;
      this.adjustVertical = adjustVertical;
      this.force = force;
   }

   public AppliedForce(@Nonnull AppliedForce other) {
      this.direction = other.direction;
      this.adjustVertical = other.adjustVertical;
      this.force = other.force;
   }

   @Nonnull
   public static AppliedForce deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 17) {
         throw ProtocolException.bufferTooSmall("AppliedForce", 17, buf.readableBytes() - offset);
      }

      AppliedForce obj = new AppliedForce();
      obj.direction = PacketIO.readVector3f(buf, offset + 0);
      obj.adjustVertical = buf.getByte(offset + 12) != 0;
      obj.force = buf.getFloatLE(offset + 13);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 17;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      PacketIO.writeVector3f(buf, this.direction);
      buf.writeByte(this.adjustVertical ? 1 : 0);
      buf.writeFloatLE(this.force);
   }

   public int computeSize() {
      return 17;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 17 ? ValidationResult.error("Buffer too small: expected at least 17 bytes") : ValidationResult.OK;
   }

   public AppliedForce clone() {
      AppliedForce copy = new AppliedForce();
      copy.direction = this.direction;
      copy.adjustVertical = this.adjustVertical;
      copy.force = this.force;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AppliedForce other)
            ? false
            : Objects.equals(this.direction, other.direction) && this.adjustVertical == other.adjustVertical && this.force == other.force;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.direction, this.adjustVertical, this.force);
   }
}
