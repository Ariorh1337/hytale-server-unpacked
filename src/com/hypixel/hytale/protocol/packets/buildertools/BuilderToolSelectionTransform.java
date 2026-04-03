package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public class BuilderToolSelectionTransform implements Packet, ToServerPacket {
   public static final int PACKET_ID = 405;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 80;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 80;
   public static final int MAX_SIZE = 80;
   @Nullable
   public Quaternionfc rotation;
   @Nullable
   public BlockPosition translationOffset;
   @Nullable
   public BlockPosition initialSelectionMin;
   @Nullable
   public BlockPosition initialSelectionMax;
   @Nonnull
   public Vector3fc initialRotationOrigin = PacketIO.ZERO_VECTOR3;
   public boolean cutOriginal;
   public boolean applyTransformationToSelectionMinMax;
   public boolean isExitingTransformMode;
   @Nullable
   public BlockPosition initialPastePointForClipboardPaste;

   @Override
   public int getId() {
      return 405;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public BuilderToolSelectionTransform() {
   }

   public BuilderToolSelectionTransform(
      @Nullable Quaternionfc rotation,
      @Nullable BlockPosition translationOffset,
      @Nullable BlockPosition initialSelectionMin,
      @Nullable BlockPosition initialSelectionMax,
      @Nonnull Vector3fc initialRotationOrigin,
      boolean cutOriginal,
      boolean applyTransformationToSelectionMinMax,
      boolean isExitingTransformMode,
      @Nullable BlockPosition initialPastePointForClipboardPaste
   ) {
      this.rotation = rotation;
      this.translationOffset = translationOffset;
      this.initialSelectionMin = initialSelectionMin;
      this.initialSelectionMax = initialSelectionMax;
      this.initialRotationOrigin = initialRotationOrigin;
      this.cutOriginal = cutOriginal;
      this.applyTransformationToSelectionMinMax = applyTransformationToSelectionMinMax;
      this.isExitingTransformMode = isExitingTransformMode;
      this.initialPastePointForClipboardPaste = initialPastePointForClipboardPaste;
   }

   public BuilderToolSelectionTransform(@Nonnull BuilderToolSelectionTransform other) {
      this.rotation = other.rotation;
      this.translationOffset = other.translationOffset;
      this.initialSelectionMin = other.initialSelectionMin;
      this.initialSelectionMax = other.initialSelectionMax;
      this.initialRotationOrigin = other.initialRotationOrigin;
      this.cutOriginal = other.cutOriginal;
      this.applyTransformationToSelectionMinMax = other.applyTransformationToSelectionMinMax;
      this.isExitingTransformMode = other.isExitingTransformMode;
      this.initialPastePointForClipboardPaste = other.initialPastePointForClipboardPaste;
   }

   @Nonnull
   public static BuilderToolSelectionTransform deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 80) {
         throw ProtocolException.bufferTooSmall("BuilderToolSelectionTransform", 80, buf.readableBytes() - offset);
      }

      BuilderToolSelectionTransform obj = new BuilderToolSelectionTransform();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         obj.rotation = PacketIO.readQuaternionf(buf, offset + 1);
      }

      if ((nullBits & 2) != 0) {
         obj.translationOffset = BlockPosition.deserialize(buf, offset + 17);
      }

      if ((nullBits & 4) != 0) {
         obj.initialSelectionMin = BlockPosition.deserialize(buf, offset + 29);
      }

      if ((nullBits & 8) != 0) {
         obj.initialSelectionMax = BlockPosition.deserialize(buf, offset + 41);
      }

      obj.initialRotationOrigin = PacketIO.readVector3f(buf, offset + 53);
      obj.cutOriginal = buf.getByte(offset + 65) != 0;
      obj.applyTransformationToSelectionMinMax = buf.getByte(offset + 66) != 0;
      obj.isExitingTransformMode = buf.getByte(offset + 67) != 0;
      if ((nullBits & 16) != 0) {
         obj.initialPastePointForClipboardPaste = BlockPosition.deserialize(buf, offset + 68);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 80;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.rotation != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.translationOffset != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.initialSelectionMin != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.initialSelectionMax != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.initialPastePointForClipboardPaste != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      if (this.rotation != null) {
         PacketIO.writeQuaternionf(buf, this.rotation);
      } else {
         buf.writeZero(16);
      }

      if (this.translationOffset != null) {
         this.translationOffset.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.initialSelectionMin != null) {
         this.initialSelectionMin.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.initialSelectionMax != null) {
         this.initialSelectionMax.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      PacketIO.writeVector3f(buf, this.initialRotationOrigin);
      buf.writeByte(this.cutOriginal ? 1 : 0);
      buf.writeByte(this.applyTransformationToSelectionMinMax ? 1 : 0);
      buf.writeByte(this.isExitingTransformMode ? 1 : 0);
      if (this.initialPastePointForClipboardPaste != null) {
         this.initialPastePointForClipboardPaste.serialize(buf);
      } else {
         buf.writeZero(12);
      }
   }

   @Override
   public int computeSize() {
      return 80;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 80) {
         return ValidationResult.error("Buffer too small: expected at least 80 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      return ValidationResult.OK;
   }

   public BuilderToolSelectionTransform clone() {
      BuilderToolSelectionTransform copy = new BuilderToolSelectionTransform();
      copy.rotation = this.rotation;
      copy.translationOffset = this.translationOffset != null ? this.translationOffset.clone() : null;
      copy.initialSelectionMin = this.initialSelectionMin != null ? this.initialSelectionMin.clone() : null;
      copy.initialSelectionMax = this.initialSelectionMax != null ? this.initialSelectionMax.clone() : null;
      copy.initialRotationOrigin = this.initialRotationOrigin;
      copy.cutOriginal = this.cutOriginal;
      copy.applyTransformationToSelectionMinMax = this.applyTransformationToSelectionMinMax;
      copy.isExitingTransformMode = this.isExitingTransformMode;
      copy.initialPastePointForClipboardPaste = this.initialPastePointForClipboardPaste != null ? this.initialPastePointForClipboardPaste.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BuilderToolSelectionTransform other)
            ? false
            : Objects.equals(this.rotation, other.rotation)
               && Objects.equals(this.translationOffset, other.translationOffset)
               && Objects.equals(this.initialSelectionMin, other.initialSelectionMin)
               && Objects.equals(this.initialSelectionMax, other.initialSelectionMax)
               && Objects.equals(this.initialRotationOrigin, other.initialRotationOrigin)
               && this.cutOriginal == other.cutOriginal
               && this.applyTransformationToSelectionMinMax == other.applyTransformationToSelectionMinMax
               && this.isExitingTransformMode == other.isExitingTransformMode
               && Objects.equals(this.initialPastePointForClipboardPaste, other.initialPastePointForClipboardPaste);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.rotation,
         this.translationOffset,
         this.initialSelectionMin,
         this.initialSelectionMax,
         this.initialRotationOrigin,
         this.cutOriginal,
         this.applyTransformationToSelectionMinMax,
         this.isExitingTransformMode,
         this.initialPastePointForClipboardPaste
      );
   }
}
