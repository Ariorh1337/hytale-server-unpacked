package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class SCVPReqRes extends ASN1Object {
   private final ContentInfo request;
   private final ContentInfo response;

   public static SCVPReqRes getInstance(Object var0) {
      if (var0 instanceof SCVPReqRes) {
         return (SCVPReqRes)var0;
      } else {
         return var0 != null ? new SCVPReqRes(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static SCVPReqRes getInstance(ASN1TaggedObject var0, boolean var1) {
      return new SCVPReqRes(ASN1Sequence.getInstance(var0, var1));
   }

   public static SCVPReqRes getTagged(ASN1TaggedObject var0, boolean var1) {
      return new SCVPReqRes(ASN1Sequence.getTagged(var0, var1));
   }

   private SCVPReqRes(ASN1Sequence var1) {
      int var2 = var1.size();
      int var3 = 0;
      if (var2 >= 1 && var2 <= 2) {
         ContentInfo var4 = null;
         if (var3 < var2) {
            ASN1TaggedObject var5 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 0);
            if (var5 != null) {
               var3++;
               var4 = ContentInfo.getTagged(var5, true);
            }
         }

         this.request = var4;
         this.response = ContentInfo.getInstance(var1.getObjectAt(var3++));
         if (var3 != var2) {
            throw new IllegalArgumentException("Unexpected elements in sequence");
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      }
   }

   public SCVPReqRes(ContentInfo var1) {
      this(null, var1);
   }

   public SCVPReqRes(ContentInfo var1, ContentInfo var2) {
      if (var2 == null) {
         throw new NullPointerException("'response' cannot be null");
      }

      this.request = var1;
      this.response = var2;
   }

   public ContentInfo getRequest() {
      return this.request;
   }

   public ContentInfo getResponse() {
      return this.response;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.request == null ? new DERSequence(this.response) : new DERSequence(new DERTaggedObject(true, 0, this.request), this.response);
   }
}
