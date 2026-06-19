package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3fc;

public class CylinderBreakShape extends BreakShape {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 27;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 27;
   public static final int MAX_SIZE = 27;
   public int width;
   public int height;
   public int depth;
   public boolean centered;
   @Nullable
   public Vector3fc offset;
   @Nonnull
   public BreakShapeOrientation orientation = BreakShapeOrientation.View;

   public CylinderBreakShape() {
   }

   public CylinderBreakShape(int width, int height, int depth, boolean centered, @Nullable Vector3fc offset, @Nonnull BreakShapeOrientation orientation) {
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.centered = centered;
      this.offset = offset;
      this.orientation = orientation;
   }

   public CylinderBreakShape(@Nonnull CylinderBreakShape other) {
      this.width = other.width;
      this.height = other.height;
      this.depth = other.depth;
      this.centered = other.centered;
      this.offset = other.offset;
      this.orientation = other.orientation;
   }

   @Nonnull
   public static CylinderBreakShape deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 27) {
         throw ProtocolException.bufferTooSmall("CylinderBreakShape", 27, buf.readableBytes() - offset);
      }

      CylinderBreakShape obj = new CylinderBreakShape();
      byte nullBits = buf.getByte(offset);
      obj.width = buf.getIntLE(offset + 1);
      obj.height = buf.getIntLE(offset + 5);
      obj.depth = buf.getIntLE(offset + 9);
      obj.centered = buf.getByte(offset + 13) != 0;
      if ((nullBits & 1) != 0) {
         obj.offset = PacketIO.readVector3f(buf, offset + 14);
      }

      obj.orientation = BreakShapeOrientation.fromValue(buf.getByte(offset + 26));
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 27;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 27L;
   }

   public static int getWidth(MemorySegment mem) {
      return getWidth(mem, 0);
   }

   public static int getWidth(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 1);
   }

   public static int getHeight(MemorySegment mem) {
      return getHeight(mem, 0);
   }

   public static int getHeight(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 5);
   }

   public static int getDepth(MemorySegment mem) {
      return getDepth(mem, 0);
   }

   public static int getDepth(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 9);
   }

   public static boolean getCentered(MemorySegment mem) {
      return getCentered(mem, 0);
   }

   public static boolean getCentered(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 13);
   }

   @Nullable
   public static Vector3fc getOffset(MemorySegment mem) {
      return getOffset(mem, 0);
   }

   @Nullable
   public static Vector3fc getOffset(MemorySegment mem, int offset) {
      return hasOffset(mem, offset) ? PacketIO.readVector3f(mem, offset + 14) : null;
   }

   public static BreakShapeOrientation getOrientation(MemorySegment mem) {
      return getOrientation(mem, 0);
   }

   public static BreakShapeOrientation getOrientation(MemorySegment mem, int offset) {
      return BreakShapeOrientation.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 26));
   }

   public static boolean hasOffset(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static CylinderBreakShape toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static CylinderBreakShape toObject(MemorySegment mem, int offset) {
      if (offset + 27 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("CylinderBreakShape", offset + 27, (int)mem.byteSize());
      } else {
         return new CylinderBreakShape(
            mem.get(PacketIO.PROTO_INT, offset + 1),
            mem.get(PacketIO.PROTO_INT, offset + 5),
            mem.get(PacketIO.PROTO_INT, offset + 9),
            mem.get(PacketIO.PROTO_BOOL, offset + 13),
            hasOffset(mem, offset) ? PacketIO.readVector3f(mem, offset + 14) : null,
            BreakShapeOrientation.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 26))
         );
      }
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.offset != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.width);
      buf.writeIntLE(this.height);
      buf.writeIntLE(this.depth);
      buf.writeByte(this.centered ? 1 : 0);
      if (this.offset != null) {
         PacketIO.writeVector3f(buf, this.offset);
      } else {
         buf.writeZero(12);
      }

      buf.writeByte(this.orientation.getValue());
      return buf.writerIndex() - startPos;
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.offset != null) {
         nullBits = (byte)(nullBits | 1);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_INT, offset + 1, this.width);
      mem.set(PacketIO.PROTO_INT, offset + 5, this.height);
      mem.set(PacketIO.PROTO_INT, offset + 9, this.depth);
      mem.set(PacketIO.PROTO_BOOL, offset + 13, this.centered);
      if (this.offset != null) {
         PacketIO.writeVector3f(mem, offset + 14, this.offset);
      } else {
         mem.asSlice(offset + 14, 12L).fill((byte)0);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 26, (byte)this.orientation.getValue());
      return 27;
   }

   @Override
   public int computeSize() {
      return 27;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 27) {
         return ValidationResult.error("Buffer too small: expected at least 27 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 26) & 255;
      return v >= 2 ? ValidationResult.error("Invalid BreakShapeOrientation value for Orientation") : ValidationResult.OK;
   }

   public CylinderBreakShape clone() {
      CylinderBreakShape copy = new CylinderBreakShape();
      copy.width = this.width;
      copy.height = this.height;
      copy.depth = this.depth;
      copy.centered = this.centered;
      copy.offset = this.offset;
      copy.orientation = this.orientation;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof CylinderBreakShape other)
            ? false
            : this.width == other.width
               && this.height == other.height
               && this.depth == other.depth
               && this.centered == other.centered
               && Objects.equals(this.offset, other.offset)
               && Objects.equals(this.orientation, other.orientation);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.width, this.height, this.depth, this.centered, this.offset, this.orientation);
   }
}
