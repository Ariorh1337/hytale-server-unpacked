package com.hypixel.hytale.protocol.packets.connection;

import com.hypixel.hytale.protocol.HostAddress;
import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Connect implements Packet, ToServerPacket {
   public static final int PACKET_ID = 0;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 30;
   public static final int VARIABLE_FIELD_COUNT = 4;
   public static final int VARIABLE_BLOCK_START = 46;
   public static final int MAX_SIZE = 37972;
   public int protocolCrc;
   public int protocolBuildNumber;
   @Nonnull
   public String clientVersion = "";
   @Nonnull
   public ClientType clientType = ClientType.Game;
   @Nullable
   public String identityToken;
   @Nonnull
   public String language = "";
   @Nullable
   public byte[] referralData;
   @Nullable
   public HostAddress referralSource;

   @Override
   public int getId() {
      return 0;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public Connect() {
   }

   public Connect(
      int protocolCrc,
      int protocolBuildNumber,
      @Nonnull String clientVersion,
      @Nonnull ClientType clientType,
      @Nullable String identityToken,
      @Nonnull String language,
      @Nullable byte[] referralData,
      @Nullable HostAddress referralSource
   ) {
      this.protocolCrc = protocolCrc;
      this.protocolBuildNumber = protocolBuildNumber;
      this.clientVersion = clientVersion;
      this.clientType = clientType;
      this.identityToken = identityToken;
      this.language = language;
      this.referralData = referralData;
      this.referralSource = referralSource;
   }

   public Connect(@Nonnull Connect other) {
      this.protocolCrc = other.protocolCrc;
      this.protocolBuildNumber = other.protocolBuildNumber;
      this.clientVersion = other.clientVersion;
      this.clientType = other.clientType;
      this.identityToken = other.identityToken;
      this.language = other.language;
      this.referralData = other.referralData;
      this.referralSource = other.referralSource;
   }

   @Nonnull
   public static Connect deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 46) {
         throw ProtocolException.bufferTooSmall("Connect", 46, buf.readableBytes() - offset);
      }

      Connect obj = new Connect();
      byte nullBits = buf.getByte(offset);
      obj.protocolCrc = buf.getIntLE(offset + 1);
      obj.protocolBuildNumber = buf.getIntLE(offset + 5);
      obj.clientVersion = PacketIO.readFixedAsciiString(buf, offset + 9, 20);
      obj.clientType = ClientType.fromValue(buf.getByte(offset + 29));
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 30);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 46) {
            throw ProtocolException.invalidOffset("IdentityToken", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 46 + varPosBase0;
         int identityTokenLen = VarInt.peek(buf, varPos0);
         if (identityTokenLen < 0) {
            throw ProtocolException.invalidVarInt("IdentityToken");
         }

         int identityTokenVarIntLen = VarInt.size(identityTokenLen);
         if (identityTokenLen > 8192) {
            throw ProtocolException.stringTooLong("IdentityToken", identityTokenLen, 8192);
         }

         if (varPos0 + identityTokenVarIntLen + identityTokenLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("IdentityToken", varPos0 + identityTokenVarIntLen + identityTokenLen, buf.readableBytes());
         }

         obj.identityToken = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      int varPosBase1 = buf.getIntLE(offset + 34);
      if (varPosBase1 >= 0 && varPosBase1 <= buf.writerIndex() - offset - 46) {
         int varPos1 = offset + 46 + varPosBase1;
         int languageLen = VarInt.peek(buf, varPos1);
         if (languageLen < 0) {
            throw ProtocolException.invalidVarInt("Language");
         }

         int languageVarIntLen = VarInt.size(languageLen);
         if (languageLen > 16) {
            throw ProtocolException.stringTooLong("Language", languageLen, 16);
         }

         if (varPos1 + languageVarIntLen + languageLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Language", varPos1 + languageVarIntLen + languageLen, buf.readableBytes());
         }

         obj.language = PacketIO.readValidatedAsciiString(buf, varPos1 + languageVarIntLen, languageLen, "Language");
         if ((nullBits & 2) != 0) {
            varPosBase1 = buf.getIntLE(offset + 38);
            if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 46) {
               throw ProtocolException.invalidOffset("ReferralData", varPosBase1, buf.readableBytes());
            }

            varPos1 = offset + 46 + varPosBase1;
            languageLen = VarInt.peek(buf, varPos1);
            if (languageLen < 0) {
               throw ProtocolException.invalidVarInt("ReferralData");
            }

            languageVarIntLen = VarInt.size(languageLen);
            if (languageLen > 4096) {
               throw ProtocolException.arrayTooLong("ReferralData", languageLen, 4096);
            }

            if (varPos1 + languageVarIntLen + languageLen * 1L > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("ReferralData", varPos1 + languageVarIntLen + languageLen * 1, buf.readableBytes());
            }

            obj.referralData = new byte[languageLen];

            for (int i = 0; i < languageLen; i++) {
               obj.referralData[i] = buf.getByte(varPos1 + languageVarIntLen + i * 1);
            }
         }

         if ((nullBits & 4) != 0) {
            varPosBase1 = buf.getIntLE(offset + 42);
            if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 46) {
               throw ProtocolException.invalidOffset("ReferralSource", varPosBase1, buf.readableBytes());
            }

            varPos1 = offset + 46 + varPosBase1;
            obj.referralSource = HostAddress.deserialize(buf, varPos1);
         }

         return obj;
      } else {
         throw ProtocolException.invalidOffset("Language", varPosBase1, buf.readableBytes());
      }
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 46;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 30);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 46) {
            throw ProtocolException.invalidOffset("IdentityToken", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 46 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      int fieldOffset1 = buf.getIntLE(offset + 34);
      if (fieldOffset1 >= 0 && fieldOffset1 <= buf.writerIndex() - offset - 46) {
         int pos1 = offset + 46 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }

         if ((nullBits & 2) != 0) {
            fieldOffset1 = buf.getIntLE(offset + 38);
            if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 46) {
               throw ProtocolException.invalidOffset("ReferralData", fieldOffset1, maxEnd);
            }

            pos1 = offset + 46 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            pos1 += VarInt.size(sl) + sl * 1;
            if (pos1 - offset > maxEnd) {
               maxEnd = pos1 - offset;
            }
         }

         if ((nullBits & 4) != 0) {
            fieldOffset1 = buf.getIntLE(offset + 42);
            if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 46) {
               throw ProtocolException.invalidOffset("ReferralSource", fieldOffset1, maxEnd);
            }

            pos1 = offset + 46 + fieldOffset1;
            pos1 += HostAddress.computeBytesConsumed(buf, pos1);
            if (pos1 - offset > maxEnd) {
               maxEnd = pos1 - offset;
            }
         }

         return maxEnd;
      } else {
         throw ProtocolException.invalidOffset("Language", fieldOffset1, maxEnd);
      }
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.identityToken != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.referralData != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.referralSource != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.protocolCrc);
      buf.writeIntLE(this.protocolBuildNumber);
      PacketIO.writeFixedAsciiString(buf, this.clientVersion, 20);
      buf.writeByte(this.clientType.getValue());
      int identityTokenOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int languageOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int referralDataOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int referralSourceOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.identityToken != null) {
         buf.setIntLE(identityTokenOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.identityToken, 8192);
      } else {
         buf.setIntLE(identityTokenOffsetSlot, -1);
      }

      buf.setIntLE(languageOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarAsciiString(buf, this.language, 16);
      if (this.referralData != null) {
         buf.setIntLE(referralDataOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.referralData.length > 4096) {
            throw ProtocolException.arrayTooLong("ReferralData", this.referralData.length, 4096);
         }

         VarInt.write(buf, this.referralData.length);

         for (byte item : this.referralData) {
            buf.writeByte(item);
         }
      } else {
         buf.setIntLE(referralDataOffsetSlot, -1);
      }

      if (this.referralSource != null) {
         buf.setIntLE(referralSourceOffsetSlot, buf.writerIndex() - varBlockStart);
         this.referralSource.serialize(buf);
      } else {
         buf.setIntLE(referralSourceOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 46;
      if (this.identityToken != null) {
         size += PacketIO.stringSize(this.identityToken);
      }

      size += VarInt.size(this.language.length()) + this.language.length();
      if (this.referralData != null) {
         size += VarInt.size(this.referralData.length) + this.referralData.length * 1;
      }

      if (this.referralSource != null) {
         size += this.referralSource.computeSize();
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 46) {
         return ValidationResult.error("Buffer too small: expected at least 46 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 29) & 255;
      if (v >= 2) {
         return ValidationResult.error("Invalid ClientType value for ClientType");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 30);
         if (v < 0 || v > buffer.writerIndex() - offset - 46) {
            return ValidationResult.error("Invalid offset for IdentityToken");
         }

         int pos = offset + 46 + v;
         int identityTokenLen = VarInt.peek(buffer, pos);
         if (identityTokenLen < 0) {
            return ValidationResult.error("Invalid string length for IdentityToken");
         }

         if (identityTokenLen > 8192) {
            return ValidationResult.error("IdentityToken exceeds max length 8192");
         }

         pos += VarInt.size(identityTokenLen);
         pos += identityTokenLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading IdentityToken");
         }
      }

      v = buffer.getIntLE(offset + 34);
      if (v >= 0 && v <= buffer.writerIndex() - offset - 46) {
         int pos = offset + 46 + v;
         int languageLen = VarInt.peek(buffer, pos);
         if (languageLen < 0) {
            return ValidationResult.error("Invalid string length for Language");
         }

         if (languageLen > 16) {
            return ValidationResult.error("Language exceeds max length 16");
         }

         pos += VarInt.size(languageLen);
         pos += languageLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Language");
         }

         if (!PacketIO.isValidAscii(buffer, pos - languageLen, languageLen)) {
            return ValidationResult.error("Language contains non-ASCII bytes");
         }

         if ((nullBits & 2) != 0) {
            v = buffer.getIntLE(offset + 38);
            if (v < 0 || v > buffer.writerIndex() - offset - 46) {
               return ValidationResult.error("Invalid offset for ReferralData");
            }

            pos = offset + 46 + v;
            languageLen = VarInt.peek(buffer, pos);
            if (languageLen < 0) {
               return ValidationResult.error("Invalid array count for ReferralData");
            }

            if (languageLen > 4096) {
               return ValidationResult.error("ReferralData exceeds max length 4096");
            }

            pos += VarInt.size(languageLen);
            pos += languageLen * 1;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading ReferralData");
            }
         }

         if ((nullBits & 4) != 0) {
            v = buffer.getIntLE(offset + 42);
            if (v < 0 || v > buffer.writerIndex() - offset - 46) {
               return ValidationResult.error("Invalid offset for ReferralSource");
            }

            pos = offset + 46 + v;
            ValidationResult referralSourceResult = HostAddress.validateStructure(buffer, pos);
            if (!referralSourceResult.isValid()) {
               return ValidationResult.error("Invalid ReferralSource: " + referralSourceResult.error());
            }

            pos += HostAddress.computeBytesConsumed(buffer, pos);
         }

         return ValidationResult.OK;
      } else {
         return ValidationResult.error("Invalid offset for Language");
      }
   }

   public Connect clone() {
      Connect copy = new Connect();
      copy.protocolCrc = this.protocolCrc;
      copy.protocolBuildNumber = this.protocolBuildNumber;
      copy.clientVersion = this.clientVersion;
      copy.clientType = this.clientType;
      copy.identityToken = this.identityToken;
      copy.language = this.language;
      copy.referralData = this.referralData != null ? Arrays.copyOf(this.referralData, this.referralData.length) : null;
      copy.referralSource = this.referralSource != null ? this.referralSource.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Connect other)
            ? false
            : this.protocolCrc == other.protocolCrc
               && this.protocolBuildNumber == other.protocolBuildNumber
               && Objects.equals(this.clientVersion, other.clientVersion)
               && Objects.equals(this.clientType, other.clientType)
               && Objects.equals(this.identityToken, other.identityToken)
               && Objects.equals(this.language, other.language)
               && Arrays.equals(this.referralData, other.referralData)
               && Objects.equals(this.referralSource, other.referralSource);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Integer.hashCode(this.protocolCrc);
      result = 31 * result + Integer.hashCode(this.protocolBuildNumber);
      result = 31 * result + Objects.hashCode(this.clientVersion);
      result = 31 * result + Objects.hashCode(this.clientType);
      result = 31 * result + Objects.hashCode(this.identityToken);
      result = 31 * result + Objects.hashCode(this.language);
      result = 31 * result + Arrays.hashCode(this.referralData);
      return 31 * result + Objects.hashCode(this.referralSource);
   }
}
