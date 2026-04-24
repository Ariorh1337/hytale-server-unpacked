package org.bouncycastle.crypto.hash2curve;

import java.math.BigInteger;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.hash2curve.data.AffineXY;
import org.bouncycastle.crypto.hash2curve.impl.Elligator2MapToCurve;
import org.bouncycastle.crypto.hash2curve.impl.MontgomeryCurveProcessor;
import org.bouncycastle.crypto.hash2curve.impl.NistCurveProcessor;
import org.bouncycastle.crypto.hash2curve.impl.SimplifiedShallueVanDeWoestijneMapToCurve;
import org.bouncycastle.crypto.hash2curve.impl.XmdMessageExpansion;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.djb.Curve25519;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP384R1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Curve;
import org.bouncycastle.util.Strings;

public class HashToEllipticCurve {
   protected final HashToField hashToField;
   protected final MapToCurve mapToCurve;
   protected final CurveProcessor curveProcessor;

   protected HashToEllipticCurve(HashToField var1, MapToCurve var2, CurveProcessor var3) {
      this.curveProcessor = var3;
      this.hashToField = var1;
      this.mapToCurve = var2;
   }

   public static HashToEllipticCurve getInstance(HashToCurveProfile var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      switch (var0) {
         case P256_XMD_SHA_256:
            SecP256R1Curve var6 = new SecP256R1Curve();
            return new HashToEllipticCurve(
               new HashToField(var2, var6, new XmdMessageExpansion(new SHA256Digest(), var0.getK()), var0.getL()),
               new SimplifiedShallueVanDeWoestijneMapToCurve(var6, var0.getZ()),
               new NistCurveProcessor()
            );
         case P384_XMD_SHA_384:
            SecP384R1Curve var5 = new SecP384R1Curve();
            return new HashToEllipticCurve(
               new HashToField(var2, var5, new XmdMessageExpansion(new SHA384Digest(), var0.getK()), var0.getL()),
               new SimplifiedShallueVanDeWoestijneMapToCurve(var5, var0.getZ()),
               new NistCurveProcessor()
            );
         case P521_XMD_SHA_512:
            SecP521R1Curve var4 = new SecP521R1Curve();
            return new HashToEllipticCurve(
               new HashToField(var2, var4, new XmdMessageExpansion(new SHA512Digest(), var0.getK()), var0.getL()),
               new SimplifiedShallueVanDeWoestijneMapToCurve(var4, var0.getZ()),
               new NistCurveProcessor()
            );
         case CURVE25519W_XMD_SHA_512_ELL2:
            Curve25519 var3 = new Curve25519();
            return new HashToEllipticCurve(
               new HashToField(var2, var3, new XmdMessageExpansion(new SHA512Digest(), var0.getK()), var0.getL()),
               new Elligator2MapToCurve(var3, var0.getZ(), BigInteger.valueOf(var0.getmJ().intValue()), BigInteger.valueOf(var0.getmK().intValue())),
               new MontgomeryCurveProcessor(var3, var0.getmJ(), var0.getmK(), var0.getH())
            );
         default:
            throw new IllegalArgumentException("Unsupported profile: " + var0);
      }
   }

   public ECPoint hashToCurve(byte[] var1) {
      BigInteger[][] var2 = this.hashToField.process(var1, 2);
      ECPoint var3 = this.mapToCurve.process(var2[0][0]);
      ECPoint var4 = this.mapToCurve.process(var2[1][0]);
      ECPoint var5 = this.curveProcessor.add(var3, var4);
      return this.curveProcessor.clearCofactor(var5);
   }

   public ECPoint encodeToCurve(byte[] var1) {
      BigInteger[][] var2 = this.hashToField.process(var1, 1);
      ECPoint var3 = this.mapToCurve.process(var2[0][0]);
      return this.curveProcessor.clearCofactor(var3);
   }

   public AffineXY getAffineXY(ECPoint var1) {
      return this.curveProcessor.mapToAffineXY(var1);
   }
}
