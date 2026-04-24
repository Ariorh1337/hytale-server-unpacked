package org.bouncycastle.jce;

import java.io.IOException;
import java.math.BigInteger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Properties;

public class PKCS12Util {
   private static final BigInteger DEFAULT_MAX_IT_COUNT = BigInteger.valueOf(5000000L);
   static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";

   public static byte[] convertToDefiniteLength(byte[] var0) throws IOException {
      Pfx var1 = Pfx.getInstance(var0);
      return var1.getEncoded("DER");
   }

   public static byte[] convertToDefiniteLength(byte[] var0, char[] var1, String var2) throws IOException {
      Pfx var3 = Pfx.getInstance(var0);
      ContentInfo var4 = var3.getAuthSafe();
      ASN1Primitive var5 = ASN1Primitive.fromByteArray(getContentOctets(var4));
      byte[] var6 = var5.getEncoded("DER");
      var4 = new ContentInfo(var4.getContentType(), new DEROctetString(var6));
      MacData var7 = var3.getMacData();

      try {
         int var8 = validateIterationCount(var7.getIterationCount());
         byte[] var9 = getContentOctets(var4);
         byte[] var10 = calculatePbeMac(var7.getMac().getAlgorithmId().getAlgorithm(), var7.getSalt(), var8, var1, var9, var2);
         AlgorithmIdentifier var11 = new AlgorithmIdentifier(var7.getMac().getAlgorithmId().getAlgorithm(), DERNull.INSTANCE);
         DigestInfo var12 = new DigestInfo(var11, var10);
         var7 = new MacData(var12, var7.getSalt(), var8);
      } catch (Exception var13) {
         throw new IOException("error constructing MAC: " + var13.toString());
      }

      var3 = new Pfx(var4, var7);
      return var3.getEncoded("DER");
   }

   public static ASN1Encodable getContent(ContentInfo var0) throws IOException {
      ASN1Encodable var1 = var0.getContent();
      if (var1 == null) {
         throw new ASN1ParsingException("ContentInfo content missing");
      } else {
         return var1;
      }
   }

   public static byte[] getContentOctets(ContentInfo var0) throws IOException {
      return ASN1OctetString.getInstance(getContent(var0)).getOctets();
   }

   public static ASN1OctetString getEncryptedContent(EncryptedData var0) throws IOException {
      ASN1OctetString var1 = var0.getContent();
      if (var1 == null) {
         throw new ASN1ParsingException("EncryptedContentInfo content missing");
      } else {
         return var1;
      }
   }

   public static int validateIterationCount(BigInteger var0) {
      if (var0.signum() < 0) {
         throw new IllegalStateException("negative iteration count found");
      }

      if (var0.bitLength() > 31) {
         throw new IllegalStateException("iteration counts >= 2^31 are not suppported");
      }

      BigInteger var1 = Properties.asBigInteger("org.bouncycastle.pkcs12.max_it_count");
      if (var1 == null) {
         var1 = DEFAULT_MAX_IT_COUNT;
      }

      if (var0.compareTo(var1) > 0) {
         throw new IllegalStateException("iteration count " + var0 + " greater than " + var1);
      } else {
         return BigIntegers.intValueExact(var0);
      }
   }

   private static byte[] calculatePbeMac(ASN1ObjectIdentifier var0, byte[] var1, int var2, char[] var3, byte[] var4, String var5) throws Exception {
      SecretKeyFactory var6 = SecretKeyFactory.getInstance(var0.getId(), var5);
      PBEParameterSpec var7 = new PBEParameterSpec(var1, var2);
      PBEKeySpec var8 = new PBEKeySpec(var3);
      SecretKey var9 = var6.generateSecret(var8);
      Mac var10 = Mac.getInstance(var0.getId(), var5);
      var10.init(var9, var7);
      var10.update(var4);
      return var10.doFinal();
   }
}
