package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class PKCS12StoreParameter implements LoadStoreParameter {
   private final OutputStream out;
   private final ProtectionParameter protectionParameter;
   private final boolean forDEREncoding;
   private final boolean overwriteFriendlyName;
   private final AlgorithmIdentifier macAlgorithm;
   private final boolean useISO8859d1ForDecryption;

   public static PKCS12StoreParameter.PBMAC1WithPBKDF2Builder pbmac1WithPBKDF2Builder() {
      return new PKCS12StoreParameter.PBMAC1WithPBKDF2Builder();
   }

   public static PKCS12StoreParameter.Builder builder(OutputStream var0, char[] var1) {
      return builder(var0, new PasswordProtection(var1));
   }

   public static PKCS12StoreParameter.Builder builder(OutputStream var0, ProtectionParameter var1) {
      return new PKCS12StoreParameter.Builder(var0, var1);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2) {
      this(var1, var2, false);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2) {
      this(var1, var2, false, true);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2, boolean var3) {
      this(var1, new PasswordProtection(var2), var3, true);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2, boolean var3) {
      this(var1, var2, var3, true);
   }

   public PKCS12StoreParameter(OutputStream var1, char[] var2, boolean var3, boolean var4) {
      this(var1, new PasswordProtection(var2), var3, var4);
   }

   public PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2, boolean var3, boolean var4) {
      this(var1, var2, var3, var4, new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE), false);
   }

   private PKCS12StoreParameter(OutputStream var1, ProtectionParameter var2, boolean var3, boolean var4, AlgorithmIdentifier var5, boolean var6) {
      this.out = var1;
      this.protectionParameter = var2;
      this.forDEREncoding = var3;
      this.overwriteFriendlyName = var4;
      this.macAlgorithm = var5;
      this.useISO8859d1ForDecryption = var6;
   }

   public OutputStream getOutputStream() {
      return this.out;
   }

   @Override
   public ProtectionParameter getProtectionParameter() {
      return this.protectionParameter;
   }

   public boolean isForDEREncoding() {
      return this.forDEREncoding;
   }

   public boolean isOverwriteFriendlyName() {
      return this.overwriteFriendlyName;
   }

   public AlgorithmIdentifier getMacAlgorithm() {
      return this.macAlgorithm;
   }

   public boolean useISO8859d1ForDecryption() {
      return this.useISO8859d1ForDecryption;
   }

   public static class Builder {
      private final OutputStream out;
      private final ProtectionParameter protectionParameter;
      private boolean forDEREncoding = true;
      private boolean overwriteFriendlyName = true;
      private boolean useISO8859d1ForDecryption = false;
      private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);

      private Builder(OutputStream var1, ProtectionParameter var2) {
         this.out = var1;
         this.protectionParameter = var2;
      }

      public PKCS12StoreParameter.Builder setDEREncoding(boolean var1) {
         this.forDEREncoding = var1;
         return this;
      }

      public PKCS12StoreParameter.Builder setOverwriteFriendlyName(boolean var1) {
         this.overwriteFriendlyName = var1;
         return this;
      }

      public PKCS12StoreParameter.Builder setUseISO8859d1ForDecryption(boolean var1) {
         this.useISO8859d1ForDecryption = var1;
         return this;
      }

      public PKCS12StoreParameter.Builder setMacAlgorithm(AlgorithmIdentifier var1) {
         this.macAlgorithm = var1;
         return this;
      }

      public PKCS12StoreParameter build() {
         return new PKCS12StoreParameter(
            this.out, this.protectionParameter, this.forDEREncoding, this.overwriteFriendlyName, this.macAlgorithm, this.useISO8859d1ForDecryption
         );
      }
   }

   public static class PBMAC1WithPBKDF2Builder {
      private int iterationCount = 16384;
      private byte[] salt = null;
      private int keySizeinBits = 256;
      private ASN1ObjectIdentifier prf = PKCSObjectIdentifiers.id_hmacWithSHA256;
      private ASN1ObjectIdentifier mac = PKCSObjectIdentifiers.id_hmacWithSHA512;

      PBMAC1WithPBKDF2Builder() {
      }

      public PKCS12StoreParameter.PBMAC1WithPBKDF2Builder setIterationCount(int var1) {
         this.iterationCount = var1;
         return this;
      }

      public PKCS12StoreParameter.PBMAC1WithPBKDF2Builder setSalt(byte[] var1) {
         this.salt = Arrays.clone(var1);
         return this;
      }

      public PKCS12StoreParameter.PBMAC1WithPBKDF2Builder setKeySize(int var1) {
         this.keySizeinBits = var1;
         return this;
      }

      public PKCS12StoreParameter.PBMAC1WithPBKDF2Builder setPrf(ASN1ObjectIdentifier var1) {
         this.prf = var1;
         return this;
      }

      public PKCS12StoreParameter.PBMAC1WithPBKDF2Builder setMac(ASN1ObjectIdentifier var1) {
         this.mac = var1;
         return this;
      }

      public AlgorithmIdentifier build() {
         if (this.salt == null) {
            throw new IllegalStateException("salt must be non-null");
         }

         PBKDF2Params var1 = new PBKDF2Params(this.salt, this.iterationCount, this.keySizeinBits, new AlgorithmIdentifier(this.prf));
         AlgorithmIdentifier var2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, var1);
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(this.mac);
         PBMAC1Params var4 = new PBMAC1Params(var2, var3);
         return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, var4);
      }
   }
}
