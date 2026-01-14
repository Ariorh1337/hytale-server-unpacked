package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusPublicKeyParameters {
   private final byte[][] publicKey;

   protected WOTSPlusPublicKeyParameters(WOTSPlusParameters var1, byte[][] var2) {
      if (var1 == null) {
         throw new NullPointerException("params == null");
      }

      if (var2 == null) {
         throw new NullPointerException("publicKey == null");
      }

      if (XMSSUtil.hasNullPointer(var2)) {
         throw new NullPointerException("publicKey byte array == null");
      }

      if (var2.length != var1.getLen()) {
         throw new IllegalArgumentException("wrong publicKey size");
      }

      for (int var3 = 0; var3 < var2.length; var3++) {
         if (var2[var3].length != var1.getTreeDigestSize()) {
            throw new IllegalArgumentException("wrong publicKey format");
         }
      }

      this.publicKey = XMSSUtil.cloneArray(var2);
   }

   protected byte[][] toByteArray() {
      return XMSSUtil.cloneArray(this.publicKey);
   }
}
