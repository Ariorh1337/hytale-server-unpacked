package org.bouncycastle.asn1.sec;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.BigIntegers;

public class ECPrivateKey extends ASN1Object {
   private final ASN1Sequence seq;

   public static ECPrivateKey getInstance(Object var0) {
      if (var0 instanceof ECPrivateKey) {
         return (ECPrivateKey)var0;
      } else {
         return var0 != null ? new ECPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ECPrivateKey getInstance(ASN1TaggedObject var0, boolean var1) {
      return new ECPrivateKey(ASN1Sequence.getInstance(var0, var1));
   }

   public static ECPrivateKey getTagged(ASN1TaggedObject var0, boolean var1) {
      return new ECPrivateKey(ASN1Sequence.getTagged(var0, var1));
   }

   private ECPrivateKey(ASN1Sequence var1) {
      this.seq = var1;
   }

   public ECPrivateKey(int var1, BigInteger var2) {
      byte[] var3 = BigIntegers.asUnsignedByteArray((var1 + 7) / 8, var2);
      this.seq = new DERSequence(ASN1Integer.ONE, new DEROctetString(var3));
   }

   public ECPrivateKey(int var1, BigInteger var2, ASN1Encodable var3) {
      this(var1, var2, null, var3);
   }

   public ECPrivateKey(int var1, BigInteger var2, ASN1BitString var3, ASN1Encodable var4) {
      byte[] var5 = BigIntegers.asUnsignedByteArray((var1 + 7) / 8, var2);
      ASN1EncodableVector var6 = new ASN1EncodableVector(4);
      var6.add(ASN1Integer.ONE);
      var6.add(new DEROctetString(var5));
      if (var4 != null) {
         var6.add(new DERTaggedObject(true, 0, var4));
      }

      if (var3 != null) {
         var6.add(new DERTaggedObject(true, 1, var3));
      }

      this.seq = new DERSequence(var6);
   }

   public ECPrivateKey(ASN1OctetString var1, ASN1Encodable var2, ASN1BitString var3) {
      ASN1EncodableVector var4 = new ASN1EncodableVector(4);
      var4.add(ASN1Integer.ONE);
      var4.add(var1);
      if (var2 != null) {
         var4.add(new DERTaggedObject(true, 0, var2));
      }

      if (var3 != null) {
         var4.add(new DERTaggedObject(true, 1, var3));
      }

      this.seq = new DERSequence(var4);
   }

   public BigInteger getKey() {
      return new BigInteger(1, this.getPrivateKey().getOctets());
   }

   public ASN1OctetString getPrivateKey() {
      return (ASN1OctetString)this.seq.getObjectAt(1);
   }

   public ASN1BitString getPublicKey() {
      return (ASN1BitString)this.getObjectInTag(1, 3);
   }

   public ASN1Object getParametersObject() {
      return this.getObjectInTag(0, -1);
   }

   private ASN1Object getObjectInTag(int var1, int var2) {
      int var3 = 0;

      for (int var4 = this.seq.size(); var3 < var4; var3++) {
         ASN1Encodable var5 = this.seq.getObjectAt(var3);
         ASN1TaggedObject var6 = ASN1TaggedObject.getContextOptional(var5, var1);
         if (var6 != null) {
            return var2 < 0 ? var6.getExplicitBaseObject().toASN1Primitive() : var6.getBaseUniversal(true, var2);
         }
      }

      return null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.seq;
   }
}
