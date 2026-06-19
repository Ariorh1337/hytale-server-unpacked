package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolColorAction implements Packet, ToServerPacket {
   public static final int PACKET_ID = 433;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 56;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 64;
   public static final int MAX_SIZE = 32768074;
   @Nonnull
   public ColorToolMode mode = ColorToolMode.Coloring;
   public int x;
   public int y;
   public int z;
   public int gradientStartX;
   public int gradientStartY;
   public int gradientStartZ;
   public int gradientEndX;
   public int gradientEndY;
   public int gradientEndZ;
   @Nullable
   public String gradientMaterials;
   @Nullable
   public String gradientShape;
   public float playerX;
   public float playerY;
   public float playerZ;
   public boolean shadingLighten;
   public boolean isHoldDownInteraction;
   public int undoGroupSize;

   @Override
   public int getId() {
      return 433;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public BuilderToolColorAction() {
   }

   public BuilderToolColorAction(
      @Nonnull ColorToolMode mode,
      int x,
      int y,
      int z,
      int gradientStartX,
      int gradientStartY,
      int gradientStartZ,
      int gradientEndX,
      int gradientEndY,
      int gradientEndZ,
      @Nullable String gradientMaterials,
      @Nullable String gradientShape,
      float playerX,
      float playerY,
      float playerZ,
      boolean shadingLighten,
      boolean isHoldDownInteraction,
      int undoGroupSize
   ) {
      this.mode = mode;
      this.x = x;
      this.y = y;
      this.z = z;
      this.gradientStartX = gradientStartX;
      this.gradientStartY = gradientStartY;
      this.gradientStartZ = gradientStartZ;
      this.gradientEndX = gradientEndX;
      this.gradientEndY = gradientEndY;
      this.gradientEndZ = gradientEndZ;
      this.gradientMaterials = gradientMaterials;
      this.gradientShape = gradientShape;
      this.playerX = playerX;
      this.playerY = playerY;
      this.playerZ = playerZ;
      this.shadingLighten = shadingLighten;
      this.isHoldDownInteraction = isHoldDownInteraction;
      this.undoGroupSize = undoGroupSize;
   }

   public BuilderToolColorAction(@Nonnull BuilderToolColorAction other) {
      this.mode = other.mode;
      this.x = other.x;
      this.y = other.y;
      this.z = other.z;
      this.gradientStartX = other.gradientStartX;
      this.gradientStartY = other.gradientStartY;
      this.gradientStartZ = other.gradientStartZ;
      this.gradientEndX = other.gradientEndX;
      this.gradientEndY = other.gradientEndY;
      this.gradientEndZ = other.gradientEndZ;
      this.gradientMaterials = other.gradientMaterials;
      this.gradientShape = other.gradientShape;
      this.playerX = other.playerX;
      this.playerY = other.playerY;
      this.playerZ = other.playerZ;
      this.shadingLighten = other.shadingLighten;
      this.isHoldDownInteraction = other.isHoldDownInteraction;
      this.undoGroupSize = other.undoGroupSize;
   }

   @Nonnull
   public static BuilderToolColorAction deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 64) {
         throw ProtocolException.bufferTooSmall("BuilderToolColorAction", 64, buf.readableBytes() - offset);
      }

      BuilderToolColorAction obj = new BuilderToolColorAction();
      byte nullBits = buf.getByte(offset);
      obj.mode = ColorToolMode.fromValue(buf.getByte(offset + 1));
      obj.x = buf.getIntLE(offset + 2);
      obj.y = buf.getIntLE(offset + 6);
      obj.z = buf.getIntLE(offset + 10);
      obj.gradientStartX = buf.getIntLE(offset + 14);
      obj.gradientStartY = buf.getIntLE(offset + 18);
      obj.gradientStartZ = buf.getIntLE(offset + 22);
      obj.gradientEndX = buf.getIntLE(offset + 26);
      obj.gradientEndY = buf.getIntLE(offset + 30);
      obj.gradientEndZ = buf.getIntLE(offset + 34);
      obj.playerX = buf.getFloatLE(offset + 38);
      obj.playerY = buf.getFloatLE(offset + 42);
      obj.playerZ = buf.getFloatLE(offset + 46);
      obj.shadingLighten = buf.getByte(offset + 50) != 0;
      obj.isHoldDownInteraction = buf.getByte(offset + 51) != 0;
      obj.undoGroupSize = buf.getIntLE(offset + 52);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 56);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 64) {
            throw ProtocolException.invalidOffset("GradientMaterials", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 64 + varPosBase0;
         int gradientMaterialsLen = VarInt.peek(buf, varPos0);
         if (gradientMaterialsLen < 0) {
            throw ProtocolException.invalidVarInt("GradientMaterials");
         }

         int gradientMaterialsVarIntLen = VarInt.size(gradientMaterialsLen);
         if (gradientMaterialsLen > 4096000) {
            throw ProtocolException.stringTooLong("GradientMaterials", gradientMaterialsLen, 4096000);
         }

         if (varPos0 + gradientMaterialsVarIntLen + gradientMaterialsLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("GradientMaterials", varPos0 + gradientMaterialsVarIntLen + gradientMaterialsLen, buf.readableBytes());
         }

         obj.gradientMaterials = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 60);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 64) {
            throw ProtocolException.invalidOffset("GradientShape", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 64 + varPosBase1;
         int gradientShapeLen = VarInt.peek(buf, varPos1);
         if (gradientShapeLen < 0) {
            throw ProtocolException.invalidVarInt("GradientShape");
         }

         int gradientShapeVarIntLen = VarInt.size(gradientShapeLen);
         if (gradientShapeLen > 4096000) {
            throw ProtocolException.stringTooLong("GradientShape", gradientShapeLen, 4096000);
         }

         if (varPos1 + gradientShapeVarIntLen + gradientShapeLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("GradientShape", varPos1 + gradientShapeVarIntLen + gradientShapeLen, buf.readableBytes());
         }

         obj.gradientShape = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 64;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 56);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 64) {
            throw ProtocolException.invalidOffset("GradientMaterials", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 64 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 60);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 64) {
            throw ProtocolException.invalidOffset("GradientShape", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 64 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 64L;
   }

   public static ColorToolMode getMode(MemorySegment mem) {
      return getMode(mem, 0);
   }

   public static ColorToolMode getMode(MemorySegment mem, int offset) {
      return ColorToolMode.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 1));
   }

   public static int getX(MemorySegment mem) {
      return getX(mem, 0);
   }

   public static int getX(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 2);
   }

   public static int getY(MemorySegment mem) {
      return getY(mem, 0);
   }

   public static int getY(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 6);
   }

   public static int getZ(MemorySegment mem) {
      return getZ(mem, 0);
   }

   public static int getZ(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 10);
   }

   public static int getGradientStartX(MemorySegment mem) {
      return getGradientStartX(mem, 0);
   }

   public static int getGradientStartX(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 14);
   }

   public static int getGradientStartY(MemorySegment mem) {
      return getGradientStartY(mem, 0);
   }

   public static int getGradientStartY(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 18);
   }

   public static int getGradientStartZ(MemorySegment mem) {
      return getGradientStartZ(mem, 0);
   }

   public static int getGradientStartZ(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 22);
   }

   public static int getGradientEndX(MemorySegment mem) {
      return getGradientEndX(mem, 0);
   }

   public static int getGradientEndX(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 26);
   }

   public static int getGradientEndY(MemorySegment mem) {
      return getGradientEndY(mem, 0);
   }

   public static int getGradientEndY(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 30);
   }

   public static int getGradientEndZ(MemorySegment mem) {
      return getGradientEndZ(mem, 0);
   }

   public static int getGradientEndZ(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 34);
   }

   @Nullable
   public static String getGradientMaterials(MemorySegment mem) {
      return getGradientMaterials(mem, 0);
   }

   @Nullable
   public static String getGradientMaterials(MemorySegment mem, int offset) {
      return hasGradientMaterials(mem, offset)
         ? PacketIO.readVarString("GradientMaterials", mem, offset + getValidatedOffset(mem, offset, 56, 64, "GradientMaterials"), 4096000, PacketIO.UTF8)
         : null;
   }

   @Nullable
   public static String getGradientShape(MemorySegment mem) {
      return getGradientShape(mem, 0);
   }

   @Nullable
   public static String getGradientShape(MemorySegment mem, int offset) {
      return hasGradientShape(mem, offset)
         ? PacketIO.readVarString("GradientShape", mem, offset + getValidatedOffset(mem, offset, 60, 64, "GradientShape"), 4096000, PacketIO.UTF8)
         : null;
   }

   public static float getPlayerX(MemorySegment mem) {
      return getPlayerX(mem, 0);
   }

   public static float getPlayerX(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 38);
   }

   public static float getPlayerY(MemorySegment mem) {
      return getPlayerY(mem, 0);
   }

   public static float getPlayerY(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 42);
   }

   public static float getPlayerZ(MemorySegment mem) {
      return getPlayerZ(mem, 0);
   }

   public static float getPlayerZ(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 46);
   }

   public static boolean getShadingLighten(MemorySegment mem) {
      return getShadingLighten(mem, 0);
   }

   public static boolean getShadingLighten(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 50);
   }

   public static boolean getIsHoldDownInteraction(MemorySegment mem) {
      return getIsHoldDownInteraction(mem, 0);
   }

   public static boolean getIsHoldDownInteraction(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 51);
   }

   public static int getUndoGroupSize(MemorySegment mem) {
      return getUndoGroupSize(mem, 0);
   }

   public static int getUndoGroupSize(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 52);
   }

   public static boolean hasGradientMaterials(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasGradientShape(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static BuilderToolColorAction toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static BuilderToolColorAction toObject(MemorySegment mem, int offset) {
      if (offset + 64 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("BuilderToolColorAction", offset + 64, (int)mem.byteSize());
      } else {
         return new BuilderToolColorAction(
            ColorToolMode.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 1)),
            mem.get(PacketIO.PROTO_INT, offset + 2),
            mem.get(PacketIO.PROTO_INT, offset + 6),
            mem.get(PacketIO.PROTO_INT, offset + 10),
            mem.get(PacketIO.PROTO_INT, offset + 14),
            mem.get(PacketIO.PROTO_INT, offset + 18),
            mem.get(PacketIO.PROTO_INT, offset + 22),
            mem.get(PacketIO.PROTO_INT, offset + 26),
            mem.get(PacketIO.PROTO_INT, offset + 30),
            mem.get(PacketIO.PROTO_INT, offset + 34),
            hasGradientMaterials(mem, offset)
               ? PacketIO.readVarString("GradientMaterials", mem, offset + getValidatedOffset(mem, offset, 56, 64, "GradientMaterials"), 4096000, PacketIO.UTF8)
               : null,
            hasGradientShape(mem, offset)
               ? PacketIO.readVarString("GradientShape", mem, offset + getValidatedOffset(mem, offset, 60, 64, "GradientShape"), 4096000, PacketIO.UTF8)
               : null,
            mem.get(PacketIO.PROTO_FLOAT, offset + 38),
            mem.get(PacketIO.PROTO_FLOAT, offset + 42),
            mem.get(PacketIO.PROTO_FLOAT, offset + 46),
            mem.get(PacketIO.PROTO_BOOL, offset + 50),
            mem.get(PacketIO.PROTO_BOOL, offset + 51),
            mem.get(PacketIO.PROTO_INT, offset + 52)
         );
      }
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.gradientMaterials != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.gradientShape != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.mode.getValue());
      buf.writeIntLE(this.x);
      buf.writeIntLE(this.y);
      buf.writeIntLE(this.z);
      buf.writeIntLE(this.gradientStartX);
      buf.writeIntLE(this.gradientStartY);
      buf.writeIntLE(this.gradientStartZ);
      buf.writeIntLE(this.gradientEndX);
      buf.writeIntLE(this.gradientEndY);
      buf.writeIntLE(this.gradientEndZ);
      buf.writeFloatLE(this.playerX);
      buf.writeFloatLE(this.playerY);
      buf.writeFloatLE(this.playerZ);
      buf.writeByte(this.shadingLighten ? 1 : 0);
      buf.writeByte(this.isHoldDownInteraction ? 1 : 0);
      buf.writeIntLE(this.undoGroupSize);
      int gradientMaterialsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int gradientShapeOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.gradientMaterials != null) {
         buf.setIntLE(gradientMaterialsOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.gradientMaterials, 4096000);
      } else {
         buf.setIntLE(gradientMaterialsOffsetSlot, -1);
      }

      if (this.gradientShape != null) {
         buf.setIntLE(gradientShapeOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.gradientShape, 4096000);
      } else {
         buf.setIntLE(gradientShapeOffsetSlot, -1);
      }
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.gradientMaterials != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.gradientShape != null) {
         nullBits = (byte)(nullBits | 2);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_BYTE, offset + 1, (byte)this.mode.getValue());
      mem.set(PacketIO.PROTO_INT, offset + 2, this.x);
      mem.set(PacketIO.PROTO_INT, offset + 6, this.y);
      mem.set(PacketIO.PROTO_INT, offset + 10, this.z);
      mem.set(PacketIO.PROTO_INT, offset + 14, this.gradientStartX);
      mem.set(PacketIO.PROTO_INT, offset + 18, this.gradientStartY);
      mem.set(PacketIO.PROTO_INT, offset + 22, this.gradientStartZ);
      mem.set(PacketIO.PROTO_INT, offset + 26, this.gradientEndX);
      mem.set(PacketIO.PROTO_INT, offset + 30, this.gradientEndY);
      mem.set(PacketIO.PROTO_INT, offset + 34, this.gradientEndZ);
      mem.set(PacketIO.PROTO_FLOAT, offset + 38, this.playerX);
      mem.set(PacketIO.PROTO_FLOAT, offset + 42, this.playerY);
      mem.set(PacketIO.PROTO_FLOAT, offset + 46, this.playerZ);
      mem.set(PacketIO.PROTO_BOOL, offset + 50, this.shadingLighten);
      mem.set(PacketIO.PROTO_BOOL, offset + 51, this.isHoldDownInteraction);
      mem.set(PacketIO.PROTO_INT, offset + 52, this.undoGroupSize);
      int varOffset = offset + 64;
      if (this.gradientMaterials != null) {
         mem.set(PacketIO.PROTO_INT, offset + 56, varOffset - offset - 64);
         varOffset += PacketIO.writeVarString(mem, varOffset, this.gradientMaterials, 4096000);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 56, -1);
      }

      if (this.gradientShape != null) {
         mem.set(PacketIO.PROTO_INT, offset + 60, varOffset - offset - 64);
         varOffset += PacketIO.writeVarString(mem, varOffset, this.gradientShape, 4096000);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 60, -1);
      }

      return varOffset - offset;
   }

   @Override
   public int computeSize() {
      int size = 64;
      if (this.gradientMaterials != null) {
         size += PacketIO.stringSize(this.gradientMaterials);
      }

      if (this.gradientShape != null) {
         size += PacketIO.stringSize(this.gradientShape);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 64) {
         return ValidationResult.error("Buffer too small: expected at least 64 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid ColorToolMode value for Mode");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 56);
         if (v < 0 || v > buffer.writerIndex() - offset - 64) {
            return ValidationResult.error("Invalid offset for GradientMaterials");
         }

         int pos = offset + 64 + v;
         int gradientMaterialsLen = VarInt.peek(buffer, pos);
         if (gradientMaterialsLen < 0) {
            return ValidationResult.error("Invalid string length for GradientMaterials");
         }

         if (gradientMaterialsLen > 4096000) {
            return ValidationResult.error("GradientMaterials exceeds max length 4096000");
         }

         pos += VarInt.size(gradientMaterialsLen);
         pos += gradientMaterialsLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading GradientMaterials");
         }
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 60);
         if (v < 0 || v > buffer.writerIndex() - offset - 64) {
            return ValidationResult.error("Invalid offset for GradientShape");
         }

         int pos = offset + 64 + v;
         int gradientShapeLen = VarInt.peek(buffer, pos);
         if (gradientShapeLen < 0) {
            return ValidationResult.error("Invalid string length for GradientShape");
         }

         if (gradientShapeLen > 4096000) {
            return ValidationResult.error("GradientShape exceeds max length 4096000");
         }

         pos += VarInt.size(gradientShapeLen);
         pos += gradientShapeLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading GradientShape");
         }
      }

      return ValidationResult.OK;
   }

   public BuilderToolColorAction clone() {
      BuilderToolColorAction copy = new BuilderToolColorAction();
      copy.mode = this.mode;
      copy.x = this.x;
      copy.y = this.y;
      copy.z = this.z;
      copy.gradientStartX = this.gradientStartX;
      copy.gradientStartY = this.gradientStartY;
      copy.gradientStartZ = this.gradientStartZ;
      copy.gradientEndX = this.gradientEndX;
      copy.gradientEndY = this.gradientEndY;
      copy.gradientEndZ = this.gradientEndZ;
      copy.gradientMaterials = this.gradientMaterials;
      copy.gradientShape = this.gradientShape;
      copy.playerX = this.playerX;
      copy.playerY = this.playerY;
      copy.playerZ = this.playerZ;
      copy.shadingLighten = this.shadingLighten;
      copy.isHoldDownInteraction = this.isHoldDownInteraction;
      copy.undoGroupSize = this.undoGroupSize;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BuilderToolColorAction other)
            ? false
            : Objects.equals(this.mode, other.mode)
               && this.x == other.x
               && this.y == other.y
               && this.z == other.z
               && this.gradientStartX == other.gradientStartX
               && this.gradientStartY == other.gradientStartY
               && this.gradientStartZ == other.gradientStartZ
               && this.gradientEndX == other.gradientEndX
               && this.gradientEndY == other.gradientEndY
               && this.gradientEndZ == other.gradientEndZ
               && Objects.equals(this.gradientMaterials, other.gradientMaterials)
               && Objects.equals(this.gradientShape, other.gradientShape)
               && this.playerX == other.playerX
               && this.playerY == other.playerY
               && this.playerZ == other.playerZ
               && this.shadingLighten == other.shadingLighten
               && this.isHoldDownInteraction == other.isHoldDownInteraction
               && this.undoGroupSize == other.undoGroupSize;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.mode,
         this.x,
         this.y,
         this.z,
         this.gradientStartX,
         this.gradientStartY,
         this.gradientStartZ,
         this.gradientEndX,
         this.gradientEndY,
         this.gradientEndZ,
         this.gradientMaterials,
         this.gradientShape,
         this.playerX,
         this.playerY,
         this.playerZ,
         this.shadingLighten,
         this.isHoldDownInteraction,
         this.undoGroupSize
      );
   }
}
