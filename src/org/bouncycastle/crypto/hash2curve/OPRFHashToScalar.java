package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.hash2curve.impl.XmdMessageExpansion;
import org.bouncycastle.math.ec.ECCurve;

public class OPRFHashToScalar {
   private final ECCurve curve;
   private final MessageExpansion messageExpansion;
   private final int L;

   public OPRFHashToScalar(ECCurve var1, Digest var2, int var3, int var4) {
      this.curve = var1;
      this.L = (int)Math.ceil(((double)var1.getOrder().subtract(BigInteger.ONE).bitLength() + var3) / 8.0);
      this.messageExpansion = new XmdMessageExpansion(var2, var3, var4);
   }

   public OPRFHashToScalar(ECCurve var1, ExtendedDigest var2, int var3) {
      this.curve = var1;
      this.L = (int)Math.ceil(((double)var1.getOrder().subtract(BigInteger.ONE).bitLength() + var3) / 8.0);
      this.messageExpansion = new XmdMessageExpansion(var2, var3);
   }

   public BigInteger process(byte[] var1, byte[] var2) {
      byte[] var3 = this.messageExpansion.expandMessage(var1, var2, this.L);
      return new BigInteger(1, var3).mod(this.curve.getOrder());
   }
}
