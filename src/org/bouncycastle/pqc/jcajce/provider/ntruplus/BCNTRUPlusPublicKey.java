package org.bouncycastle.pqc.jcajce.provider.ntruplus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.NTRUPlusKey;
import org.bouncycastle.pqc.jcajce.spec.NTRUPlusParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCNTRUPlusPublicKey implements PublicKey, NTRUPlusKey {
   private static final long serialVersionUID = 1L;
   private transient NTRUPlusPublicKeyParameters params;

   public BCNTRUPlusPublicKey(NTRUPlusPublicKeyParameters var1) {
      this.params = var1;
   }

   public BCNTRUPlusPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.params = (NTRUPlusPublicKeyParameters)PublicKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCNTRUPlusPublicKey) {
         BCNTRUPlusPublicKey var2 = (BCNTRUPlusPublicKey)var1;
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
         SubjectPublicKeyInfo var1 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.params);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public NTRUPlusParameterSpec getParameterSpec() {
      return NTRUPlusParameterSpec.fromName(this.params.getParameters().getName());
   }

   NTRUPlusPublicKeyParameters getKeyParams() {
      return this.params;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(SubjectPublicKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
