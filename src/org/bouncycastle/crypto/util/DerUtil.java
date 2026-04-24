package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

class DerUtil {
   static ASN1OctetString getOctetString(byte[] var0) {
      return var0 == null ? new DEROctetString(new byte[0]) : new DEROctetString(Arrays.clone(var0));
   }

   static byte[] toByteArray(ASN1Primitive var0) {
      try {
         return var0.getEncoded();
      } catch (IOException var2) {
         throw Exceptions.illegalStateException("Cannot get encoding: " + var2.getMessage(), var2);
      }
   }
}
