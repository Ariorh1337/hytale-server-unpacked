package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class BEROctetString extends ASN1OctetString {
   public static final BEROctetString EMPTY = new BEROctetString(EMPTY_OCTETS);
   private static final int DEFAULT_SEGMENT_LIMIT = 1000;
   private final int segmentLimit;
   private final ASN1OctetString[] elements;

   public static BEROctetString fromContents(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("'contents' cannot be null");
      } else {
         return internalFromContents(var0);
      }
   }

   public static BEROctetString fromContentsOptional(byte[] var0) {
      return var0 == null ? null : internalFromContents(var0);
   }

   public static BEROctetString withContents(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("'contents' cannot be null");
      } else {
         return internalWithContents(var0);
      }
   }

   public static BEROctetString withContentsOptional(byte[] var0) {
      return var0 == null ? null : internalWithContents(var0);
   }

   static byte[] flattenOctetStrings(ASN1OctetString[] var0) {
      int var1 = var0.length;
      switch (var1) {
         case 0:
            return EMPTY_OCTETS;
         case 1:
            return var0[0].string;
         default:
            int var2 = 0;

            for (int var3 = 0; var3 < var1; var3++) {
               var2 += var0[var3].string.length;
            }

            byte[] var7 = new byte[var2];
            int var4 = 0;
            int var5 = 0;

            while (var4 < var1) {
               byte[] var6 = var0[var4].string;
               System.arraycopy(var6, 0, var7, var5, var6.length);
               var5 += var6.length;
               var4++;
            }

            return var7;
      }
   }

   static BEROctetString internalFromContents(byte[] var0) {
      return var0.length < 1 ? EMPTY : new BEROctetString(Arrays.clone(var0));
   }

   static BEROctetString internalWithContents(byte[] var0) {
      return var0.length < 1 ? EMPTY : new BEROctetString(var0);
   }

   public BEROctetString(byte[] var1) {
      this(var1, 1000);
   }

   public BEROctetString(ASN1OctetString[] var1) {
      this(var1, 1000);
   }

   public BEROctetString(byte[] var1, int var2) {
      this(var1, null, var2);
   }

   public BEROctetString(ASN1OctetString[] var1, int var2) {
      this(flattenOctetStrings(var1), var1, var2);
   }

   private BEROctetString(byte[] var1, ASN1OctetString[] var2, int var3) {
      super(var1);
      this.elements = var2;
      this.segmentLimit = var3;
   }

   @Override
   boolean encodeConstructed() {
      return true;
   }

   @Override
   int encodedLength(boolean var1) throws IOException {
      int var2 = var1 ? 4 : 3;
      if (null != this.elements) {
         for (int var3 = 0; var3 < this.elements.length; var3++) {
            var2 += this.elements[var3].encodedLength(true);
         }
      } else {
         int var5 = this.string.length / this.segmentLimit;
         var2 += var5 * DEROctetString.encodedLength(true, this.segmentLimit);
         int var4 = this.string.length - var5 * this.segmentLimit;
         if (var4 > 0) {
            var2 += DEROctetString.encodedLength(true, var4);
         }
      }

      return var2;
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeIdentifier(var2, 36);
      var1.write(128);
      if (null != this.elements) {
         var1.writePrimitives(this.elements);
      } else {
         int var3 = 0;

         while (var3 < this.string.length) {
            int var4 = Math.min(this.string.length - var3, this.segmentLimit);
            DEROctetString.encode(var1, true, this.string, var3, var4);
            var3 += var4;
         }
      }

      var1.write(0);
      var1.write(0);
   }
}
