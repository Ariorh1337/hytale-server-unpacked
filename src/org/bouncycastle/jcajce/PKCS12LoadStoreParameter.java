package org.bouncycastle.jcajce;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;

public class PKCS12LoadStoreParameter extends BCLoadStoreParameter {
   private final boolean useISO8859d1ForDecryption;

   private PKCS12LoadStoreParameter(PKCS12LoadStoreParameter.Builder var1) {
      super(var1.in, var1.out, var1.protectionParameter);
      this.useISO8859d1ForDecryption = var1.useISO8859d1ForDecryption;
   }

   public boolean useISO8859d1ForDecryption() {
      return this.useISO8859d1ForDecryption;
   }

   public static class Builder {
      private final OutputStream out;
      private final InputStream in;
      private final ProtectionParameter protectionParameter;
      private boolean useISO8859d1ForDecryption = false;

      public Builder() {
         this((OutputStream)null, (ProtectionParameter)null);
      }

      public Builder(OutputStream var1, char[] var2) {
         this(var1, new PasswordProtection(var2));
      }

      public Builder(OutputStream var1, ProtectionParameter var2) {
         this.in = null;
         this.out = var1;
         this.protectionParameter = var2;
      }

      public Builder(InputStream var1, char[] var2) {
         this(var1, new PasswordProtection(var2));
      }

      public Builder(InputStream var1, ProtectionParameter var2) {
         this.in = var1;
         this.out = null;
         this.protectionParameter = var2;
      }

      public PKCS12LoadStoreParameter.Builder setUseISO8859d1ForDecryption(boolean var1) {
         this.useISO8859d1ForDecryption = var1;
         return this;
      }

      public PKCS12LoadStoreParameter build() {
         return new PKCS12LoadStoreParameter(this);
      }
   }
}
