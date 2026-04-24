package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OtherInfo extends ASN1Object {
   private final KeySpecificInfo keyInfo;
   private final ASN1OctetString partyAInfo;
   private final ASN1OctetString suppPubInfo;

   public static OtherInfo getInstance(Object var0) {
      if (var0 instanceof OtherInfo) {
         return (OtherInfo)var0;
      } else {
         return var0 != null ? new OtherInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static OtherInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return new OtherInfo(ASN1Sequence.getInstance(var0, var1));
   }

   public static OtherInfo getTagged(ASN1TaggedObject var0, boolean var1) {
      return new OtherInfo(ASN1Sequence.getTagged(var0, var1));
   }

   private OtherInfo(ASN1Sequence var1) {
      int var2 = var1.size();
      int var3 = 0;
      if (var2 >= 2 && var2 <= 3) {
         this.keyInfo = KeySpecificInfo.getInstance(var1.getObjectAt(var3++));
         ASN1OctetString var4 = null;
         if (var3 < var2) {
            ASN1TaggedObject var5 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 0);
            if (var5 != null) {
               var3++;
               var4 = ASN1OctetString.getTagged(var5, true);
            }
         }

         this.partyAInfo = var4;
         ASN1TaggedObject var8 = ASN1TaggedObject.getContextInstance(var1.getObjectAt(var3++), 2);
         this.suppPubInfo = ASN1OctetString.getTagged(var8, true);
         if (var3 != var2) {
            throw new IllegalArgumentException("Unexpected elements in sequence");
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      }
   }

   public OtherInfo(KeySpecificInfo var1, ASN1OctetString var2, ASN1OctetString var3) {
      if (var1 == null) {
         throw new NullPointerException("'keyInfo' cannot be null");
      }

      if (var3 == null) {
         throw new NullPointerException("'suppPubInfo' cannot be null");
      }

      this.keyInfo = var1;
      this.partyAInfo = var2;
      this.suppPubInfo = var3;
   }

   public KeySpecificInfo getKeyInfo() {
      return this.keyInfo;
   }

   public ASN1OctetString getPartyAInfo() {
      return this.partyAInfo;
   }

   public ASN1OctetString getSuppPubInfo() {
      return this.suppPubInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.keyInfo);
      if (this.partyAInfo != null) {
         var1.add(new DERTaggedObject(0, this.partyAInfo));
      }

      var1.add(new DERTaggedObject(2, this.suppPubInfo));
      return new DERSequence(var1);
   }
}
