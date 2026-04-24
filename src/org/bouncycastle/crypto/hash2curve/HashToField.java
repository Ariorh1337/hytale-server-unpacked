package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class HashToField {
   protected final byte[] dst;
   protected final ECCurve curve;
   protected final MessageExpansion messageExpansion;
   protected int L;
   protected int m;
   protected BigInteger p;

   public HashToField(byte[] var1, ECCurve var2, MessageExpansion var3, int var4) {
      this.dst = var1;
      this.curve = var2;
      this.L = var4;
      this.messageExpansion = var3;
      this.p = var2.getField().getCharacteristic();
      this.m = var2.getField().getDimension();
   }

   public BigInteger[][] process(byte[] var1, int var2) {
      int var3 = var2 * this.m * this.L;
      byte[] var4 = this.messageExpansion.expandMessage(var1, this.dst, var3);
      BigInteger[][] var5 = new BigInteger[var2][this.m];

      for (int var6 = 0; var6 < var2; var6++) {
         BigInteger[] var7 = new BigInteger[this.m];

         for (int var8 = 0; var8 < this.m; var8++) {
            int var9 = this.L * (var8 + var6 * this.m);
            byte[] var10 = Arrays.copyOfRange(var4, var9, var9 + this.L);
            var7[var8] = H2cUtils.os2ip(var10).mod(this.p);
         }

         var5[var6] = var7;
      }

      return var5;
   }
}
