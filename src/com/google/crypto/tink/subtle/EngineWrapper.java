package com.google.crypto.tink.subtle;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;

public interface EngineWrapper<T> {
   T getInstance(String algorithm, Provider provider) throws GeneralSecurityException;

   class TCipher implements EngineWrapper<Cipher> {
      public Cipher getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
      }
   }

   class TKeyAgreement implements EngineWrapper<KeyAgreement> {
      public KeyAgreement getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? KeyAgreement.getInstance(algorithm) : KeyAgreement.getInstance(algorithm, provider);
      }
   }

   class TKeyFactory implements EngineWrapper<KeyFactory> {
      public KeyFactory getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? KeyFactory.getInstance(algorithm) : KeyFactory.getInstance(algorithm, provider);
      }
   }

   class TKeyPairGenerator implements EngineWrapper<KeyPairGenerator> {
      public KeyPairGenerator getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? KeyPairGenerator.getInstance(algorithm) : KeyPairGenerator.getInstance(algorithm, provider);
      }
   }

   class TMac implements EngineWrapper<Mac> {
      public Mac getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? Mac.getInstance(algorithm) : Mac.getInstance(algorithm, provider);
      }
   }

   class TMessageDigest implements EngineWrapper<MessageDigest> {
      public MessageDigest getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? MessageDigest.getInstance(algorithm) : MessageDigest.getInstance(algorithm, provider);
      }
   }

   class TSignature implements EngineWrapper<Signature> {
      public Signature getInstance(String algorithm, Provider provider) throws GeneralSecurityException {
         return provider == null ? Signature.getInstance(algorithm) : Signature.getInstance(algorithm, provider);
      }
   }
}
