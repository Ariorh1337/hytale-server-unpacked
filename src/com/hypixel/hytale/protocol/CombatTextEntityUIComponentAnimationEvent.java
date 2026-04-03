package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.joml.Vector2fc;

public class CombatTextEntityUIComponentAnimationEvent {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 33;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 33;
   public static final int MAX_SIZE = 33;
   @Nonnull
   public CombatTextEntityUIAnimationEventType type = CombatTextEntityUIAnimationEventType.Scale;
   public float startAt;
   public float endAt;
   public float startScale;
   public float endScale;
   @Nonnull
   public Vector2fc positionOffset = PacketIO.ZERO_VECTOR2;
   public float startOpacity;
   public float endOpacity;

   public CombatTextEntityUIComponentAnimationEvent() {
   }

   public CombatTextEntityUIComponentAnimationEvent(
      @Nonnull CombatTextEntityUIAnimationEventType type,
      float startAt,
      float endAt,
      float startScale,
      float endScale,
      @Nonnull Vector2fc positionOffset,
      float startOpacity,
      float endOpacity
   ) {
      this.type = type;
      this.startAt = startAt;
      this.endAt = endAt;
      this.startScale = startScale;
      this.endScale = endScale;
      this.positionOffset = positionOffset;
      this.startOpacity = startOpacity;
      this.endOpacity = endOpacity;
   }

   public CombatTextEntityUIComponentAnimationEvent(@Nonnull CombatTextEntityUIComponentAnimationEvent other) {
      this.type = other.type;
      this.startAt = other.startAt;
      this.endAt = other.endAt;
      this.startScale = other.startScale;
      this.endScale = other.endScale;
      this.positionOffset = other.positionOffset;
      this.startOpacity = other.startOpacity;
      this.endOpacity = other.endOpacity;
   }

   @Nonnull
   public static CombatTextEntityUIComponentAnimationEvent deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 33) {
         throw ProtocolException.bufferTooSmall("CombatTextEntityUIComponentAnimationEvent", 33, buf.readableBytes() - offset);
      }

      CombatTextEntityUIComponentAnimationEvent obj = new CombatTextEntityUIComponentAnimationEvent();
      obj.type = CombatTextEntityUIAnimationEventType.fromValue(buf.getByte(offset + 0));
      obj.startAt = buf.getFloatLE(offset + 1);
      obj.endAt = buf.getFloatLE(offset + 5);
      obj.startScale = buf.getFloatLE(offset + 9);
      obj.endScale = buf.getFloatLE(offset + 13);
      obj.positionOffset = PacketIO.readVector2f(buf, offset + 17);
      obj.startOpacity = buf.getFloatLE(offset + 25);
      obj.endOpacity = buf.getFloatLE(offset + 29);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 33;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeByte(this.type.getValue());
      buf.writeFloatLE(this.startAt);
      buf.writeFloatLE(this.endAt);
      buf.writeFloatLE(this.startScale);
      buf.writeFloatLE(this.endScale);
      PacketIO.writeVector2f(buf, this.positionOffset);
      buf.writeFloatLE(this.startOpacity);
      buf.writeFloatLE(this.endOpacity);
   }

   public int computeSize() {
      return 33;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 33) {
         return ValidationResult.error("Buffer too small: expected at least 33 bytes");
      }

      int v = buffer.getByte(offset + 0) & 255;
      return v >= 3 ? ValidationResult.error("Invalid CombatTextEntityUIAnimationEventType value for Type") : ValidationResult.OK;
   }

   public CombatTextEntityUIComponentAnimationEvent clone() {
      CombatTextEntityUIComponentAnimationEvent copy = new CombatTextEntityUIComponentAnimationEvent();
      copy.type = this.type;
      copy.startAt = this.startAt;
      copy.endAt = this.endAt;
      copy.startScale = this.startScale;
      copy.endScale = this.endScale;
      copy.positionOffset = this.positionOffset;
      copy.startOpacity = this.startOpacity;
      copy.endOpacity = this.endOpacity;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof CombatTextEntityUIComponentAnimationEvent other)
            ? false
            : Objects.equals(this.type, other.type)
               && this.startAt == other.startAt
               && this.endAt == other.endAt
               && this.startScale == other.startScale
               && this.endScale == other.endScale
               && Objects.equals(this.positionOffset, other.positionOffset)
               && this.startOpacity == other.startOpacity
               && this.endOpacity == other.endOpacity;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.startAt, this.endAt, this.startScale, this.endScale, this.positionOffset, this.startOpacity, this.endOpacity);
   }
}
