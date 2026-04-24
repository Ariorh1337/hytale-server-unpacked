package org.bouncycastle.jcajce.provider.kdf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class KDFAlgorithmProvider extends AlgorithmProvider {
   void addKDFAlgorithm(ConfigurableProvider var1, String var2, String var3) {
      this.addKDFAlgorithm(var1, var2, var3, null);
   }

   void addKDFAlgorithm(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
      var1.addAlgorithm("KDF." + var2, var3);
      if (var4 != null) {
         this.registerKDFAliasOid(var1, var4, var2);
      }
   }

   void registerKDFAlias(ConfigurableProvider var1, String var2, String var3) {
      var1.addAlgorithm("Alg.Alias.KDF." + var2, var3);
   }

   void registerKDFAliasOid(ConfigurableProvider var1, ASN1ObjectIdentifier var2, String var3) {
      var1.addAlgorithm("Alg.Alias.KDF." + var2, var3);
      var1.addAlgorithm("Alg.Alias.KDF.OID." + var2, var3);
   }
}
