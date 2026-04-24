package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class ECDHKEKGenerator implements DigestDerivationFunction {
   private DigestDerivationFunction kdf;
   private ASN1ObjectIdentifier algorithm;
   private int keySize;
   private byte[] z;

   public ECDHKEKGenerator(Digest var1) {
      this.kdf = new KDF2BytesGenerator(var1);
   }

   @Override
   public void init(DerivationParameters var1) {
      DHKDFParameters var2 = (DHKDFParameters)var1;
      this.algorithm = var2.getAlgorithm();
      this.keySize = var2.getKeySize();
      this.z = var2.getZ();
   }

   @Override
   public Digest getDigest() {
      return this.kdf.getDigest();
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("output buffer too small");
      }

      AlgorithmIdentifier var4 = new AlgorithmIdentifier(this.algorithm, DERNull.INSTANCE);
      DEROctetString var5 = DEROctetString.withContents(Pack.intToBigEndian(this.keySize));
      DERSequence var6 = new DERSequence(var4, new DERTaggedObject(2, var5));

      try {
         byte[] var7 = var6.getEncoded("DER");
         this.kdf.init(new KDFParameters(this.z, var7));
      } catch (IOException var8) {
         throw new IllegalArgumentException("unable to initialise kdf: " + var8.getMessage());
      }

      return this.kdf.generateBytes(var1, var2, var3);
   }
}
