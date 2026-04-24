package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECGOST3410NamedCurveTable {
   public static ECNamedCurveParameterSpec getParameterSpec(String var0) {
      X9ECParameters var1 = ECGOST3410NamedCurves.getByNameX9(var0);
      if (var1 == null) {
         ASN1ObjectIdentifier var2 = ASN1ObjectIdentifier.tryFromID(var0);
         if (var2 != null) {
            var1 = ECGOST3410NamedCurves.getByOIDX9(var2);
         }
      }

      return var1 == null ? null : new ECNamedCurveParameterSpec(var0, var1.getCurve(), var1.getG(), var1.getN(), var1.getH(), var1.getSeed());
   }

   public static Enumeration getNames() {
      return ECGOST3410NamedCurves.getNames();
   }
}
