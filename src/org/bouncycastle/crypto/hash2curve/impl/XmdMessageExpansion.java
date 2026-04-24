package org.bouncycastle.crypto.hash2curve.impl;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.hash2curve.H2cUtils;
import org.bouncycastle.crypto.hash2curve.MessageExpansion;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class XmdMessageExpansion implements MessageExpansion {
   private final Digest digest;
   private final int s;
   private final int hashOutputBytes;

   public XmdMessageExpansion(Digest var1, int var2, int var3) {
      this.digest = var1;
      this.s = var3;
      this.hashOutputBytes = var1.getDigestSize();
      if (this.hashOutputBytes < (int)Math.ceil(var2 * 2 / 8.0)) {
         throw new IllegalArgumentException("Hash output size is too small for the security level of the curve");
      }
   }

   public XmdMessageExpansion(ExtendedDigest var1, int var2) {
      this(var1, var2, getInputBlockSize(var1));
   }

   private static int getInputBlockSize(ExtendedDigest var0) {
      return var0.getByteLength() * 8;
   }

   @Override
   public byte[] expandMessage(byte[] var1, byte[] var2, int var3) {
      int var4 = (int)Math.ceil((double)var3 / this.hashOutputBytes);
      if (var4 > 255) {
         throw new IllegalArgumentException("Ell parameter must not be greater than 255. Current value = " + var4);
      }

      if (var3 > 65535) {
         throw new IllegalArgumentException("Output size must not be greater than 65535. Current value = " + var3);
      }

      if (var2.length > 255) {
         throw new IllegalArgumentException("DST size must not be greater than 255. Current value = " + var2.length);
      }

      byte[] var5 = Arrays.concatenate(var2, H2cUtils.i2osp(var2.length, 1));
      byte[] var6 = H2cUtils.i2osp(0, this.s / 8);
      byte[] var7 = H2cUtils.i2osp(var3, 2);
      byte[] var8 = Arrays.concatenate(new byte[][]{var6, var1, var7, H2cUtils.i2osp(0, 1), var5});
      byte[][] var9 = new byte[var4 + 1][this.hashOutputBytes];
      var9[0] = this.hash(var8);
      var9[1] = this.hash(Arrays.concatenate(var9[0], H2cUtils.i2osp(1, 1), var5));
      byte[] var10 = Arrays.clone(var9[1]);

      for (int var11 = 2; var11 <= var4; var11++) {
         var9[var11] = this.hash(Arrays.concatenate(H2cUtils.xor(var9[0], var9[var11 - 1]), H2cUtils.i2osp(var11, 1), var5));
         var10 = Arrays.concatenate(var10, var9[var11]);
      }

      return Arrays.copyOfRange(var10, 0, var3);
   }

   private byte[] hash(byte[] var1) {
      Digest var2 = DigestFactory.cloneDigest(this.digest);
      var2.update(var1, 0, var1.length);
      byte[] var3 = new byte[this.digest.getDigestSize()];
      var2.doFinal(var3, 0);
      return var3;
   }
}
