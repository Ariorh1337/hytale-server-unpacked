package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECNamedCurveTable {
   public static ECNamedCurveParameterSpec getParameterSpec(String var0) {
      ASN1ObjectIdentifier var1 = ASN1ObjectIdentifier.tryFromID(var0);
      X9ECParameters var2;
      if (var1 != null) {
         var2 = CustomNamedCurves.getByOID(var1);
      } else {
         var2 = CustomNamedCurves.getByName(var0);
      }

      if (var2 == null) {
         if (var1 != null) {
            var2 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(var1);
         } else {
            var2 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(var0);
         }
      }

      return var2 == null ? null : new ECNamedCurveParameterSpec(var0, var2.getCurve(), var2.getG(), var2.getN(), var2.getH(), var2.getSeed());
   }

   public static Enumeration getNames() {
      return org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
   }
}
