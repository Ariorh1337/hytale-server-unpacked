package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class BaseKDFBytesGenerator implements DigestDerivationFunction {
   private int counterStart;
   private Digest digest;
   private byte[] shared;
   private byte[] iv;

   protected BaseKDFBytesGenerator(int var1, Digest var2) {
      this.counterStart = var1;
      this.digest = var2;
   }

   @Override
   public void init(DerivationParameters var1) {
      if (var1 instanceof KDFParameters) {
         KDFParameters var2 = (KDFParameters)var1;
         this.shared = var2.getSharedSecret();
         this.iv = var2.getIV();
      } else {
         if (!(var1 instanceof ISO18033KDFParameters)) {
            throw new IllegalArgumentException("KDF parameters required for generator");
         }

         ISO18033KDFParameters var3 = (ISO18033KDFParameters)var1;
         this.shared = var3.getSeed();
         this.iv = null;
      }
   }

   @Override
   public Digest getDigest() {
      return this.digest;
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var1.length - var3 < var2) {
         throw new OutputLengthException("output buffer too small");
      }

      this.digest.reset();
      int var4 = var3;
      int var5 = this.digest.getDigestSize();
      if (var4 > 4294967295L * var5) {
         throw new IllegalArgumentException("Output length too large");
      }

      int var6 = this.counterStart;
      byte[] var7 = new byte[4];

      while (var3 > 0) {
         Pack.intToBigEndian(var6, var7);
         this.digest.update(this.shared, 0, this.shared.length);
         this.digest.update(var7, 0, 4);
         if (this.iv != null) {
            this.digest.update(this.iv, 0, this.iv.length);
         }

         if (var3 < var5) {
            byte[] var8 = new byte[var5];
            this.digest.doFinal(var8, 0);
            System.arraycopy(var8, 0, var1, var2, var3);
            break;
         }

         this.digest.doFinal(var1, var2);
         var2 += var5;
         var3 -= var5;
         var6++;
      }

      return var4;
   }
}
