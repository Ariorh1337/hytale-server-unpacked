package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Fingerprint;

public class RSAUtil {
   public static final ASN1ObjectIdentifier[] rsaOids = new ASN1ObjectIdentifier[]{
      PKCSObjectIdentifiers.rsaEncryption, X509ObjectIdentifiers.id_ea_rsa, PKCSObjectIdentifiers.id_RSAES_OAEP, PKCSObjectIdentifiers.id_RSASSA_PSS
   };

   public static boolean isRsaOid(ASN1ObjectIdentifier var0) {
      for (int var1 = 0; var1 != rsaOids.length; var1++) {
         if (var0.equals(rsaOids[var1])) {
            return true;
         }
      }

      return false;
   }

   static RSAKeyParameters generatePublicKeyParameter(RSAPublicKey var0) {
      return var0 instanceof BCRSAPublicKey
         ? ((BCRSAPublicKey)var0).engineGetKeyParameters()
         : new RSAKeyParameters(false, var0.getModulus(), var0.getPublicExponent());
   }

   static RSAKeyParameters generatePrivateKeyParameter(RSAPrivateKey var0) {
      if (var0 instanceof BCRSAPrivateKey) {
         return ((BCRSAPrivateKey)var0).engineGetKeyParameters();
      } else if (var0 instanceof RSAPrivateCrtKey) {
         RSAPrivateCrtKey var2 = (RSAPrivateCrtKey)var0;
         return new RSAPrivateCrtKeyParameters(
            var2.getModulus(),
            var2.getPublicExponent(),
            var2.getPrivateExponent(),
            var2.getPrimeP(),
            var2.getPrimeQ(),
            var2.getPrimeExponentP(),
            var2.getPrimeExponentQ(),
            var2.getCrtCoefficient()
         );
      } else {
         RSAPrivateKey var1 = var0;
         return new RSAKeyParameters(true, var1.getModulus(), var1.getPrivateExponent());
      }
   }

   static String generateKeyFingerprint(BigInteger var0) {
      return new Fingerprint(var0.toByteArray()).toString();
   }

   static String generateExponentFingerprint(BigInteger var0) {
      return new Fingerprint(var0.toByteArray(), 32).toString();
   }
}
