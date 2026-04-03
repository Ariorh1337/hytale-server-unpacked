package com.hypixel.hytale.protocol.io;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public final class VarInt {
   private VarInt() {
   }

   public static void write(@Nonnull ByteBuf buf, int value) {
      if (value < 0) {
         throw new IllegalArgumentException("VarInt cannot encode negative values: " + value);
      }

      if ((value & -128) == 0) {
         buf.writeByte(value);
      } else if ((value & -16384) == 0) {
         buf.writeShort((value & 127 | 128) << 8 | value >>> 7);
      } else if ((value & -2097152) == 0) {
         buf.writeMedium((value & 127 | 128) << 16 | (value >>> 7 & 127 | 128) << 8 | value >>> 14);
      } else if ((value & -268435456) == 0) {
         buf.writeInt((value & 127 | 128) << 24 | (value >>> 7 & 127 | 128) << 16 | (value >>> 14 & 127 | 128) << 8 | value >>> 21);
      } else {
         buf.writeInt((value & 127 | 128) << 24 | (value >>> 7 & 127 | 128) << 16 | (value >>> 14 & 127 | 128) << 8 | value >>> 21 & 127 | 128);
         buf.writeByte(value >>> 28);
      }
   }

   public static int read(@Nonnull ByteBuf buf) {
      int b = buf.readByte();
      int value = b & 127;
      if ((b & 128) == 0) {
         return value;
      } else {
         int var3 = buf.readByte();
         value |= (var3 & 127) << 7;
         if ((var3 & 128) == 0) {
            return value;
         } else {
            var3 = buf.readByte();
            value |= (var3 & 127) << 14;
            if ((var3 & 128) == 0) {
               return value;
            } else {
               var3 = buf.readByte();
               value |= (var3 & 127) << 21;
               if ((var3 & 128) == 0) {
                  return value;
               } else {
                  var3 = buf.readByte();
                  value |= (var3 & 127) << 28;
                  if ((var3 & 128) == 0) {
                     return value;
                  } else {
                     throw new ProtocolException("VarInt exceeds maximum length (5 bytes)");
                  }
               }
            }
         }
      }
   }

   public static int peek(@Nonnull ByteBuf buf, int index) {
      int limit = buf.writerIndex();
      if (index >= limit) {
         return -1;
      }

      int b = buf.getByte(index);
      int value = b & 127;
      if ((b & 128) == 0) {
         return value;
      }

      if (index + 1 >= limit) {
         return -1;
      }

      int var5 = buf.getByte(index + 1);
      value |= (var5 & 127) << 7;
      if ((var5 & 128) == 0) {
         return value;
      }

      if (index + 2 >= limit) {
         return -1;
      }

      var5 = buf.getByte(index + 2);
      value |= (var5 & 127) << 14;
      if ((var5 & 128) == 0) {
         return value;
      }

      if (index + 3 >= limit) {
         return -1;
      }

      var5 = buf.getByte(index + 3);
      value |= (var5 & 127) << 21;
      if ((var5 & 128) == 0) {
         return value;
      }

      if (index + 4 >= limit) {
         return -1;
      }

      var5 = buf.getByte(index + 4);
      value |= (var5 & 127) << 28;
      return (var5 & 128) == 0 ? value : -1;
   }

   public static int length(@Nonnull ByteBuf buf, int index) {
      int limit = buf.writerIndex();
      if (index >= limit) {
         return -1;
      } else if ((buf.getByte(index) & 128) == 0) {
         return 1;
      } else if (index + 1 >= limit) {
         return -1;
      } else if ((buf.getByte(index + 1) & 128) == 0) {
         return 2;
      } else if (index + 2 >= limit) {
         return -1;
      } else if ((buf.getByte(index + 2) & 128) == 0) {
         return 3;
      } else if (index + 3 >= limit) {
         return -1;
      } else if ((buf.getByte(index + 3) & 128) == 0) {
         return 4;
      } else if (index + 4 >= limit) {
         return -1;
      } else {
         return (buf.getByte(index + 4) & 128) == 0 ? 5 : -1;
      }
   }

   public static int size(int value) {
      if ((value & -128) == 0) {
         return 1;
      } else if ((value & -16384) == 0) {
         return 2;
      } else if ((value & -2097152) == 0) {
         return 3;
      } else {
         return (value & -268435456) == 0 ? 4 : 5;
      }
   }
}
