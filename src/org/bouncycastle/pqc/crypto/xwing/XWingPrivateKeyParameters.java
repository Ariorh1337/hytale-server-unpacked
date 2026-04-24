package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingPrivateKeyParameters extends XWingKeyParameters {
   private final transient byte[] seed;
   private final transient MLKEMPrivateKeyParameters kyberPrivateKey;
   private final transient X25519PrivateKeyParameters xdhPrivateKey;
   private final transient MLKEMPublicKeyParameters kyberPublicKey;
   private final transient X25519PublicKeyParameters xdhPublicKey;

   public XWingPrivateKeyParameters(
      byte[] var1, MLKEMPrivateKeyParameters var2, X25519PrivateKeyParameters var3, MLKEMPublicKeyParameters var4, X25519PublicKeyParameters var5
   ) {
      super(true);
      this.seed = Arrays.clone(var1);
      this.kyberPrivateKey = var2;
      this.xdhPrivateKey = var3;
      this.kyberPublicKey = var4;
      this.xdhPublicKey = var5;
   }

   @Deprecated
   public XWingPrivateKeyParameters(
      byte[] var1,
      org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters var2,
      X25519PrivateKeyParameters var3,
      org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters var4,
      X25519PublicKeyParameters var5
   ) {
      super(true);
      MLKEMParameters var6;
      if (var4.getParameters().getName().equals("ML-KEM-512")) {
         var6 = MLKEMParameters.ml_kem_512;
      } else if (var4.getParameters().getName().equals("ML-KEM-768")) {
         var6 = MLKEMParameters.ml_kem_768;
      } else {
         var6 = MLKEMParameters.ml_kem_1024;
      }

      MLKEMPublicKeyParameters var7 = new MLKEMPublicKeyParameters(var6, var4.getEncoded());
      this.seed = Arrays.clone(var1);
      this.kyberPrivateKey = new MLKEMPrivateKeyParameters(var6, var2.getEncoded(), var7);
      this.xdhPrivateKey = var3;
      this.kyberPublicKey = var7;
      this.xdhPublicKey = var5;
   }

   public XWingPrivateKeyParameters(byte[] var1) {
      super(true);
      XWingPrivateKeyParameters var2 = (XWingPrivateKeyParameters)XWingKeyPairGenerator.genKeyPair(var1).getPrivate();
      this.seed = var2.seed;
      this.kyberPrivateKey = var2.kyberPrivateKey;
      this.xdhPrivateKey = var2.xdhPrivateKey;
      this.kyberPublicKey = var2.kyberPublicKey;
      this.xdhPublicKey = var2.xdhPublicKey;
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }

   MLKEMPrivateKeyParameters getKyberPrivateKey() {
      return this.kyberPrivateKey;
   }

   MLKEMPublicKeyParameters getKyberPublicKey() {
      return this.kyberPublicKey;
   }

   X25519PrivateKeyParameters getXDHPrivateKey() {
      return this.xdhPrivateKey;
   }

   X25519PublicKeyParameters getXDHPublicKey() {
      return this.xdhPublicKey;
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.seed);
   }
}
