package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.KEMParameters;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.generators.MLKEMKeyPairGenerator;
import org.bouncycastle.crypto.kems.MLKEMExtractor;
import org.bouncycastle.crypto.kems.MLKEMGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.MLKEMKeyGenerationParameters;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;

public class OtherInfoGenerator {
   protected final DEROtherInfo.Builder otherInfoBuilder;
   protected final SecureRandom random;
   protected boolean used = false;

   public OtherInfoGenerator(AlgorithmIdentifier var1, byte[] var2, byte[] var3, SecureRandom var4) {
      this.otherInfoBuilder = new DEROtherInfo.Builder(var1, var2, var3);
      this.random = var4;
   }

   private static byte[] getEncoded(AsymmetricKeyParameter var0) {
      try {
         return SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(var0).getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   private static AsymmetricKeyParameter getPublicKey(byte[] var0) throws IOException {
      return PublicKeyFactory.createKey(var0);
   }

   public static class PartyU extends OtherInfoGenerator {
      private AsymmetricCipherKeyPair aKp;
      private EncapsulatedSecretExtractor encSE;

      public PartyU(KEMParameters var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4, SecureRandom var5) {
         super(var2, var3, var4, var5);
         if (var1 instanceof MLKEMParameters) {
            MLKEMKeyPairGenerator var6 = new MLKEMKeyPairGenerator();
            var6.init(new MLKEMKeyGenerationParameters(var5, (MLKEMParameters)var1));
            this.aKp = var6.generateKeyPair();
            this.encSE = new MLKEMExtractor((MLKEMPrivateKeyParameters)this.aKp.getPrivate());
         } else {
            throw new IllegalArgumentException("unknown KEMParameters");
         }
      }

      public OtherInfoGenerator withSuppPubInfo(byte[] var1) {
         this.otherInfoBuilder.withSuppPubInfo(var1);
         return this;
      }

      public byte[] getSuppPrivInfoPartA() {
         return OtherInfoGenerator.getEncoded(this.aKp.getPublic());
      }

      public DEROtherInfo generate(byte[] var1) {
         this.otherInfoBuilder.withSuppPrivInfo(this.encSE.extractSecret(var1));
         return this.otherInfoBuilder.build();
      }
   }

   public static class PartyV extends OtherInfoGenerator {
      private EncapsulatedSecretGenerator encSG;

      public PartyV(KEMParameters var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4, SecureRandom var5) {
         super(var2, var3, var4, var5);
         if (var1 instanceof MLKEMParameters) {
            this.encSG = new MLKEMGenerator(var5);
         } else {
            throw new IllegalArgumentException("unknown KEMParameters");
         }
      }

      public OtherInfoGenerator withSuppPubInfo(byte[] var1) {
         this.otherInfoBuilder.withSuppPubInfo(var1);
         return this;
      }

      public byte[] getSuppPrivInfoPartB(byte[] var1) {
         this.used = false;

         try {
            SecretWithEncapsulation var2 = this.encSG.generateEncapsulated(OtherInfoGenerator.getPublicKey(var1));
            this.otherInfoBuilder.withSuppPrivInfo(var2.getSecret());
            return var2.getEncapsulation();
         } catch (IOException var3) {
            throw new IllegalArgumentException("cannot decode public key");
         }
      }

      public DEROtherInfo generate() {
         if (this.used) {
            throw new IllegalStateException("builder already used");
         }

         this.used = true;
         return this.otherInfoBuilder.build();
      }
   }
}
