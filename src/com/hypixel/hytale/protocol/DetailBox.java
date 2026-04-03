package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3fc;

public class DetailBox {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 37;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 37;
   public static final int MAX_SIZE = 37;
   @Nonnull
   public Vector3fc offset = PacketIO.ZERO_VECTOR3;
   @Nullable
   public Hitbox box;

   public DetailBox() {
   }

   public DetailBox(@Nonnull Vector3fc offset, @Nullable Hitbox box) {
      this.offset = offset;
      this.box = box;
   }

   public DetailBox(@Nonnull DetailBox other) {
      this.offset = other.offset;
      this.box = other.box;
   }

   @Nonnull
   public static DetailBox deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 37) {
         throw ProtocolException.bufferTooSmall("DetailBox", 37, buf.readableBytes() - offset);
      }

      DetailBox obj = new DetailBox();
      byte nullBits = buf.getByte(offset);
      obj.offset = PacketIO.readVector3f(buf, offset + 1);
      if ((nullBits & 1) != 0) {
         obj.box = Hitbox.deserialize(buf, offset + 13);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 37;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.box != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      PacketIO.writeVector3f(buf, this.offset);
      if (this.box != null) {
         this.box.serialize(buf);
      } else {
         buf.writeZero(24);
      }
   }

   public int computeSize() {
      return 37;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 37) {
         return ValidationResult.error("Buffer too small: expected at least 37 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      return ValidationResult.OK;
   }

   public DetailBox clone() {
      DetailBox copy = new DetailBox();
      copy.offset = this.offset;
      copy.box = this.box != null ? this.box.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof DetailBox other) ? false : Objects.equals(this.offset, other.offset) && Objects.equals(this.box, other.box);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.offset, this.box);
   }
}
