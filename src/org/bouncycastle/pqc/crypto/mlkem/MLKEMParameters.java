package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.KEMParameters;

@Deprecated
public class MLKEMParameters implements KEMParameters {
   public static final MLKEMParameters ml_kem_512 = new MLKEMParameters("ML-KEM-512", 2);
   public static final MLKEMParameters ml_kem_768 = new MLKEMParameters("ML-KEM-768", 3);
   public static final MLKEMParameters ml_kem_1024 = new MLKEMParameters("ML-KEM-1024", 4);
   private final String name;
   private final MLKEMEngine engine;

   private MLKEMParameters(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("'name' cannot be null");
      }

      this.name = var1;
      this.engine = new MLKEMEngine(var2);
   }

   MLKEMEngine getEngine() {
      return this.engine;
   }

   public int getEncapsulationLength() {
      return this.engine.getCipherTextBytes();
   }

   public String getName() {
      return this.name;
   }

   public int getSessionKeySize() {
      return 256;
   }
}
