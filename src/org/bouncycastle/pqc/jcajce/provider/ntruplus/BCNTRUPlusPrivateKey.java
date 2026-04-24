package org.bouncycastle.pqc.jcajce.provider.ntruplus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.NTRUPlusKey;
import org.bouncycastle.pqc.jcajce.spec.NTRUPlusParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCNTRUPlusPrivateKey implements PrivateKey, NTRUPlusKey {
   private static final long serialVersionUID = 1L;
   private transient NTRUPlusPrivateKeyParameters params;
   private transient ASN1Set attributes;

   public BCNTRUPlusPrivateKey(NTRUPlusPrivateKeyParameters var1) {
      this.params = var1;
   }

   public BCNTRUPlusPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.params = (NTRUPlusPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCNTRUPlusPrivateKey) {
         BCNTRUPlusPrivateKey var2 = (BCNTRUPlusPrivateKey)var1;
         return Arrays.areEqual(this.params.getEncoded(), var2.params.getEncoded());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.params.getEncoded());
   }

   @Override
   public final String getAlgorithm() {
      return Strings.toUpperCase(this.params.getParameters().getName());
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public NTRUPlusParameterSpec getParameterSpec() {
      return NTRUPlusParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   NTRUPlusPrivateKeyParameters getKeyParams() {
      return this.params;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(PrivateKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
