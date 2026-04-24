package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class SSHBuffer {
   private final byte[] buffer;
   private int pos = 0;

   SSHBuffer(byte[] var1, byte[] var2) {
      this.buffer = var2;

      for (int var3 = 0; var3 != var1.length; var3++) {
         if (var1[var3] != var2[var3]) {
            throw new IllegalArgumentException("magic-number incorrect");
         }
      }

      this.pos += var1.length;
   }

   SSHBuffer(byte[] var1) {
      this.buffer = var1;
   }

   int readU32() {
      if (this.pos > this.buffer.length - 4) {
         throw new IllegalArgumentException("4 bytes for U32 exceeds buffer.");
      }

      int var1 = org.bouncycastle.util.Pack.bigEndianToInt(this.buffer, this.pos);
      this.pos += 4;
      return var1;
   }

   String readString() {
      return Strings.fromByteArray(this.readBlock());
   }

   byte[] readBlock() {
      int var1 = this.readU32();
      if (var1 == 0) {
         return new byte[0];
      }

      if (var1 > this.buffer.length - this.pos) {
         throw new IllegalArgumentException("not enough data for block");
      }

      int var2 = this.pos;
      this.pos += var1;
      return Arrays.copyOfRange(this.buffer, var2, this.pos);
   }

   void skipBlock() {
      int var1 = this.readU32();
      if (var1 > this.buffer.length - this.pos) {
         throw new IllegalArgumentException("not enough data for block");
      }

      this.pos += var1;
   }

   byte[] readPaddedBlock() {
      return this.readPaddedBlock(8);
   }

   byte[] readPaddedBlock(int var1) {
      int var2 = this.readU32();
      if (var2 == 0) {
         return new byte[0];
      }

      if (var2 > this.buffer.length - this.pos) {
         throw new IllegalArgumentException("not enough data for block");
      }

      int var3 = var2 % var1;
      if (0 != var3) {
         throw new IllegalArgumentException("missing padding");
      }

      int var4 = this.pos;
      this.pos += var2;
      int var5 = this.pos;
      if (var2 > 0) {
         int var6 = this.buffer[this.pos - 1] & 255;
         if (0 < var6 && var6 < var1) {
            int var7 = var6;
            var5 -= var7;
            int var8 = 1;

            for (int var9 = var5; var8 <= var7; var9++) {
               if (var8 != (this.buffer[var9] & 255)) {
                  throw new IllegalArgumentException("incorrect padding");
               }

               var8++;
            }
         }
      }

      return Arrays.copyOfRange(this.buffer, var4, var5);
   }

   BigInteger readBigNumPositive() {
      int var1 = this.readU32();
      if (var1 > this.buffer.length - this.pos) {
         throw new IllegalArgumentException("not enough data for big num");
      }

      int var2 = this.pos;
      this.pos += var1;
      byte[] var3 = Arrays.copyOfRange(this.buffer, var2, this.pos);
      return new BigInteger(1, var3);
   }

   byte[] getBuffer() {
      return Arrays.clone(this.buffer);
   }

   boolean hasRemaining() {
      return this.pos < this.buffer.length;
   }
}
