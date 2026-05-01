package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SegmentMusicContainer extends MusicContainer {
   public static final int NULLABLE_BIT_FIELD_SIZE = 2;
   public static final int FIXED_BLOCK_SIZE = 88;
   public static final int VARIABLE_FIELD_COUNT = 4;
   public static final int VARIABLE_BLOCK_START = 104;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public LayerPlacement[] layers;
   @Nullable
   public BarBeatDuration entryMarker;
   @Nullable
   public BarBeatDuration exitMarker;
   @Nullable
   public String[] stateNames;
   @Nullable
   public float[] stateVolumeData;
   public int defaultStateIndex;

   public SegmentMusicContainer() {
   }

   public SegmentMusicContainer(
      float volume,
      int loopCount,
      float weight,
      @Nullable Rangef silenceAfter,
      @Nullable Rangef exitSilence,
      float fadeInDuration,
      float fadeOutDuration,
      @Nonnull MusicTransitionType transitionType,
      float transitionDuration,
      boolean playToCompletion,
      @Nullable String nameTranslationKey,
      int audioCategoryIndex,
      @Nullable TempoSettings tempo,
      @Nullable LayerPlacement[] layers,
      @Nullable BarBeatDuration entryMarker,
      @Nullable BarBeatDuration exitMarker,
      @Nullable String[] stateNames,
      @Nullable float[] stateVolumeData,
      int defaultStateIndex
   ) {
      this.volume = volume;
      this.loopCount = loopCount;
      this.weight = weight;
      this.silenceAfter = silenceAfter;
      this.exitSilence = exitSilence;
      this.fadeInDuration = fadeInDuration;
      this.fadeOutDuration = fadeOutDuration;
      this.transitionType = transitionType;
      this.transitionDuration = transitionDuration;
      this.playToCompletion = playToCompletion;
      this.nameTranslationKey = nameTranslationKey;
      this.audioCategoryIndex = audioCategoryIndex;
      this.tempo = tempo;
      this.layers = layers;
      this.entryMarker = entryMarker;
      this.exitMarker = exitMarker;
      this.stateNames = stateNames;
      this.stateVolumeData = stateVolumeData;
      this.defaultStateIndex = defaultStateIndex;
   }

   public SegmentMusicContainer(@Nonnull SegmentMusicContainer other) {
      this.volume = other.volume;
      this.loopCount = other.loopCount;
      this.weight = other.weight;
      this.silenceAfter = other.silenceAfter;
      this.exitSilence = other.exitSilence;
      this.fadeInDuration = other.fadeInDuration;
      this.fadeOutDuration = other.fadeOutDuration;
      this.transitionType = other.transitionType;
      this.transitionDuration = other.transitionDuration;
      this.playToCompletion = other.playToCompletion;
      this.nameTranslationKey = other.nameTranslationKey;
      this.audioCategoryIndex = other.audioCategoryIndex;
      this.tempo = other.tempo;
      this.layers = other.layers;
      this.entryMarker = other.entryMarker;
      this.exitMarker = other.exitMarker;
      this.stateNames = other.stateNames;
      this.stateVolumeData = other.stateVolumeData;
      this.defaultStateIndex = other.defaultStateIndex;
   }

   @Nonnull
   public static SegmentMusicContainer deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 104) {
         throw ProtocolException.bufferTooSmall("SegmentMusicContainer", 104, buf.readableBytes() - offset);
      }

      SegmentMusicContainer obj = new SegmentMusicContainer();
      byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
      obj.volume = buf.getFloatLE(offset + 2);
      obj.loopCount = buf.getIntLE(offset + 6);
      obj.weight = buf.getFloatLE(offset + 10);
      if ((nullBits[0] & 1) != 0) {
         obj.silenceAfter = Rangef.deserialize(buf, offset + 14);
      }

      if ((nullBits[0] & 2) != 0) {
         obj.exitSilence = Rangef.deserialize(buf, offset + 22);
      }

      obj.fadeInDuration = buf.getFloatLE(offset + 30);
      obj.fadeOutDuration = buf.getFloatLE(offset + 34);
      obj.transitionType = MusicTransitionType.fromValue(buf.getByte(offset + 38));
      obj.transitionDuration = buf.getFloatLE(offset + 39);
      obj.playToCompletion = buf.getByte(offset + 43) != 0;
      obj.audioCategoryIndex = buf.getIntLE(offset + 44);
      if ((nullBits[0] & 4) != 0) {
         obj.tempo = TempoSettings.deserialize(buf, offset + 48);
      }

      if ((nullBits[0] & 8) != 0) {
         obj.entryMarker = BarBeatDuration.deserialize(buf, offset + 60);
      }

      if ((nullBits[0] & 16) != 0) {
         obj.exitMarker = BarBeatDuration.deserialize(buf, offset + 72);
      }

      obj.defaultStateIndex = buf.getIntLE(offset + 84);
      if ((nullBits[0] & 32) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 88);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("NameTranslationKey", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 104 + varPosBase0;
         int nameTranslationKeyLen = VarInt.peek(buf, varPos0);
         if (nameTranslationKeyLen < 0) {
            throw ProtocolException.invalidVarInt("NameTranslationKey");
         }

         int nameTranslationKeyVarIntLen = VarInt.size(nameTranslationKeyLen);
         if (nameTranslationKeyLen > 4096000) {
            throw ProtocolException.stringTooLong("NameTranslationKey", nameTranslationKeyLen, 4096000);
         }

         if (varPos0 + nameTranslationKeyVarIntLen + nameTranslationKeyLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("NameTranslationKey", varPos0 + nameTranslationKeyVarIntLen + nameTranslationKeyLen, buf.readableBytes());
         }

         obj.nameTranslationKey = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits[0] & 64) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 92);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Layers", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 104 + varPosBase1;
         int layersCount = VarInt.peek(buf, varPos1);
         if (layersCount < 0) {
            throw ProtocolException.invalidVarInt("Layers");
         }

         int varIntLen = VarInt.size(layersCount);
         if (layersCount > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", layersCount, 4096000);
         }

         if (varPos1 + varIntLen + layersCount * 17L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Layers", varPos1 + varIntLen + layersCount * 17, buf.readableBytes());
         }

         obj.layers = new LayerPlacement[layersCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < layersCount; i++) {
            obj.layers[i] = LayerPlacement.deserialize(buf, elemPos);
            elemPos += LayerPlacement.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 96);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("StateNames", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 104 + varPosBase2;
         int stateNamesCount = VarInt.peek(buf, varPos2);
         if (stateNamesCount < 0) {
            throw ProtocolException.invalidVarInt("StateNames");
         }

         int varIntLen = VarInt.size(stateNamesCount);
         if (stateNamesCount > 4096000) {
            throw ProtocolException.arrayTooLong("StateNames", stateNamesCount, 4096000);
         }

         if (varPos2 + varIntLen + stateNamesCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("StateNames", varPos2 + varIntLen + stateNamesCount * 1, buf.readableBytes());
         }

         obj.stateNames = new String[stateNamesCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < stateNamesCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("stateNames[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("stateNames[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("stateNames[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.stateNames[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 100);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("StateVolumeData", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 104 + varPosBase3;
         int stateVolumeDataCount = VarInt.peek(buf, varPos3);
         if (stateVolumeDataCount < 0) {
            throw ProtocolException.invalidVarInt("StateVolumeData");
         }

         int varIntLen = VarInt.size(stateVolumeDataCount);
         if (stateVolumeDataCount > 4096000) {
            throw ProtocolException.arrayTooLong("StateVolumeData", stateVolumeDataCount, 4096000);
         }

         if (varPos3 + varIntLen + stateVolumeDataCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("StateVolumeData", varPos3 + varIntLen + stateVolumeDataCount * 4, buf.readableBytes());
         }

         obj.stateVolumeData = new float[stateVolumeDataCount];

         for (int i = 0; i < stateVolumeDataCount; i++) {
            obj.stateVolumeData[i] = buf.getFloatLE(varPos3 + varIntLen + i * 4);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
      int maxEnd = 104;
      if ((nullBits[0] & 32) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 88);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("NameTranslationKey", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 104 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 92);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Layers", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 104 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos1 += LayerPlacement.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 96);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("StateNames", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 104 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos2);
            pos2 += VarInt.size(sl) + sl;
         }

         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 100);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("StateVolumeData", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 104 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen) + arrLen * 4;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 104L;
   }

   public static float getVolume(MemorySegment mem) {
      return getVolume(mem, 0);
   }

   public static float getVolume(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 2);
   }

   public static int getLoopCount(MemorySegment mem) {
      return getLoopCount(mem, 0);
   }

   public static int getLoopCount(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 6);
   }

   public static float getWeight(MemorySegment mem) {
      return getWeight(mem, 0);
   }

   public static float getWeight(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 10);
   }

   @Nullable
   public static Rangef getSilenceAfter(MemorySegment mem) {
      return getSilenceAfter(mem, 0);
   }

   @Nullable
   public static Rangef getSilenceAfter(MemorySegment mem, int offset) {
      return hasSilenceAfter(mem, offset) ? Rangef.toObject(mem, offset + 14) : null;
   }

   @Nullable
   public static Rangef getExitSilence(MemorySegment mem) {
      return getExitSilence(mem, 0);
   }

   @Nullable
   public static Rangef getExitSilence(MemorySegment mem, int offset) {
      return hasExitSilence(mem, offset) ? Rangef.toObject(mem, offset + 22) : null;
   }

   public static float getFadeInDuration(MemorySegment mem) {
      return getFadeInDuration(mem, 0);
   }

   public static float getFadeInDuration(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 30);
   }

   public static float getFadeOutDuration(MemorySegment mem) {
      return getFadeOutDuration(mem, 0);
   }

   public static float getFadeOutDuration(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 34);
   }

   public static MusicTransitionType getTransitionType(MemorySegment mem) {
      return getTransitionType(mem, 0);
   }

   public static MusicTransitionType getTransitionType(MemorySegment mem, int offset) {
      return MusicTransitionType.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 38));
   }

   public static float getTransitionDuration(MemorySegment mem) {
      return getTransitionDuration(mem, 0);
   }

   public static float getTransitionDuration(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 39);
   }

   public static boolean getPlayToCompletion(MemorySegment mem) {
      return getPlayToCompletion(mem, 0);
   }

   public static boolean getPlayToCompletion(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 43);
   }

   @Nullable
   public static String getNameTranslationKey(MemorySegment mem) {
      return getNameTranslationKey(mem, 0);
   }

   @Nullable
   public static String getNameTranslationKey(MemorySegment mem, int offset) {
      return hasNameTranslationKey(mem, offset)
         ? PacketIO.readVarString("NameTranslationKey", mem, offset + getValidatedOffset(mem, offset, 88, 104, "NameTranslationKey"), 4096000, PacketIO.UTF8)
         : null;
   }

   public static int getAudioCategoryIndex(MemorySegment mem) {
      return getAudioCategoryIndex(mem, 0);
   }

   public static int getAudioCategoryIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 44);
   }

   @Nullable
   public static TempoSettings getTempo(MemorySegment mem) {
      return getTempo(mem, 0);
   }

   @Nullable
   public static TempoSettings getTempo(MemorySegment mem, int offset) {
      return hasTempo(mem, offset) ? TempoSettings.toObject(mem, offset + 48) : null;
   }

   @Nullable
   public static LayerPlacement[] getLayers(MemorySegment mem) {
      return getLayers(mem, 0);
   }

   @Nullable
   public static LayerPlacement[] getLayers(MemorySegment mem, int offset) {
      if (!hasLayers(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 92, 104, "Layers");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Layers", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("Layers", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("Layers", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      LayerPlacement[] data = new LayerPlacement[len];

      for (int i = 0; i < len; i++) {
         data[i] = LayerPlacement.toObject(mem, off);
         off += data[i].computeSize();
      }

      return data;
   }

   @Nullable
   public static BarBeatDuration getEntryMarker(MemorySegment mem) {
      return getEntryMarker(mem, 0);
   }

   @Nullable
   public static BarBeatDuration getEntryMarker(MemorySegment mem, int offset) {
      return hasEntryMarker(mem, offset) ? BarBeatDuration.toObject(mem, offset + 60) : null;
   }

   @Nullable
   public static BarBeatDuration getExitMarker(MemorySegment mem) {
      return getExitMarker(mem, 0);
   }

   @Nullable
   public static BarBeatDuration getExitMarker(MemorySegment mem, int offset) {
      return hasExitMarker(mem, offset) ? BarBeatDuration.toObject(mem, offset + 72) : null;
   }

   @Nullable
   public static String[] getStateNames(MemorySegment mem) {
      return getStateNames(mem, 0);
   }

   @Nullable
   public static String[] getStateNames(MemorySegment mem, int offset) {
      if (!hasStateNames(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 96, 104, "StateNames");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("StateNames", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("StateNames", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("StateNames", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      String[] data = new String[len];

      for (int i = 0; i < len; i++) {
         long sp = VarInt.getWithLength(mem, off);
         int n = (int)sp + (int)(sp >>> 32);
         data[i] = PacketIO.readVarString("StateNames", mem, off, 16384000, PacketIO.UTF8);
         off += n;
      }

      return data;
   }

   @Nullable
   public static float[] getStateVolumeData(MemorySegment mem) {
      return getStateVolumeData(mem, 0);
   }

   @Nullable
   public static float[] getStateVolumeData(MemorySegment mem, int offset) {
      if (!hasStateVolumeData(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 100, 104, "StateVolumeData");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("StateVolumeData", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("StateVolumeData", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len * 4L > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("StateVolumeData", off + lenOffset + len * 4, (int)mem.byteSize());
      }

      off += lenOffset;
      float[] data = new float[len];
      MemorySegment.copy(mem, PacketIO.PROTO_FLOAT, off, data, 0, len);
      return data;
   }

   public static int getDefaultStateIndex(MemorySegment mem) {
      return getDefaultStateIndex(mem, 0);
   }

   public static int getDefaultStateIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 84);
   }

   public static boolean hasSilenceAfter(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasExitSilence(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   public static boolean hasTempo(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 4) != 0;
   }

   public static boolean hasEntryMarker(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 8) != 0;
   }

   public static boolean hasExitMarker(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 16) != 0;
   }

   public static boolean hasNameTranslationKey(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 32) != 0;
   }

   public static boolean hasLayers(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 64) != 0;
   }

   public static boolean hasStateNames(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 128) != 0;
   }

   public static boolean hasStateVolumeData(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 1);
      return (b & 1) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static SegmentMusicContainer toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static SegmentMusicContainer toObject(MemorySegment mem, int offset) {
      if (offset + 104 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("SegmentMusicContainer", offset + 104, (int)mem.byteSize());
      }

      LayerPlacement[] layers = null;
      if (hasLayers(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 92, 104, "Layers");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Layers", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("Layers", off + lenOffset + len, (int)mem.byteSize());
         }

         off += lenOffset;
         layers = new LayerPlacement[len];

         for (int i = 0; i < len; i++) {
            layers[i] = LayerPlacement.toObject(mem, off);
            off += layers[i].computeSize();
         }
      }

      String[] stateNames = null;
      if (hasStateNames(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 96, 104, "StateNames");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("StateNames", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("StateNames", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("StateNames", off + lenOffset + len, (int)mem.byteSize());
         }

         off += lenOffset;
         stateNames = new String[len];

         for (int i = 0; i < len; i++) {
            long sp = VarInt.getWithLength(mem, off);
            int n = (int)sp + (int)(sp >>> 32);
            stateNames[i] = PacketIO.readVarString("StateNames", mem, off, 16384000, PacketIO.UTF8);
            off += n;
         }
      }

      float[] stateVolumeData = null;
      if (hasStateVolumeData(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 100, 104, "StateVolumeData");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("StateVolumeData", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("StateVolumeData", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len * 4L > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("StateVolumeData", off + lenOffset + len * 4, (int)mem.byteSize());
         }

         off += lenOffset;
         stateVolumeData = new float[len];
         MemorySegment.copy(mem, PacketIO.PROTO_FLOAT, off, stateVolumeData, 0, len);
      }

      return new SegmentMusicContainer(
         mem.get(PacketIO.PROTO_FLOAT, offset + 2),
         mem.get(PacketIO.PROTO_INT, offset + 6),
         mem.get(PacketIO.PROTO_FLOAT, offset + 10),
         hasSilenceAfter(mem, offset) ? Rangef.toObject(mem, offset + 14) : null,
         hasExitSilence(mem, offset) ? Rangef.toObject(mem, offset + 22) : null,
         mem.get(PacketIO.PROTO_FLOAT, offset + 30),
         mem.get(PacketIO.PROTO_FLOAT, offset + 34),
         MusicTransitionType.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 38)),
         mem.get(PacketIO.PROTO_FLOAT, offset + 39),
         mem.get(PacketIO.PROTO_BOOL, offset + 43),
         hasNameTranslationKey(mem, offset)
            ? PacketIO.readVarString("NameTranslationKey", mem, offset + getValidatedOffset(mem, offset, 88, 104, "NameTranslationKey"), 4096000, PacketIO.UTF8)
            : null,
         mem.get(PacketIO.PROTO_INT, offset + 44),
         hasTempo(mem, offset) ? TempoSettings.toObject(mem, offset + 48) : null,
         layers,
         hasEntryMarker(mem, offset) ? BarBeatDuration.toObject(mem, offset + 60) : null,
         hasExitMarker(mem, offset) ? BarBeatDuration.toObject(mem, offset + 72) : null,
         stateNames,
         stateVolumeData,
         mem.get(PacketIO.PROTO_INT, offset + 84)
      );
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte[] nullBits = new byte[2];
      if (this.silenceAfter != null) {
         nullBits[0] = (byte)(nullBits[0] | 1);
      }

      if (this.exitSilence != null) {
         nullBits[0] = (byte)(nullBits[0] | 2);
      }

      if (this.tempo != null) {
         nullBits[0] = (byte)(nullBits[0] | 4);
      }

      if (this.entryMarker != null) {
         nullBits[0] = (byte)(nullBits[0] | 8);
      }

      if (this.exitMarker != null) {
         nullBits[0] = (byte)(nullBits[0] | 16);
      }

      if (this.nameTranslationKey != null) {
         nullBits[0] = (byte)(nullBits[0] | 32);
      }

      if (this.layers != null) {
         nullBits[0] = (byte)(nullBits[0] | 64);
      }

      if (this.stateNames != null) {
         nullBits[0] = (byte)(nullBits[0] | 128);
      }

      if (this.stateVolumeData != null) {
         nullBits[1] = (byte)(nullBits[1] | 1);
      }

      buf.writeBytes(nullBits);
      buf.writeFloatLE(this.volume);
      buf.writeIntLE(this.loopCount);
      buf.writeFloatLE(this.weight);
      if (this.silenceAfter != null) {
         this.silenceAfter.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      if (this.exitSilence != null) {
         this.exitSilence.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      buf.writeFloatLE(this.fadeInDuration);
      buf.writeFloatLE(this.fadeOutDuration);
      buf.writeByte(this.transitionType.getValue());
      buf.writeFloatLE(this.transitionDuration);
      buf.writeByte(this.playToCompletion ? 1 : 0);
      buf.writeIntLE(this.audioCategoryIndex);
      if (this.tempo != null) {
         this.tempo.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.entryMarker != null) {
         this.entryMarker.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.exitMarker != null) {
         this.exitMarker.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      buf.writeIntLE(this.defaultStateIndex);
      int nameTranslationKeyOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int layersOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int stateNamesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int stateVolumeDataOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.nameTranslationKey != null) {
         buf.setIntLE(nameTranslationKeyOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.nameTranslationKey, 4096000);
      } else {
         buf.setIntLE(nameTranslationKeyOffsetSlot, -1);
      }

      if (this.layers != null) {
         buf.setIntLE(layersOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.layers.length > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", this.layers.length, 4096000);
         }

         VarInt.write(buf, this.layers.length);

         for (LayerPlacement item : this.layers) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(layersOffsetSlot, -1);
      }

      if (this.stateNames != null) {
         buf.setIntLE(stateNamesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.stateNames.length > 4096000) {
            throw ProtocolException.arrayTooLong("StateNames", this.stateNames.length, 4096000);
         }

         VarInt.write(buf, this.stateNames.length);

         for (String item : this.stateNames) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(stateNamesOffsetSlot, -1);
      }

      if (this.stateVolumeData != null) {
         buf.setIntLE(stateVolumeDataOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.stateVolumeData.length > 4096000) {
            throw ProtocolException.arrayTooLong("StateVolumeData", this.stateVolumeData.length, 4096000);
         }

         VarInt.write(buf, this.stateVolumeData.length);

         for (float item : this.stateVolumeData) {
            buf.writeFloatLE(item);
         }
      } else {
         buf.setIntLE(stateVolumeDataOffsetSlot, -1);
      }

      return buf.writerIndex() - startPos;
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.silenceAfter != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.exitSilence != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.tempo != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.entryMarker != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.exitMarker != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.nameTranslationKey != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.layers != null) {
         nullBits = (byte)(nullBits | 64);
      }

      if (this.stateNames != null) {
         nullBits = (byte)(nullBits | 128);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      nullBits = 0;
      if (this.stateVolumeData != null) {
         nullBits = (byte)(nullBits | 1);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 1, nullBits);
      mem.set(PacketIO.PROTO_FLOAT, offset + 2, this.volume);
      mem.set(PacketIO.PROTO_INT, offset + 6, this.loopCount);
      mem.set(PacketIO.PROTO_FLOAT, offset + 10, this.weight);
      if (this.silenceAfter != null) {
         this.silenceAfter.serialize(mem, offset + 14);
      } else {
         mem.asSlice(offset + 14, 8L).fill((byte)0);
      }

      if (this.exitSilence != null) {
         this.exitSilence.serialize(mem, offset + 22);
      } else {
         mem.asSlice(offset + 22, 8L).fill((byte)0);
      }

      mem.set(PacketIO.PROTO_FLOAT, offset + 30, this.fadeInDuration);
      mem.set(PacketIO.PROTO_FLOAT, offset + 34, this.fadeOutDuration);
      mem.set(PacketIO.PROTO_BYTE, offset + 38, (byte)this.transitionType.getValue());
      mem.set(PacketIO.PROTO_FLOAT, offset + 39, this.transitionDuration);
      mem.set(PacketIO.PROTO_BOOL, offset + 43, this.playToCompletion);
      mem.set(PacketIO.PROTO_INT, offset + 44, this.audioCategoryIndex);
      if (this.tempo != null) {
         this.tempo.serialize(mem, offset + 48);
      } else {
         mem.asSlice(offset + 48, 12L).fill((byte)0);
      }

      if (this.entryMarker != null) {
         this.entryMarker.serialize(mem, offset + 60);
      } else {
         mem.asSlice(offset + 60, 12L).fill((byte)0);
      }

      if (this.exitMarker != null) {
         this.exitMarker.serialize(mem, offset + 72);
      } else {
         mem.asSlice(offset + 72, 12L).fill((byte)0);
      }

      mem.set(PacketIO.PROTO_INT, offset + 84, this.defaultStateIndex);
      int varOffset = offset + 104;
      if (this.nameTranslationKey != null) {
         mem.set(PacketIO.PROTO_INT, offset + 88, varOffset - offset - 104);
         varOffset += PacketIO.writeVarString(mem, varOffset, this.nameTranslationKey, 4096000);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 88, -1);
      }

      if (this.layers != null) {
         mem.set(PacketIO.PROTO_INT, offset + 92, varOffset - offset - 104);
         if (this.layers.length > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", this.layers.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.layers.length);
         int layersValueOffset = 0;

         for (int i = 0; i < this.layers.length; i++) {
            layersValueOffset += this.layers[i].serialize(mem, varOffset + layersValueOffset);
         }

         varOffset += layersValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 92, -1);
      }

      if (this.stateNames != null) {
         mem.set(PacketIO.PROTO_INT, offset + 96, varOffset - offset - 104);
         if (this.stateNames.length > 4096000) {
            throw ProtocolException.arrayTooLong("StateNames", this.stateNames.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.stateNames.length);
         int stateNamesValueOffset = 0;

         for (int i = 0; i < this.stateNames.length; i++) {
            stateNamesValueOffset += PacketIO.writeVarString(mem, varOffset + stateNamesValueOffset, this.stateNames[i], 16384000);
         }

         varOffset += stateNamesValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 96, -1);
      }

      if (this.stateVolumeData != null) {
         mem.set(PacketIO.PROTO_INT, offset + 100, varOffset - offset - 104);
         if (this.stateVolumeData.length > 4096000) {
            throw ProtocolException.arrayTooLong("StateVolumeData", this.stateVolumeData.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.stateVolumeData.length);
         MemorySegment.copy(this.stateVolumeData, 0, mem, PacketIO.PROTO_FLOAT, varOffset, this.stateVolumeData.length);
         varOffset += this.stateVolumeData.length * 4;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 100, -1);
      }

      return varOffset - offset;
   }

   @Override
   public int computeSize() {
      int size = 104;
      if (this.nameTranslationKey != null) {
         size += PacketIO.stringSize(this.nameTranslationKey);
      }

      if (this.layers != null) {
         int layersSize = 0;

         for (LayerPlacement elem : this.layers) {
            layersSize += elem.computeSize();
         }

         size += VarInt.size(this.layers.length) + layersSize;
      }

      if (this.stateNames != null) {
         int stateNamesSize = 0;

         for (String elem : this.stateNames) {
            stateNamesSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.stateNames.length) + stateNamesSize;
      }

      if (this.stateVolumeData != null) {
         size += VarInt.size(this.stateVolumeData.length) + this.stateVolumeData.length * 4;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 104) {
         return ValidationResult.error("Buffer too small: expected at least 104 bytes");
      }

      byte[] nullBits = PacketIO.readBytes(buffer, offset, 2);
      int v = buffer.getByte(offset + 38) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid MusicTransitionType value for TransitionType");
      }

      if ((nullBits[0] & 32) != 0) {
         v = buffer.getIntLE(offset + 88);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for NameTranslationKey");
         }

         int pos = offset + 104 + v;
         int nameTranslationKeyLen = VarInt.peek(buffer, pos);
         if (nameTranslationKeyLen < 0) {
            return ValidationResult.error("Invalid string length for NameTranslationKey");
         }

         if (nameTranslationKeyLen > 4096000) {
            return ValidationResult.error("NameTranslationKey exceeds max length 4096000");
         }

         pos += VarInt.size(nameTranslationKeyLen);
         pos += nameTranslationKeyLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading NameTranslationKey");
         }
      }

      if ((nullBits[0] & 64) != 0) {
         v = buffer.getIntLE(offset + 92);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Layers");
         }

         int pos = offset + 104 + v;
         int layersCount = VarInt.peek(buffer, pos);
         if (layersCount < 0) {
            return ValidationResult.error("Invalid array count for Layers");
         }

         if (layersCount > 4096000) {
            return ValidationResult.error("Layers exceeds max length 4096000");
         }

         pos += VarInt.size(layersCount);

         for (int i = 0; i < layersCount; i++) {
            ValidationResult structResult = LayerPlacement.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid LayerPlacement in Layers[" + i + "]: " + structResult.error());
            }

            pos += LayerPlacement.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[0] & 128) != 0) {
         v = buffer.getIntLE(offset + 96);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for StateNames");
         }

         int pos = offset + 104 + v;
         int stateNamesCount = VarInt.peek(buffer, pos);
         if (stateNamesCount < 0) {
            return ValidationResult.error("Invalid array count for StateNames");
         }

         if (stateNamesCount > 4096000) {
            return ValidationResult.error("StateNames exceeds max length 4096000");
         }

         pos += VarInt.size(stateNamesCount);

         for (int i = 0; i < stateNamesCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in StateNames");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in StateNames");
            }
         }
      }

      if ((nullBits[1] & 1) != 0) {
         v = buffer.getIntLE(offset + 100);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for StateVolumeData");
         }

         int pos = offset + 104 + v;
         int stateVolumeDataCount = VarInt.peek(buffer, pos);
         if (stateVolumeDataCount < 0) {
            return ValidationResult.error("Invalid array count for StateVolumeData");
         }

         if (stateVolumeDataCount > 4096000) {
            return ValidationResult.error("StateVolumeData exceeds max length 4096000");
         }

         pos += VarInt.size(stateVolumeDataCount);
         pos += stateVolumeDataCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading StateVolumeData");
         }
      }

      return ValidationResult.OK;
   }

   public SegmentMusicContainer clone() {
      SegmentMusicContainer copy = new SegmentMusicContainer();
      copy.volume = this.volume;
      copy.loopCount = this.loopCount;
      copy.weight = this.weight;
      copy.silenceAfter = this.silenceAfter != null ? this.silenceAfter.clone() : null;
      copy.exitSilence = this.exitSilence != null ? this.exitSilence.clone() : null;
      copy.fadeInDuration = this.fadeInDuration;
      copy.fadeOutDuration = this.fadeOutDuration;
      copy.transitionType = this.transitionType;
      copy.transitionDuration = this.transitionDuration;
      copy.playToCompletion = this.playToCompletion;
      copy.nameTranslationKey = this.nameTranslationKey;
      copy.audioCategoryIndex = this.audioCategoryIndex;
      copy.tempo = this.tempo != null ? this.tempo.clone() : null;
      copy.layers = this.layers != null ? Arrays.stream(this.layers).map(e -> e.clone()).toArray(LayerPlacement[]::new) : null;
      copy.entryMarker = this.entryMarker != null ? this.entryMarker.clone() : null;
      copy.exitMarker = this.exitMarker != null ? this.exitMarker.clone() : null;
      copy.stateNames = this.stateNames != null ? Arrays.copyOf(this.stateNames, this.stateNames.length) : null;
      copy.stateVolumeData = this.stateVolumeData != null ? Arrays.copyOf(this.stateVolumeData, this.stateVolumeData.length) : null;
      copy.defaultStateIndex = this.defaultStateIndex;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SegmentMusicContainer other)
            ? false
            : this.volume == other.volume
               && this.loopCount == other.loopCount
               && this.weight == other.weight
               && Objects.equals(this.silenceAfter, other.silenceAfter)
               && Objects.equals(this.exitSilence, other.exitSilence)
               && this.fadeInDuration == other.fadeInDuration
               && this.fadeOutDuration == other.fadeOutDuration
               && Objects.equals(this.transitionType, other.transitionType)
               && this.transitionDuration == other.transitionDuration
               && this.playToCompletion == other.playToCompletion
               && Objects.equals(this.nameTranslationKey, other.nameTranslationKey)
               && this.audioCategoryIndex == other.audioCategoryIndex
               && Objects.equals(this.tempo, other.tempo)
               && Arrays.equals(this.layers, other.layers)
               && Objects.equals(this.entryMarker, other.entryMarker)
               && Objects.equals(this.exitMarker, other.exitMarker)
               && Arrays.equals(this.stateNames, other.stateNames)
               && Arrays.equals(this.stateVolumeData, other.stateVolumeData)
               && this.defaultStateIndex == other.defaultStateIndex;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Float.hashCode(this.volume);
      result = 31 * result + Integer.hashCode(this.loopCount);
      result = 31 * result + Float.hashCode(this.weight);
      result = 31 * result + Objects.hashCode(this.silenceAfter);
      result = 31 * result + Objects.hashCode(this.exitSilence);
      result = 31 * result + Float.hashCode(this.fadeInDuration);
      result = 31 * result + Float.hashCode(this.fadeOutDuration);
      result = 31 * result + Objects.hashCode(this.transitionType);
      result = 31 * result + Float.hashCode(this.transitionDuration);
      result = 31 * result + Boolean.hashCode(this.playToCompletion);
      result = 31 * result + Objects.hashCode(this.nameTranslationKey);
      result = 31 * result + Integer.hashCode(this.audioCategoryIndex);
      result = 31 * result + Objects.hashCode(this.tempo);
      result = 31 * result + Arrays.hashCode(this.layers);
      result = 31 * result + Objects.hashCode(this.entryMarker);
      result = 31 * result + Objects.hashCode(this.exitMarker);
      result = 31 * result + Arrays.hashCode(this.stateNames);
      result = 31 * result + Arrays.hashCode(this.stateVolumeData);
      return 31 * result + Integer.hashCode(this.defaultStateIndex);
   }
}
