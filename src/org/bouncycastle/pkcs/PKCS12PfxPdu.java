package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class PKCS12PfxPdu {
   private Pfx pfx;

   private static Pfx parseBytes(byte[] var0) throws IOException {
      try {
         return Pfx.getInstance(ASN1Primitive.fromByteArray(var0));
      } catch (ClassCastException var2) {
         throw new PKCSIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new PKCSIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   public PKCS12PfxPdu(Pfx var1) {
      this.pfx = var1;
   }

   public PKCS12PfxPdu(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public ContentInfo[] getContentInfos() {
      ASN1Sequence var1 = ASN1Sequence.getInstance(ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
      ContentInfo[] var2 = new ContentInfo[var1.size()];

      for (int var3 = 0; var3 != var1.size(); var3++) {
         var2[var3] = ContentInfo.getInstance(var1.getObjectAt(var3));
      }

      return var2;
   }

   public boolean hasMac() {
      return this.pfx.getMacData() != null;
   }

   public AlgorithmIdentifier getMacAlgorithmID() {
      MacData var1 = this.pfx.getMacData();
      return var1 != null ? var1.getMac().getAlgorithmId() : null;
   }

   public boolean isMacValid(PKCS12MacCalculatorBuilderProvider var1, char[] var2) throws PKCSException {
      MacData var3 = this.pfx.getMacData();
      if (var3 == null) {
         throw new IllegalStateException("no MAC present on PFX");
      }

      AlgorithmIdentifier var4 = var3.getMac().getAlgorithmId();
      ASN1ObjectIdentifier var5 = var4.getAlgorithm();
      ASN1Object var6;
      if (PKCSObjectIdentifiers.id_PBMAC1.equals(var5)) {
         var6 = PBMAC1Params.getInstance(var4.getParameters());
         if (var6 == null) {
            throw new PKCSException("If the DigestAlgorithmIdentifier is id-PBMAC1, then the parameters field must contain valid PBMAC1-params parameters.");
         }
      } else {
         var6 = new PKCS12PBEParams(var3.getSalt(), BigIntegers.intValueExact(var3.getIterationCount()));
      }

      PKCS12MacCalculatorBuilder var7 = var1.get(new AlgorithmIdentifier(var5, var6));
      MacDataGenerator var8 = new MacDataGenerator(var7);

      try {
         byte[] var9 = ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets();
         MacData var10 = var8.build(var2, var9);
         return Arrays.constantTimeAreEqual(var10.getEncoded(), var3.getEncoded());
      } catch (IOException var11) {
         throw new PKCSException("unable to process AuthSafe: " + var11.getMessage());
      }
   }

   public Pfx toASN1Structure() {
      return this.pfx;
   }

   public byte[] getEncoded() throws IOException {
      return this.toASN1Structure().getEncoded();
   }

   public byte[] getEncoded(String var1) throws IOException {
      return this.toASN1Structure().getEncoded(var1);
   }
}
