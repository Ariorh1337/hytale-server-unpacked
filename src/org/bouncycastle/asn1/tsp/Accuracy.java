package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class Accuracy extends ASN1Object {
   protected static final int MIN_MILLIS = 1;
   protected static final int MAX_MILLIS = 999;
   protected static final int MIN_MICROS = 1;
   protected static final int MAX_MICROS = 999;
   private final ASN1Integer seconds;
   private final ASN1Integer millis;
   private final ASN1Integer micros;

   public static Accuracy getInstance(Object var0) {
      if (var0 instanceof Accuracy) {
         return (Accuracy)var0;
      } else {
         return var0 != null ? new Accuracy(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static Accuracy getInstance(ASN1TaggedObject var0, boolean var1) {
      return new Accuracy(ASN1Sequence.getInstance(var0, var1));
   }

   public static Accuracy getTagged(ASN1TaggedObject var0, boolean var1) {
      return new Accuracy(ASN1Sequence.getTagged(var0, var1));
   }

   /** @deprecated */
   protected Accuracy() {
      this.seconds = null;
      this.millis = null;
      this.micros = null;
   }

   private Accuracy(ASN1Sequence var1) {
      int var2 = var1.size();
      int var3 = 0;
      if (var2 >= 0 && var2 <= 3) {
         ASN1Integer var4 = null;
         if (var3 < var2) {
            ASN1Encodable var5 = var1.getObjectAt(var3);
            if (var5 instanceof ASN1Integer) {
               var3++;
               var4 = (ASN1Integer)var5;
            }
         }

         this.seconds = var4;
         ASN1Integer var8 = null;
         if (var3 < var2) {
            ASN1TaggedObject var6 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 0);
            if (var6 != null) {
               var3++;
               var8 = ASN1Integer.getInstance(var6, false);
            }
         }

         this.millis = var8;
         ASN1Integer var9 = null;
         if (var3 < var2) {
            ASN1TaggedObject var7 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 1);
            if (var7 != null) {
               var3++;
               var9 = ASN1Integer.getInstance(var7, false);
            }
         }

         this.micros = var9;
         if (var3 != var2) {
            throw new IllegalArgumentException("Unexpected elements in sequence");
         }

         this.validate();
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      }
   }

   public Accuracy(ASN1Integer var1, ASN1Integer var2, ASN1Integer var3) {
      this.seconds = var1;
      this.millis = var2;
      this.micros = var3;
      this.validate();
   }

   public ASN1Integer getSeconds() {
      return this.seconds;
   }

   public ASN1Integer getMillis() {
      return this.millis;
   }

   public ASN1Integer getMicros() {
      return this.micros;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.seconds != null) {
         var1.add(this.seconds);
      }

      if (this.millis != null) {
         var1.add(new DERTaggedObject(false, 0, this.millis));
      }

      if (this.micros != null) {
         var1.add(new DERTaggedObject(false, 1, this.micros));
      }

      return new DERSequence(var1);
   }

   private void validate() {
      if (this.millis != null) {
         int var1 = this.millis.intValueExact();
         if (var1 < 1 || var1 > 999) {
            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
         }
      }

      if (this.micros != null) {
         int var2 = this.micros.intValueExact();
         if (var2 < 1 || var2 > 999) {
            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
         }
      }
   }
}
