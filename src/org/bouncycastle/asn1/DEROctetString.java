package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class DEROctetString extends ASN1OctetString {
   public static final DEROctetString EMPTY = new DEROctetString(EMPTY_OCTETS);

   public static DEROctetString fromContents(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("'contents' cannot be null");
      } else {
         return internalFromContents(var0);
      }
   }

   public static DEROctetString fromContentsOptional(byte[] var0) {
      return var0 == null ? null : internalFromContents(var0);
   }

   public static DEROctetString withContents(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("'contents' cannot be null");
      } else {
         return internalWithContents(var0);
      }
   }

   public static DEROctetString withContentsOptional(byte[] var0) {
      return var0 == null ? null : internalWithContents(var0);
   }

   static DEROctetString internalFromContents(byte[] var0) {
      return var0.length < 1 ? EMPTY : new DEROctetString(Arrays.clone(var0));
   }

   static DEROctetString internalWithContents(byte[] var0) {
      return var0.length < 1 ? EMPTY : new DEROctetString(var0);
   }

   public DEROctetString(byte[] var1) {
      super(var1);
   }

   public DEROctetString(ASN1Encodable var1) throws IOException {
      super(var1.toASN1Primitive().getEncoded("DER"));
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.string.length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 4, this.string);
   }

   @Override
   ASN1Primitive toDERObject() {
      return this;
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }

   static void encode(ASN1OutputStream var0, boolean var1, byte[] var2, int var3, int var4) throws IOException {
      var0.writeEncodingDL(var1, 4, var2, var3, var4);
   }

   static int encodedLength(boolean var0, int var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var0, var1);
   }
}
