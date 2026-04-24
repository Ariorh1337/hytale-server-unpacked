package org.bouncycastle.asn1.cmc;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class CMCStatus extends ASN1Object {
   public static final CMCStatus success = new CMCStatus(ASN1Integer.valueOf(0));
   public static final CMCStatus failed = new CMCStatus(ASN1Integer.valueOf(2));
   public static final CMCStatus pending = new CMCStatus(ASN1Integer.valueOf(3));
   public static final CMCStatus noSupport = new CMCStatus(ASN1Integer.valueOf(4));
   public static final CMCStatus confirmRequired = new CMCStatus(ASN1Integer.valueOf(5));
   public static final CMCStatus popRequired = new CMCStatus(ASN1Integer.valueOf(6));
   public static final CMCStatus partial = new CMCStatus(ASN1Integer.valueOf(7));
   private static Map range = new HashMap();
   private final ASN1Integer value;

   private CMCStatus(ASN1Integer var1) {
      this.value = var1;
   }

   public static CMCStatus getInstance(Object var0) {
      if (var0 instanceof CMCStatus) {
         return (CMCStatus)var0;
      }

      if (var0 != null) {
         CMCStatus var1 = (CMCStatus)range.get(ASN1Integer.getInstance(var0));
         if (var1 != null) {
            return var1;
         } else {
            throw new IllegalArgumentException("unknown object in getInstance(): " + var0.getClass().getName());
         }
      } else {
         return null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.value;
   }

   static {
      range.put(success.value, success);
      range.put(failed.value, failed);
      range.put(pending.value, pending);
      range.put(noSupport.value, noSupport);
      range.put(confirmRequired.value, confirmRequired);
      range.put(popRequired.value, popRequired);
      range.put(partial.value, partial);
   }
}
