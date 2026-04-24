package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x9.KeySpecificInfo;
import org.bouncycastle.asn1.x9.OtherInfo;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.util.Pack;

public class DHKEKGenerator implements DerivationFunction {
   private final Digest digest;
   private ASN1ObjectIdentifier algorithm;
   private int keySize;
   private byte[] z;
   private byte[] extraInfo;

   public DHKEKGenerator(Digest var1) {
      this.digest = var1;
   }

   @Override
   public void init(DerivationParameters var1) {
      DHKDFParameters var2 = (DHKDFParameters)var1;
      this.algorithm = var2.getAlgorithm();
      this.keySize = var2.getKeySize();
      this.z = var2.getZ();
      this.extraInfo = var2.getExtraInfo();
   }

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

      int var6 = 0;
      byte[] var7 = new byte[4];
      DEROctetString var8 = DEROctetString.withContents(var7);
      KeySpecificInfo var9 = new KeySpecificInfo(this.algorithm, var8);
      DEROctetString var10 = DEROctetString.withContentsOptional(this.extraInfo);
      DEROctetString var11 = DEROctetString.withContents(Pack.intToBigEndian(this.keySize));
      OtherInfo var12 = new OtherInfo(var9, var10, var11);
      DigestOutputStream var13 = new DigestOutputStream(this.digest);

      while (var3 > 0) {
         this.digest.update(this.z, 0, this.z.length);

         try {
            Pack.intToBigEndian(++var6, var7);
            var12.encodeTo(var13, "DER");
         } catch (IOException var15) {
            throw new IllegalArgumentException("unable to encode parameter info: " + var15.getMessage());
         }

         if (var3 < var5) {
            byte[] var14 = new byte[var5];
            this.digest.doFinal(var14, 0);
            System.arraycopy(var14, 0, var1, var2, var3);
            break;
         }

         this.digest.doFinal(var1, var2);
         var2 += var5;
         var3 -= var5;
      }

      return var4;
   }
}
