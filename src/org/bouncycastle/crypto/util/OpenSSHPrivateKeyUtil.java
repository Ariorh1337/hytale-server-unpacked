package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class OpenSSHPrivateKeyUtil {
   private static final byte[] AUTH_MAGIC = Strings.toByteArray("openssh-key-v1\u0000");

   private OpenSSHPrivateKeyUtil() {
   }

   public static byte[] encodePrivateKey(AsymmetricKeyParameter var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("param is null");
      }

      if (var0 instanceof RSAPrivateCrtKeyParameters) {
         PrivateKeyInfo var10 = PrivateKeyInfoFactory.createPrivateKeyInfo(var0);
         return var10.parsePrivateKey().toASN1Primitive().getEncoded();
      }

      if (var0 instanceof ECPrivateKeyParameters) {
         PrivateKeyInfo var9 = PrivateKeyInfoFactory.createPrivateKeyInfo(var0);
         return var9.parsePrivateKey().toASN1Primitive().getEncoded();
      }

      if (var0 instanceof DSAPrivateKeyParameters) {
         DSAPrivateKeyParameters var8 = (DSAPrivateKeyParameters)var0;
         DSAParameters var11 = var8.getParameters();
         BigInteger var12 = var11.getG().modPow(var8.getX(), var11.getP());
         ASN1EncodableVector var14 = new ASN1EncodableVector();
         var14.add(ASN1Integer.ZERO);
         var14.add(new ASN1Integer(var11.getP()));
         var14.add(new ASN1Integer(var11.getQ()));
         var14.add(new ASN1Integer(var11.getG()));
         var14.add(new ASN1Integer(var12));
         var14.add(new ASN1Integer(var8.getX()));

         try {
            return new DERSequence(var14).getEncoded();
         } catch (Exception var7) {
            throw new IllegalStateException("unable to encode DSAPrivateKeyParameters " + var7.getMessage());
         }
      } else if (var0 instanceof Ed25519PrivateKeyParameters) {
         Ed25519PrivateKeyParameters var1 = (Ed25519PrivateKeyParameters)var0;
         Ed25519PublicKeyParameters var2 = var1.generatePublicKey();
         SSHBuilder var3 = new SSHBuilder();
         var3.writeBytes(AUTH_MAGIC);
         var3.writeString("none");
         var3.writeString("none");
         var3.writeString("");
         var3.u32(1);
         byte[] var4 = OpenSSHPublicKeyUtil.encodePublicKey(var2);
         var3.writeBlock(var4);
         SSHBuilder var13 = new SSHBuilder();
         int var5 = CryptoServicesRegistrar.getSecureRandom().nextInt();
         var13.u32(var5);
         var13.u32(var5);
         var13.writeString("ssh-ed25519");
         byte[] var6 = var2.getEncoded();
         var13.writeBlock(var6);
         var13.writeBlock(Arrays.concatenate(var1.getEncoded(), var6));
         var13.writeString("");
         var3.writeBlock(var13.getPaddedBytes());
         return var3.getBytes();
      } else {
         throw new IllegalArgumentException("unable to convert " + var0.getClass().getName() + " to openssh private key");
      }
   }

   public static AsymmetricKeyParameter parsePrivateKeyBlob(byte[] var0) {
      AsymmetricKeyParameter var1 = null;
      if (var0[0] == 48) {
         ASN1Sequence var2 = ASN1Sequence.getInstance(var0);
         if (var2.size() == 6) {
            if (allIntegers(var2) && ((ASN1Integer)var2.getObjectAt(0)).getPositiveValue().equals(BigIntegers.ZERO)) {
               var1 = new DSAPrivateKeyParameters(
                  ((ASN1Integer)var2.getObjectAt(5)).getPositiveValue(),
                  new DSAParameters(
                     ((ASN1Integer)var2.getObjectAt(1)).getPositiveValue(),
                     ((ASN1Integer)var2.getObjectAt(2)).getPositiveValue(),
                     ((ASN1Integer)var2.getObjectAt(3)).getPositiveValue()
                  )
               );
            }
         } else if (var2.size() == 9) {
            if (allIntegers(var2) && ((ASN1Integer)var2.getObjectAt(0)).getPositiveValue().equals(BigIntegers.ZERO)) {
               RSAPrivateKey var3 = RSAPrivateKey.getInstance(var2);
               var1 = new RSAPrivateCrtKeyParameters(
                  var3.getModulus(),
                  var3.getPublicExponent(),
                  var3.getPrivateExponent(),
                  var3.getPrime1(),
                  var3.getPrime2(),
                  var3.getExponent1(),
                  var3.getExponent2(),
                  var3.getCoefficient()
               );
            }
         } else if (var2.size() == 4 && var2.getObjectAt(3) instanceof ASN1TaggedObject && var2.getObjectAt(2) instanceof ASN1TaggedObject) {
            ECPrivateKey var21 = ECPrivateKey.getInstance(var2);
            X962Parameters var4 = X962Parameters.getInstance(var21.getParametersObject().toASN1Primitive());
            ECDomainParameters var5;
            if (var4.isNamedCurve()) {
               ASN1ObjectIdentifier var6 = ASN1ObjectIdentifier.getInstance(var4.getParameters());
               var5 = ECNamedDomainParameters.lookup(var6);
            } else {
               X9ECParameters var25 = X9ECParameters.getInstance(var4.getParameters());
               var5 = new ECDomainParameters(var25);
            }

            BigInteger var26 = var21.getKey();
            var1 = new ECPrivateKeyParameters(var26, var5);
         }
      } else {
         SSHBuffer var20 = new SSHBuffer(AUTH_MAGIC, var0);
         String var22 = var20.readString();
         if (!"none".equals(var22)) {
            throw new IllegalStateException("encrypted keys not supported");
         }

         var20.skipBlock();
         var20.skipBlock();
         int var23 = var20.readU32();
         if (var23 != 1) {
            throw new IllegalStateException("multiple keys not supported");
         }

         OpenSSHPublicKeyUtil.parsePublicKey(var20.readBlock());
         byte[] var24 = var20.readPaddedBlock();
         if (var20.hasRemaining()) {
            throw new IllegalArgumentException("decoded key has trailing data");
         }

         SSHBuffer var27 = new SSHBuffer(var24);
         int var7 = var27.readU32();
         int var8 = var27.readU32();
         if (var7 != var8) {
            throw new IllegalStateException("private key check values are not the same");
         }

         String var9 = var27.readString();
         if ("ssh-ed25519".equals(var9)) {
            var27.readBlock();
            byte[] var10 = var27.readBlock();
            if (var10.length != 64) {
               throw new IllegalStateException("private key value of wrong length");
            }

            var1 = new Ed25519PrivateKeyParameters(var10, 0);
         } else if (var9.startsWith("ecdsa")) {
            ASN1ObjectIdentifier var28 = SSHNamedCurves.getByName(Strings.fromByteArray(var27.readBlock()));
            if (var28 == null) {
               throw new IllegalStateException("OID not found for: " + var9);
            }

            X9ECParameters var11 = NISTNamedCurves.getByOID(var28);
            if (var11 == null) {
               throw new IllegalStateException("Curve not found for: " + var28);
            }

            var27.readBlock();
            byte[] var12 = var27.readBlock();
            var1 = new ECPrivateKeyParameters(new BigInteger(1, var12), new ECNamedDomainParameters(var28, var11));
         } else if (var9.startsWith("ssh-rsa")) {
            BigInteger var29 = new BigInteger(1, var27.readBlock());
            BigInteger var30 = new BigInteger(1, var27.readBlock());
            BigInteger var31 = new BigInteger(1, var27.readBlock());
            BigInteger var13 = new BigInteger(1, var27.readBlock());
            BigInteger var14 = new BigInteger(1, var27.readBlock());
            BigInteger var15 = new BigInteger(1, var27.readBlock());
            BigInteger var16 = var14.subtract(BigIntegers.ONE);
            BigInteger var17 = var15.subtract(BigIntegers.ONE);
            BigInteger var18 = var31.remainder(var16);
            BigInteger var19 = var31.remainder(var17);
            var1 = new RSAPrivateCrtKeyParameters(var29, var30, var31, var14, var15, var18, var19, var13);
         }

         var27.skipBlock();
         if (var27.hasRemaining()) {
            throw new IllegalArgumentException("private key block has trailing data");
         }
      }

      if (var1 == null) {
         throw new IllegalArgumentException("unable to parse key");
      } else {
         return var1;
      }
   }

   private static boolean allIntegers(ASN1Sequence var0) {
      for (int var1 = 0; var1 < var0.size(); var1++) {
         if (!(var0.getObjectAt(var1) instanceof ASN1Integer)) {
            return false;
         }
      }

      return true;
   }
}
