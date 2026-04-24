package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class KeySpecificInfo extends ASN1Object {
   private final ASN1ObjectIdentifier algorithm;
   private final ASN1OctetString counter;

   public static KeySpecificInfo getInstance(Object var0) {
      if (var0 instanceof KeySpecificInfo) {
         return (KeySpecificInfo)var0;
      } else {
         return var0 != null ? new KeySpecificInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static KeySpecificInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return new KeySpecificInfo(ASN1Sequence.getInstance(var0, var1));
   }

   public static KeySpecificInfo getTagged(ASN1TaggedObject var0, boolean var1) {
      return new KeySpecificInfo(ASN1Sequence.getTagged(var0, var1));
   }

   private KeySpecificInfo(ASN1Sequence var1) {
      int var2 = var1.size();
      if (var2 != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      }

      this.algorithm = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.counter = ASN1OctetString.getInstance(var1.getObjectAt(1));
   }

   public KeySpecificInfo(ASN1ObjectIdentifier var1, ASN1OctetString var2) {
      if (var1 == null) {
         throw new NullPointerException("'algorithm' cannot be null");
      }

      if (var2 == null) {
         throw new NullPointerException("'counter' cannot be null");
      }

      this.algorithm = var1;
      this.counter = var2;
   }

   public ASN1ObjectIdentifier getAlgorithm() {
      return this.algorithm;
   }

   public ASN1OctetString getCounter() {
      return this.counter;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algorithm, this.counter);
   }
}
